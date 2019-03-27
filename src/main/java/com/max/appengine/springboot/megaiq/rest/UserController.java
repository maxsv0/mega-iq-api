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

package com.max.appengine.springboot.megaiq.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiRequestForget;
import com.max.appengine.springboot.megaiq.model.api.ApiRequestLoginToken;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiUserPublic;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.exception.MegaIQException;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.FirebaseService;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import com.max.appengine.springboot.megaiq.service.UserService;

@RestController
public class UserController extends AbstractApiController {
  public static final String MESSAGE_REGISTRATION_FAILED = "message_registration_failed";

  public static final String MESSAGE_LOGIN_FAILED = "message_login_failed";

  public static final String MESSAGE_USER_NOT_FOUND = "message_user_not_found";

  public static final String MESSAGE_VERIFY_EMAIL_SEND = "message_verify_email_send";

  public static final String MESSAGE_VERIFY_SUCCESS = "message_verify_success";

  public static final String MESSAGE_EMAIL_ALREADY_USED = "message_email_already_used";

  public static final String MESSAGE_EMAIL_FORGET_WAS_SENT = "message_email_forget_was_sent";

  public static final String MESSAGE_INVALID_ACCESS = "message_invalid_access";

  public static final String MESSAGE_WRONG_REQUEST = "message_wrong_request";

  public static final String MESSAGE_INTERNAL_ERROR = "message_internal_error";

  private static final Logger log = Logger.getLogger(UserController.class.getName());

  private final UserService userService;

  private final TestResultService testResultService;

  private final EmailService emailService;

  private final FirebaseService firebaseService;

  private final Table<String, Locale, String> configCache = HashBasedTable.create();

