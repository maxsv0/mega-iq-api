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
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResultList;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUsersList;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiUserPublic;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public abstract class AbstractApiController {
  public static final String MESSAGE_INVALID_ACCESS = "Access denied, Please log in and try again";

  public static final String MESSAGE_WRONG_REQUEST = "Wrong request";

  public static final String INTERNAL_ERROR = "Internal error. Please try again later";
  
  public static final Locale DEFAULT_LOCALE = Locale.EN;

  protected ResponseEntity<ApiResponseBase> sendResponseTestResultList(
      List<ApiTestResult> testResultList) {
    return sendResponseOk(new ApiResponseTestResultList(testResultList));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseTestResult(ApiTestResult testResult) {
    return sendResponseOk(new ApiResponseTestResult(testResult));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseUsersList(List<ApiUserPublic> apiUsers, long count) {
    return sendResponseOk(new ApiResponseUsersList(apiUsers, count));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseUser(ApiUserPublic apiUser) {
    return sendResponseOk(new ApiResponseUser(apiUser));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseError(String message) {
    return sendResponseOk(new ApiResponseError(message));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseBase(String message) {
    return sendResponseOk(new ApiResponseBase(message));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseOk(ApiResponseBase response) {
    return new ResponseEntity<ApiResponseBase>(response, HttpStatus.OK);
  }

  protected String getIp(HttpServletRequest request) {
    String ipProxy = request.getHeader("X-FORWARDED-FOR");
    if (ipProxy == null) {
      return request.getRemoteAddr();
    }
    return ipProxy;
  }

  protected Optional<String> getTokenFromHeader(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaders("Authorization");
    while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
      String value = headers.nextElement();
      if (value != null && !value.isEmpty() && value.toLowerCase().startsWith("bearer")) {
        return Optional.of(value.substring(6).trim());
      }
    }
    return Optional.empty();
  }

  protected Locale loadLocale(Optional<String> locale) {
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
