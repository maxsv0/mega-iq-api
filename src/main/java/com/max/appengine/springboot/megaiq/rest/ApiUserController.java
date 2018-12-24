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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiRequestLogin;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiUser;
import com.max.appengine.springboot.megaiq.model.api.ApiUserPublic;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.ApiService;
import com.max.appengine.springboot.megaiq.service.UserService;

@RestController
public class ApiUserController extends AbstractApiController {
  public static final String MESSAGE_REGISTRATION_FAILED = "Registration failed. Please try again";

  public static final String MESSAGE_LOGIN_FAILED = "Wrong login or password";

  public static final String MESSAGE_USER_NOT_FOUND = "User not found or profile is private";

  private final ApiService serviceApi;

  private final UserService userService;

  @Autowired
  public ApiUserController(ApiService service, UserService userService) {
    this.serviceApi = service;
    this.userService = userService;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestNewUser(HttpServletRequest request,
      @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    Optional<String> token = getTokenFromHeader(request);
    if (token.isPresent()) {
      Optional<User> userCurrentResult = serviceApi.getUserByToken(token.get(), userLocale);
      
      if (!userCurrentResult.isPresent()) {
        return sendResponseError(MESSAGE_INVALID_ACCESS);
      } else {
        return sendResponseUser(new ApiUser(userCurrentResult.get()));
      }
    } else {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }
  }
  
  @RequestMapping(value = "/user/new", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ApiResponseBase> requestNewUser(HttpServletRequest request,
      @RequestBody User user, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    user.setIp(getIp(request));
    Optional<User> userResult = serviceApi.addNewUser(user, userLocale);

    if (userResult.isPresent()) {
      return sendResponseUser(new ApiUser(userResult.get()));
    } else {
      return sendResponseError(MESSAGE_REGISTRATION_FAILED);
    }
  }

  @RequestMapping(value = "/user/login", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ApiResponseBase> requestUserLogin(
      @RequestBody ApiRequestLogin requestLogin) {
    Optional<User> userResult =
        serviceApi.userLogin(requestLogin.getLogin(), requestLogin.getPassword());

    if (userResult.isPresent()) {
      return sendResponseUser(new ApiUser(userResult.get()));
    } else {
      return sendResponseError(MESSAGE_LOGIN_FAILED);
    }
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestUserById(HttpServletRequest request,
      @PathVariable Integer userId, @RequestParam Optional<String> locale) {
    Optional<User> userResult = serviceApi.getUserById(userId);

    if (userResult.isPresent()) {
      if (userResult.get().getIsPublic()) {
        return sendResponseUser(new ApiUserPublic(userResult.get()));
      } else {
        return sendResponseError(MESSAGE_USER_NOT_FOUND);
      }
    } else {
      return sendResponseError(MESSAGE_USER_NOT_FOUND);
    }
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<ApiResponseBase> updateUserById(HttpServletRequest request,
      @PathVariable Integer userId, @RequestBody User user, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);
    
    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }
    
    Optional<User> userCurrentResult = serviceApi.getUserByToken(token.get(), userLocale);
    if (!userCurrentResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    User userCurrent = userCurrentResult.get();
    if (!userCurrent.getId().equals(userId)) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    // validate user input
    if (!userCurrent.getId().equals(user.getId())) {
      return sendResponseError(MESSAGE_WRONG_REQUEST);
    }

    // email change
    if (!userCurrent.getEmail().equals(user.getEmail())) {
      userCurrent.setIsEmailVerified(false);
      userCurrent.setEmail(user.getEmail());
    }

    userCurrent.setName(user.getName());
    userCurrent.setAge(user.getAge());
    userCurrent.setLocation(user.getLocation());
    userCurrent.setPic(user.getPic());
    userCurrent.setIsPublic(user.getIsPublic());

    userCurrent.setIp(getIp(request));
    User userResult = this.userService.saveUser(userCurrent);
    return sendResponseUser(new ApiUser(userResult));
  }

  @RequestMapping(value = "/user/top", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> getUsersTop(@RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);
    List<User> usersList = this.userService.getUsersListTopToday(userLocale);

    List<ApiUserPublic> usersPublicList = new ArrayList<ApiUserPublic>();
    for (User user : usersList) {
      usersPublicList.add(new ApiUserPublic(user));
    }
    return sendResponseUsersList(usersPublicList);
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
    return sendResponseUsersList(usersPublicList);
  }

}
