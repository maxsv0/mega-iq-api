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
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUsersList;
import com.max.appengine.springboot.megaiq.model.api.ApiUserPublic;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public abstract class AbstractApiController {
  public static final Locale DEFAULT_LOCALE = Locale.EN;
  
  public static final String BEARER_TYPE = "Bearer";
  
  protected ResponseEntity<ApiResponseBase> sendResponseUsersList(List<ApiUserPublic> apiUsers) {
    return sendResponseOk(new ApiResponseUsersList(apiUsers));
  }
  
  protected ResponseEntity<ApiResponseBase> sendResponseUser(ApiUserPublic apiUser) {
    return sendResponseOk(new ApiResponseUser(apiUser));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseError(String message) {
    return sendResponseOk(new ApiResponseError(message));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseOk(ApiResponseBase response) {
    return new ResponseEntity<ApiResponseBase>(response, HttpStatus.OK);
  }
  
  protected String getIp(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  protected Optional<String> getTokenFromHeader(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaders("Authorization");
    while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
      String value = headers.nextElement();
      if ((value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
        return Optional.of(value.substring(BEARER_TYPE.length()).trim());
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
