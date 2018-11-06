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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.repository.UserTokenReporitory;

@Service
public class UserService {
  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final UserReporitory userReporitory;
  private final UserTokenReporitory userTokenReporitory;

  @Autowired
  public UserService(UserReporitory userReporitory, UserTokenReporitory userTokenReporitory) {
    this.userReporitory = userReporitory;
    this.userTokenReporitory = userTokenReporitory;
  }

  public User addUser(User user) {
    return userReporitory.save(user);
  }
  
  public User saveUser(User user) {
    userTokenReporitory.saveAll(user.getTokenList());
    return userReporitory.save(user);
  }
  
  public Optional<User> getUserById(Integer userId) {
    Optional<User> userResult = userReporitory.findById(userId);
    if (!userResult.isPresent()) return userResult;
    
    User user = loadUserToken(userResult.get());
    return Optional.of(user);
  }

  public Optional<User> getUserByToken(String token, UserTokenType tokenType) {
    Optional<UserToken> userToken = userTokenReporitory.findByValueAndType(token, tokenType);
    log.debug("Try to auth. Token={} type={}, userToken={}", token, tokenType, userToken);
    
    if (!userToken.isPresent())
      return Optional.empty();
    
    return getUserById(userToken.get().getUserId());
  }

  public Optional<User> authUserLogin(String login, String password) {
    Optional<User> userResult = userReporitory.findByEmail(login);
    log.debug("Search for user login={}. Result={}", login, userResult);

    if (!userResult.isPresent())
      return Optional.empty();

    String hashString = null;

    try {
      byte[] bytesPassword = password.getBytes(StandardCharsets.UTF_8);
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashPassword = md.digest(bytesPassword);
      StringBuilder sb = new StringBuilder(2 * hashPassword.length);
      for (byte b : hashPassword) {
        sb.append(String.format("%02x", b & 0xff));
      }
      hashString = sb.toString().toLowerCase();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return Optional.empty();
    }

    log.debug("Got hash={}", hashString);
    User user = userResult.get();

    if (!user.getPassword().equals(hashString)) {
      return Optional.empty();
    }

    user = loadUserToken(user);
    log.debug("Auth successfull for user={}", user);

    UserToken tokenAccess = user.getUserTokenByType(UserTokenType.ACCESS);
    if (tokenAccess == null) {
      UserToken tokenAccessNew = new UserToken(user.getId(), UserTokenType.ACCESS);
      user.getTokenList().add(tokenAccessNew);
      
      userTokenReporitory.save(tokenAccessNew);
    }

    return Optional.of(user);
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

}