  @Autowired
  public UserController(TestResultService testResultService, UserService userService,
      EmailService emailService, FirebaseService firebaseService,
      ConfigurationService configurationService) {
    this.userService = userService;
    this.testResultService = testResultService;
    this.emailService = emailService;
    this.firebaseService = firebaseService;

    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_REGISTRATION_FAILED);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_LOGIN_FAILED);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_USER_NOT_FOUND);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_VERIFY_EMAIL_SEND);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_VERIFY_SUCCESS);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_EMAIL_ALREADY_USED);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_EMAIL_FORGET_WAS_SENT);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_INVALID_ACCESS);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_WRONG_REQUEST);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_INTERNAL_ERROR);
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestNewUser(HttpServletRequest request,
      @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);
    if (token.isPresent()) {
      try {
        FirebaseToken firebaseToken = firebaseService.checkToken(token.get());
        Optional<User> userCurrentResult = userService.getUserByUid(firebaseToken.getUid());

        if (userCurrentResult.isPresent()) {
          User user = userCurrentResult.get();

          // check for changes
          if (firebaseToken.isEmailVerified() != user.getIsEmailVerified()
              || firebaseToken.getPicture() != user.getPic()) {

            user.setIsEmailVerified(firebaseToken.isEmailVerified());
            user.setPic(firebaseToken.getPicture());

            user = userService.saveUser(user);
          }
          user.setToken(token.get());

          return sendResponseUser(user, userLocale);
        } else {
          return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
        }
      } catch (FirebaseAuthException error) {
        return sendResponseErrorRaw(error.getLocalizedMessage(), userLocale);
      }
    } else {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }
  }

  @RequestMapping(value = "/user/new", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ApiResponseBase> requestNewUser(HttpServletRequest request,
      @RequestBody User user, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    user.setIp(getIp(request));
    user.setLocale(userLocale);
    user.setIsEmailVerified(false);
    user.setIsPublic(true);

    try {
      UserRecord userRecord = firebaseService.createUser(user);
      user.setUid(userRecord.getUid());

      User userResult = userService.addUser(user);

      return sendResponseUser(userResult, userLocale);
    } catch (MegaIQException error) {
      String message = getCacheValue(configCache, MESSAGE_EMAIL_ALREADY_USED, userLocale);
      message = String.format(message, user.getEmail());

      return sendResponseErrorRaw(message, userLocale);
    } catch (FirebaseAuthException error) {
      return sendResponseErrorRaw(error.getLocalizedMessage(), userLocale);
    }
  }

  @RequestMapping(value = "/user/loginToken", method = RequestMethod.POST,
      consumes = "application/json")
  public ResponseEntity<ApiResponseBase> requestUserLoginWithToken(
      @RequestBody ApiRequestLoginToken requestLoginToken, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);
    Optional<User> userResult = userService.getUserByToken(requestLoginToken.getToken());

    if (userResult.isPresent()) {
      return sendResponseUser(userResult.get(), userLocale);
    } else {
      return sendResponseError(MESSAGE_LOGIN_FAILED, configCache, userLocale);
    }
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestUserById(HttpServletRequest request,
      @PathVariable Integer userId, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    Optional<User> userResult = userService.getUserById(userId);
    if (userResult.isPresent()) {
      List<TestResult> listResults = this.testResultService.findByUserId(userId, userLocale);
      userResult.get().setTestResultList(listResults);

      if (userResult.get().getIsPublic()) {
        return sendResponseUserPublic(userResult.get(), userLocale);
      } else {
        return sendResponseError(MESSAGE_USER_NOT_FOUND, configCache, userLocale);
      }
    } else {
      return sendResponseError(MESSAGE_USER_NOT_FOUND, configCache, userLocale);
    }
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<ApiResponseBase> updateUserById(HttpServletRequest request,
      @PathVariable Integer userId, @RequestBody User user, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> userCurrentResult = userService.getUserByToken(token.get());
    if (!userCurrentResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    User userCurrent = userCurrentResult.get();
    if (!userCurrent.getId().equals(userId)) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    // validate user input
    if (!userCurrent.getId().equals(user.getId())) {
      return sendResponseError(MESSAGE_WRONG_REQUEST, configCache, userLocale);
    }

    // email change
    if (!userCurrent.getEmail().equals(user.getEmail())) {
      userCurrent.setIsEmailVerified(false);
      userCurrent.setEmail(user.getEmail());
    }

    userCurrent.setLocale(userLocale);
    userCurrent.setName(user.getName());
    userCurrent.setAge(user.getAge());
    userCurrent.setLocation(user.getLocation());
    userCurrent.setPic(user.getPic());
    userCurrent.setIsPublic(user.getIsPublic());

    userCurrent.setIp(getIp(request));

    try {
      firebaseService.saveUser(userCurrent);

      User userResult = this.userService.saveUser(userCurrent);
      return sendResponseUser(userResult, userLocale);
    } catch (DataIntegrityViolationException exeption) {
      String message = getCacheValue(configCache, MESSAGE_EMAIL_ALREADY_USED, userLocale);
      message = String.format(message, userCurrent.getEmail());

      return sendResponseErrorRaw(message, userLocale);
    } catch (FirebaseAuthException exeption) {
      return sendResponseErrorRaw(exeption.getMessage(), userLocale);
    }
  }

  @RequestMapping(value = "/user/top", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> getUsersTop(@RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);
    List<User> usersList = this.userService.getUsersListTopToday(userLocale);

    List<ApiUserPublic> usersPublicList = new ArrayList<ApiUserPublic>();
    for (User user : usersList) {
      usersPublicList.add(new ApiUserPublic(user));
    }
    return sendResponseUsersList(usersPublicList, this.testResultService.getResultCount(),
        userLocale);
  }

  @RequestMapping(value = "/user/list", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> getUsersList(@RequestParam Optional<String> locale,
      Optional<Integer> page) {
    Locale userLocale = loadLocale(locale);
    List<User> usersList = this.userService.getUsersListTopMonth(userLocale, page);

    List<ApiUserPublic> usersPublicList = new ArrayList<ApiUserPublic>();
    for (User user : usersList) {
      usersPublicList.add(new ApiUserPublic(user));
    }
    return sendResponseUsersList(usersPublicList, this.testResultService.getResultCount(),
        userLocale);
  }

  @RequestMapping(value = "/user/forget", method = RequestMethod.POST)
  public ResponseEntity<ApiResponseBase> forgetPassword(@RequestBody ApiRequestForget request,
      @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    Optional<User> userResult = userService.getUserByEmail(request.getEmail());
    if (!userResult.isPresent()) {
      return sendResponseError(MESSAGE_WRONG_REQUEST, configCache, userLocale);
    }

    String url;
    try {
      url = firebaseService.getPasswordResetLink(userResult.get().getEmail());
    } catch (FirebaseAuthException e) {
      return sendResponseError(MESSAGE_INTERNAL_ERROR, configCache, userLocale);
    }
    boolean resultEmail = emailService.sendEmailForget(userResult.get(), url);

    log.log(Level.INFO,
        "Sending forget email to a userID=" + userResult.get().getId() + ". Result={0}",
        resultEmail);

    return sendResponseBase(MESSAGE_EMAIL_FORGET_WAS_SENT, configCache, userLocale);
  }

  @RequestMapping(value = "/user/verify", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> sendVerifyEmail(HttpServletRequest request,
      @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    Optional<String> token = getTokenFromHeader(request);
    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> userCurrentResult = userService.getUserByToken(token.get());
    if (!userCurrentResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    User userCurrent = userCurrentResult.get();
    if (userCurrent.getIsEmailVerified()) {
      return sendResponseBase(MESSAGE_VERIFY_SUCCESS, configCache, userLocale);
    }

    String url;
    try {
      url = firebaseService.getEmailVerificationLink(userCurrent.getEmail());
    } catch (FirebaseAuthException e) {
      return sendResponseError(MESSAGE_INTERNAL_ERROR, configCache, userLocale);
    }
    boolean result = emailService.sendEmailVerify(userCurrent, url);

    log.log(Level.INFO,
        "Sending email verify URL=" + url + " to a userID=" + userCurrent.getId() + ". Result={0}",
        result);

    if (result) {
      return sendResponseBase(MESSAGE_VERIFY_EMAIL_SEND, configCache, userLocale);
    } else {
      return sendResponseError(MESSAGE_INTERNAL_ERROR, configCache, userLocale);
    }
  }

  @RequestMapping(value = "/user/verify", method = RequestMethod.POST)
  public ResponseEntity<ApiResponseBase> verifyEmailUpdate(HttpServletRequest request,
      @RequestBody String code, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    Optional<String> token = getTokenFromHeader(request);
    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> userCurrentResult = userService.getUserByToken(token.get());
    if (!userCurrentResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> userVerifyResult = userService.getUserByToken(code);
    if (!userVerifyResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    User userVerify = userVerifyResult.get();

    User userCurrent = userCurrentResult.get();
    if (!userCurrent.getId().equals(userVerify.getId())) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    userVerify.setIsEmailVerified(true);
    this.userService.saveUser(userVerify);

    return sendResponseBase(MESSAGE_VERIFY_SUCCESS, configCache, userLocale);
  }

}
