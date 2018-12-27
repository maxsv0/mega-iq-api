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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.repository.UserTokenReporitory;

@Service
public class UserService {
  public static final Integer LIMIT_LIST_PAGE = 15;
  
  public static final Integer LIMIT_HOME_PAGE = 8;

  private final UserReporitory userReporitory;

  private final UserTokenReporitory userTokenReporitory;

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired
  public UserService(UserReporitory userReporitory, UserTokenReporitory userTokenReporitory) {
    this.userReporitory = userReporitory;
    this.userTokenReporitory = userTokenReporitory;
  }

  public List<User> getUsersListTopMonth(Locale locale, Optional<Integer> page) {
    int currentPage = 0;
    if (page.isPresent() && page.get() > 0) {
      currentPage = page.get();
    }
    
    return loadUsersList(locale, 30, PageRequest.of(currentPage, LIMIT_LIST_PAGE));
  }

  public List<User> getUsersListTopToday(Locale locale) {
    return loadUsersList(locale, 1, PageRequest.of(0, LIMIT_HOME_PAGE));
  }

  public Optional<User> addUser(User user) {
    User userResult = null;

    Optional<User> userData = userReporitory.findByEmail(user.getEmail());
    if (userData.isPresent()) {
      return Optional.empty();
    }

    user.setPassword(convertPassowrdToHash(user.getPassword()));
    user.setCreateDate(new Date());
    userResult = userReporitory.save(user);

    userResult.setUrl("/user/" + userResult.getId());
    userReporitory.save(userResult);

    userResult = initUserTokens(userResult);

    return Optional.of(userResult);
  }

  public User saveUser(User user) {
    user.setUpdateDate(new Date());

    /// userTokenReporitory.saveAll(user.getTokenList()); TODO: remove
    return userReporitory.save(user);
  }

  public Optional<User> getUserById(Integer userId) {
    Optional<User> userResult = userReporitory.findById(userId);
    if (!userResult.isPresent()) {
      return userResult;
    }

    User user = loadUserToken(userResult.get());
    return Optional.of(user);
  }

  public Optional<User> getUserByToken(String token, UserTokenType tokenType) {
    Optional<UserToken> userToken = userTokenReporitory.findByValueAndType(token, tokenType);
    log.debug("Try to auth. Token={} type={}, userToken={}", token, tokenType, userToken);

    if (!userToken.isPresent()) {
      return Optional.empty();
    }

    return getUserById(userToken.get().getUserId());
  }

  public Optional<User> authUserLogin(String login, String password) {
    Optional<User> userResult = userReporitory.findByEmail(login);
    log.debug("Search for user login={}. Result={}", login, userResult);

    if (!userResult.isPresent()) {
      return Optional.empty();
    }

    String hashString = convertPassowrdToHash(password);
    log.debug("Got hash={}", hashString);
    User user = userResult.get();

    if (!user.getPassword().equals(hashString)) {
      return Optional.empty();
    }

    user = initUserTokens(user);
    log.debug("Auth successful for user={}", user);

    return Optional.of(user);
  }

  private User initUserTokens(User user) {
    user = loadUserToken(user);

    UserToken tokenAccess = user.getUserTokenByType(UserTokenType.ACCESS);
    if (tokenAccess == null) {
      UserToken tokenAccessNew = new UserToken(user.getId(), UserTokenType.ACCESS);
      user.getTokenList().add(tokenAccessNew);

      userTokenReporitory.save(tokenAccessNew);
    }
    return user;
  }

  private User loadUserToken(User user) {
    List<UserToken> tokenList = userTokenReporitory.findByUserId(user.getId());

    if (!tokenList.isEmpty()) {
      user.setTokenList(tokenList);
    } else {
      user.setTokenList(new ArrayList<UserToken>());
    }

    return user;
  }

  private String convertPassowrdToHash(String password) {
    String hashString = null;

    byte[] bytesPassword = password.getBytes(StandardCharsets.UTF_8);
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashPassword = md.digest(bytesPassword);
      StringBuilder sb = new StringBuilder(2 * hashPassword.length);
      for (byte b : hashPassword) {
        sb.append(String.format("%02x", b & 0xff));
      }
      hashString = sb.toString().toLowerCase();

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return hashString;
  }
  
  private List<User> loadUsersList(Locale locale, Integer period, Pageable pageRequest) {
    LocalDate dateLocal = LocalDate.now().minusDays(period);
    Date date = Date.from(dateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

    return userReporitory
        .findByLocaleAndCreateDateAfterAndIsPublicIsTrueAndIqIsNotNullOrderByIqDesc(locale,
            date, pageRequest);
  }
}
