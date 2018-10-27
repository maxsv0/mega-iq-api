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
import com.max.appengine.springboot.megaiq.model.entity.EntityApiResponseBase;
import com.max.appengine.springboot.megaiq.model.entity.EntityApiResponseError;
import com.max.appengine.springboot.megaiq.model.entity.EntityApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.entity.EntityApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Service
public class ApiService {

  // reps:
  // userTestResult
  // questionSet
  // users


  public ResponseEntity<EntityApiResponseBase> index(HttpServletRequest request) {
    EntityApiResponseBase result = new EntityApiResponseBase();

    return new ResponseEntity<EntityApiResponseBase>(result, HttpStatus.OK);
  }


  public ResponseEntity<EntityApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {

    TestResult resultData = loadFullResultData(testCode, locale);
    if (resultData == null) {
      EntityApiResponseBase result = new EntityApiResponseError("Wrong request");

      return new ResponseEntity<EntityApiResponseBase>(result, HttpStatus.OK);
    }
    
    EntityApiTestResult testResult = new EntityApiTestResult(resultData, false);

    EntityApiResponseBase result = new EntityApiResponseTestResult(testResult);

    return new ResponseEntity<EntityApiResponseBase>(result, HttpStatus.OK);
  }

  public ResponseEntity<EntityApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    TestResult resultData = loadFullResultData(testCode, locale);
    if (resultData == null) {
      EntityApiResponseBase result = new EntityApiResponseError("Wrong request");

      return new ResponseEntity<EntityApiResponseBase>(result, HttpStatus.OK);
    }
    
    EntityApiTestResult testResult = new EntityApiTestResult(resultData, true);
   
    // private result can be requested only be user himself
    if (!user.getId().equals(resultData.getUserId())) {
      EntityApiResponseBase result = new EntityApiResponseError("Wrong token");

      return new ResponseEntity<EntityApiResponseBase>(result, HttpStatus.OK);
    } else {
      EntityApiResponseBase result = new EntityApiResponseTestResult(testResult);
      
      return new ResponseEntity<EntityApiResponseBase>(result, HttpStatus.OK);
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
