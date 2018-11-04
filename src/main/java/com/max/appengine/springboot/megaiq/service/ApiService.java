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

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Service
public class ApiService {

  private final QuestionsService qestionsService;
  private final TestResultService testResultService;
  private final UserService userService;

  @Autowired
  public ApiService(QuestionsService qestionsService, TestResultService testResultService,
      UserService userService) {
    super();

    this.qestionsService = qestionsService;
    this.testResultService = testResultService;
    this.userService = userService;
  }



  public ResponseEntity<ApiResponseBase> index(HttpServletRequest request) {
    ApiResponseBase result = new ApiResponseBase();

    return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
  }

  public ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {

    Optional<TestResult> resultData = loadFullResultData(testCode);
    if (!resultData.isPresent()) {
      ApiResponseBase result = new ApiResponseError("Wrong request");

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    }

    ApiTestResult testResult = new ApiTestResult(resultData.get(), false);

    ApiResponseBase result = new ApiResponseTestResult(testResult);

    return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
  }

  public ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    Optional<TestResult> resultData = loadFullResultData(testCode);
    if (!resultData.isPresent()) {
      ApiResponseBase result = new ApiResponseError("Wrong request");

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    }

    ApiTestResult testResult = new ApiTestResult(resultData.get(), true);

    // private result can be requested only be user himself
    if (!user.getId().equals(resultData.get().getUserId())) {
      ApiResponseBase result = new ApiResponseError("Wrong token");

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    } else {
      ApiResponseBase result = new ApiResponseTestResult(testResult);

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    }
  }

  public ResponseEntity<ApiResponseBase> userLogin(String login, String password) {
    ApiResponseBase result = null;
    
    Optional<User> userResult = authUserLogin(login, password);
    if (userResult.isPresent()) {
      result = new ApiResponseUser(userResult.get());
    } else {
      result = new ApiResponseError("Login failed");
    }

    return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
  }

  private Optional<TestResult> loadFullResultData(UUID testCode) {
    return testResultService.getTestResultByCode(testCode);
  }

  private Optional<User> authUserLogin(String login, String password) {
    return userService.authUserLogin(login, password);
  }
  
  private Optional<User> getUserById(Integer userId) {
    return userService.getUserById(userId);
  }
}
