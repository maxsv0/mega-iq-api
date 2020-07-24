/*
 * Copyright 2018 mega-iq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.max.appengine.springboot.megaiq.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.exception.MegaIQException;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;

@Service
public class UserService {
  public static final Integer LIMIT_LIST_PAGE = 15;

  public static final Integer LIMIT_HOME_PAGE = 5;

  private final UserReporitory userReporitory;

  private final FirebaseService firebaseService;

  private final EmailService emailService;

  private final CertificateService certificateService;

  @Autowired
  public UserService(UserReporitory userReporitory, FirebaseService firebaseService,
      EmailService emailService, CertificateService certificateService) {
    this.userReporitory = userReporitory;
    this.firebaseService = firebaseService;
    this.emailService = emailService;
    this.certificateService = certificateService;
  }

  public List<User> getUsersListTopMonth(Locale locale, Optional<Integer> page) {
    int currentPage = 0;
    if (page.isPresent() && page.get() > 0) {
      currentPage = page.get();
    }

    // TODO: fix it later. for now 300 days
    return loadUsersByPeriod(locale, 300, PageRequest.of(currentPage, LIMIT_LIST_PAGE));
  }

  public List<User> getUsersListTopToday(Locale locale) {
    return loadUsersByPeriod(locale, 150, PageRequest.of(0, LIMIT_HOME_PAGE));
  }

  public User addUser(User user) throws MegaIQException, FirebaseAuthException {
    User userResult = null;

    Optional<User> userData = userReporitory.findByEmail(user.getEmail());
    if (userData.isPresent()) {
      throw new MegaIQException(Level.SEVERE, "User already exists");
    }

    user.setIsUnsubscribed(false);
    if (user.getCreateDate() == null) {
      user.setCreateDate(new Date());
    }

    // set random images
    if (user.getPic() == null) {
      user.setPic(AbstractServiceHelper.getRandomUserAvatar());
    }

    if (user.getBackground() == null) {
      user.setBackground(getRandomUserBackground());
    }

    // in case id not set, save to get ID
    if (user.getId() == null) {
      userResult = userReporitory.save(user);

      userResult.setUrl("/user/" + userResult.getId());

      if (user.getIsEmailVerified()) {
        emailService.sendEmailRegistration(userResult);
      } else {
        String link = firebaseService.getEmailVerificationLink(userResult.getEmail());
        emailService.sendEmailRegistrationWithVerify(userResult, link);
      }
    } else {
      user.setUrl("/user/" + user.getId());

      emailService.sendRegistrationImportUser(user);
    }

    return userReporitory.save(user);
  }

  public User saveUser(User user) {
    user.setUpdateDate(new Date());

    return userReporitory.save(user);
  }

  public Optional<User> getUserById(Integer userId) {
    return userReporitory.findById(userId);
  }

  public Optional<User> getUserByUid(String uId) {
    return userReporitory.findByUid(uId);
  }

  public Optional<User> getLastProfile(Locale locale) {
    return userReporitory.findOneByIqGreaterThanAndLocaleAndIsPublicIsTrueOrderByUpdateDate(125,
        locale);
  }

  public List<User> getLastProfiles(Locale locale) {
    return userReporitory.findTop5ByIqGreaterThanAndLocaleAndIsPublicIsTrueOrderByUpdateDate(90,
        locale);
  }

  public Optional<User> getUserByEmail(String email) {
    Optional<User> userResult = userReporitory.findByEmail(email);
    if (!userResult.isPresent()) {
      return userResult;
    }

    return Optional.of(userResult.get());
  }

  public Optional<User> getUserByTokenOrRegister(String token, String ip, Locale locale)
      throws MegaIQException, FirebaseAuthException {
    FirebaseToken firebaseToken = null;
    try {
      firebaseToken = firebaseService.checkToken(token);
    } catch (FirebaseAuthException e) {
      return Optional.empty();
    }

    Optional<User> userResult = getUserByUid(firebaseToken.getUid());

    User user = null;
    if (userResult.isPresent()) {
      user = userResult.get();
    } else {
      user = new User();

      //
      UserRecord userRecord = firebaseService.getUserByUid(firebaseToken.getUid());

      user.setSource("social-login");
      user.setUid(firebaseToken.getUid());

      if (userRecord.getEmail() != null) {
        user.setEmail(userRecord.getEmail());
        user.setIsEmailVerified(userRecord.isEmailVerified());
      } else {
        /// search for email in provider data
        for (UserInfo userInfo : userRecord.getProviderData()) {
          if (userInfo.getEmail() != null) {

            user.setEmail(userInfo.getEmail());
            user.setIsEmailVerified(true);

            break;
          }
        }

      }

      user.setName(userRecord.getDisplayName());
      user.setPic(userRecord.getPhotoUrl());
      user.setIsPublic(true);
      user.setLocale(locale);
      user.setIp(ip);

      // save to locale DB
      user = addUser(user);
    }

    return Optional.of(user);
  }

  public Optional<User> getUserByToken(String token) {
    FirebaseToken firebaseToken = null;
    try {
      firebaseToken = firebaseService.checkToken(token);
    } catch (FirebaseAuthException e) {
      return Optional.empty();
    }

    Optional<User> userResult = getUserByUid(firebaseToken.getUid());

    if (userResult.isPresent()) {
      User user = userResult.get();
      user.setToken(token);

      return Optional.of(user);
    } else {
      return Optional.empty();
    }

  }

  public List<User> findByUserIdIn(List<Integer> userIds) {
    return this.userReporitory.findByIdIn(userIds);
  }

  public User setIqScoreAndCertificate(User user, Integer points) {
    user.setIq(points);

    try {
      String certificate = this.certificateService.createUserCertificate(user);

      user.setCertificate(certificate);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return this.saveUser(user);
  }

  private List<User> loadUsersByPeriod(Locale locale, Integer period, Pageable pageRequest) {
    LocalDate dateLocal = LocalDate.now().minusDays(period);
    Date date = Date.from(dateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

    return userReporitory
        .findByLocaleAndUpdateDateAfterAndIsPublicIsTrueAndIqIsNotNullOrderByIqDesc(locale, date,
            pageRequest);
  }


  private String getRandomUserBackground() {
    Random rand = new Random();
    return "custom-bg" + (rand.nextInt(10) + 1); // 1 to 10
  }

}
