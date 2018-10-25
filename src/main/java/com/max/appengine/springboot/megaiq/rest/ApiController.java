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

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.entity.EntityApiResponseBase;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.ApiService;
import com.max.appengine.springboot.megaiq.service.UserService;

@RestController
public class ApiController {
  private final ApiService serviceApi;
  private final UserService serviceUser;

  //
  // pre-load:
  // questions DB
  // locales/text DB
  // GEOIP service
  //

  @Autowired
  public ApiController(ApiService service, UserService serviceUser) {
    this.serviceApi = service;
    this.serviceUser = serviceUser;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<EntityApiResponseBase> index(HttpServletRequest request) {
    return serviceApi.index(request);
  }

  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.GET)
  public ResponseEntity<EntityApiResponseBase> iqTestDetails(@PathVariable UUID testCode,
      @RequestParam Optional<String> token, @RequestParam Optional<String> locale) {

    Locale userLocale = Locale.EN;
    if (locale.isPresent()) {
      try {
        userLocale = Locale.valueOf(locale.get());
      } catch (IllegalArgumentException e) {
        userLocale = Locale.EN;
      }
    }

    if (token.isPresent()) {
      if (serviceUser.checkAuthByToken(token.get())) {
        return serviceApi.iqTestDetailsPrivate(testCode, serviceUser.getUserByToken(token.get()),
            userLocale);
      }
    }

    return serviceApi.iqTestDetailsPublic(testCode, userLocale);
  }
}
