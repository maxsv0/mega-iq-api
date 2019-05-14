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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.exception.MegaIQException;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;

@Service
public class UserService {
  public static final Integer LIMIT_LIST_PAGE = 15;

  public static final Integer LIMIT_HOME_PAGE = 8;

  private final UserReporitory userReporitory;

  private final FirebaseService firebaseService;

  private final EmailService emailService;
  
  @Autowired
  public UserService(UserReporitory userReporitory, FirebaseService firebaseService, EmailService emailService) {
    this.userReporitory = userReporitory;
    this.firebaseService = firebaseService;
    this.emailService = emailService;
  }

  public List<User> getUsersListTopMonth(Locale locale, Optional<Integer> page) {
    int currentPage = 0;
    if (page.isPresent() && page.get() > 0) {
      currentPage = page.get();
    }

    return loadUsersByPeriod(locale, 30, PageRequest.of(currentPage, LIMIT_LIST_PAGE));
  }

  public List<User> getUsersListTopToday(Locale locale) {
    return loadUsersByPeriod(locale, 1, PageRequest.of(0, LIMIT_HOME_PAGE));
  }

  public User addUser(User user) throws MegaIQException, FirebaseAuthException {
    User userResult = null;

    Optional<User> userData = userReporitory.findByEmail(user.getEmail());
    if (userData.isPresent()) {
      throw new MegaIQException(Level.SEVERE, "User already exists");
    }

    user.setIsUnsubscribed(false);
    user.setCreateDate(new Date());
    userResult = userReporitory.save(user);

    userResult.setUrl("/user/" + userResult.getId());
    userResult = userReporitory.save(userResult);

    if (user.getIsEmailVerified()) {
      emailService.sendEmailRegistration(userResult);
    } else {
      String link = firebaseService.getEmailVerificationLink(userResult.getEmail());
      emailService.sendEmailRegistrationWithVerify(userResult, link);
    }
    
    return userResult;
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

    User user =  null;
    if (userResult.isPresent()) {
      user = userResult.get();
    } else {
      user = new User();
      user.setUid(firebaseToken.getUid());
      user.setIsEmailVerified(firebaseToken.isEmailVerified());
      user.setEmail(firebaseToken.getEmail());
      user.setName(firebaseToken.getName());
      user.setPic(firebaseToken.getPicture());
      user.setIsPublic(true);
      user.setLocale(locale);
      user.setIp(ip);
      
      // save to locale DB
      user = addUser(user);
    }
    
    user.setToken(token);
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
    return this.userReporitory.findByUserIdIn(userIds);
  }

  // TODO: remove
  // private String convertPassowrdToHash(String password) {
  // String hashString = null;
  //
  // byte[] bytesPassword = password.getBytes(StandardCharsets.UTF_8);
  // try {
  // MessageDigest md = MessageDigest.getInstance("MD5");
  // byte[] hashPassword = md.digest(bytesPassword);
  // StringBuilder sb = new StringBuilder(2 * hashPassword.length);
  // for (byte b : hashPassword) {
  // sb.append(String.format("%02x", b & 0xff));
  // }
  // hashString = sb.toString().toLowerCase();
  //
  // } catch (NoSuchAlgorithmException e) {
  // e.printStackTrace();
  // }
  // return hashString;
  // }

  private List<User> loadUsersByPeriod(Locale locale, Integer period, Pageable pageRequest) {
    LocalDate dateLocal = LocalDate.now().minusDays(period);
    Date date = Date.from(dateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

    return userReporitory
        .findByLocaleAndUpdateDateAfterAndIsPublicIsTrueAndIqIsNotNullOrderByIqDesc(locale, date,
            pageRequest);
  }
}
