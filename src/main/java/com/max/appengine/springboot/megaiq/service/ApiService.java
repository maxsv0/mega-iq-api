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

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Service
public class ApiService {

  // reps:
  // userTestResult
  // questionSet
  // users


  public ResponseEntity<ApiResponseBase> index(HttpServletRequest request) {
    ApiResponseBase result = new ApiResponseBase();

    return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
  }


  public ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {

    TestResult resultData = loadFullResultData(testCode, locale);
    if (resultData == null) {
      ApiResponseBase result = new ApiResponseError("Wrong request");

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    }
    
    ApiTestResult testResult = new ApiTestResult(resultData, false);

    ApiResponseBase result = new ApiResponseTestResult(testResult);

    return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
  }

  public ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    TestResult resultData = loadFullResultData(testCode, locale);
    if (resultData == null) {
      ApiResponseBase result = new ApiResponseError("Wrong request");

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    }
    
    ApiTestResult testResult = new ApiTestResult(resultData, true);
   
    // private result can be requested only be user himself
    if (!user.getId().equals(resultData.getUserId())) {
      ApiResponseBase result = new ApiResponseError("Wrong token");

      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    } else {
      ApiResponseBase result = new ApiResponseTestResult(testResult);
      
      return new ResponseEntity<ApiResponseBase>(result, HttpStatus.OK);
    }
  }

  private TestResult loadFullResultData(UUID testCode, Locale locale) {
    TestResult testResult = new TestResult();

    // TODO: load from rep
    testResult.setCode(testCode);
    testResult.setLocale(locale);
    testResult.setUser(getUserById(testResult.getUserId()));

    return testResult;
  }

  private User getUserById(Integer userId) {
    User user = new User();

    // TODO: load from rep
    user.setId(userId);

    return user;
  }

}
