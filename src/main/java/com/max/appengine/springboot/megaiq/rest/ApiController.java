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

import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.ApiService;

@RestController
public class ApiController {
  protected static final Logger log = LoggerFactory.getLogger(ApiController.class);
  
  private final static Locale DEFAULT_LOCALE = Locale.EN;
  public static String BEARER_TYPE = "Bearer";
  
  private final ApiService serviceApi;
  
  //
  // pre-load:
  // questions DB
  // locales/text DB
  // GEOIP service
  //

  @Autowired
  public ApiController(ApiService service) {
    this.serviceApi = service;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> index(HttpServletRequest request) {
    return new ResponseEntity<ApiResponseBase>(serviceApi.index(request), HttpStatus.OK);
  }
  
  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(@PathVariable UUID testCode,
      @RequestParam Optional<String> token, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);

    if (token.isPresent()) {
      Optional<User> user = serviceApi.getUserByToken(token.get(), userLocale);

      if (user.isPresent()) {
        return iqTestDetailsPrivate(testCode, user.get(), userLocale);
      }
    }

    return iqTestDetailsPublic(testCode, userLocale);
  }

  @RequestMapping(value = "/test/start", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(@PathVariable IqTestType type,
      @RequestParam String token, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);

    Optional<User> user = serviceApi.getUserByToken(token, userLocale);
    if (!user.isPresent()) {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }

    Optional<TestResult> testResult = serviceApi.startUserTest(type, user.get(), userLocale);
    if (!testResult.isPresent()) {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }

    return new ResponseEntity<ApiResponseBase>(new ApiResponseTestResult(testResult.get()),
        HttpStatus.OK);
  }


  private ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {
    Optional<TestResult> testResult = serviceApi.iqTestDetailsPublic(testCode, locale);

    if (testResult.isPresent()) {
      ApiResponseBase resultResponse = new ApiResponseTestResult(testResult.get());

      return new ResponseEntity<ApiResponseBase>(resultResponse, HttpStatus.OK);
    } else {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    Optional<TestResult> testResult = serviceApi.iqTestDetailsPrivate(testCode, user, locale);

    if (testResult.isPresent()) {
      ApiResponseBase resultResponse = new ApiResponseTestResult(testResult.get());

      return new ResponseEntity<ApiResponseBase>(resultResponse, HttpStatus.OK);
    } else {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }
  }

  private Locale loadLocale(Optional<String> locale) {
    Locale userLocale = DEFAULT_LOCALE;
    if (!locale.isPresent()) {
      return userLocale;
    }

    try {
      userLocale = Locale.valueOf(locale.get());
    } catch (IllegalArgumentException e) {
      userLocale = DEFAULT_LOCALE;
    }

    return userLocale;
  }

}
