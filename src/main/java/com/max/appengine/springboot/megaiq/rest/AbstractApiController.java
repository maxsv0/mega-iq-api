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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.common.collect.Table;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResultList;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUserPublic;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUsersList;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUsersTop;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiUser;
import com.max.appengine.springboot.megaiq.model.api.ApiUserPublic;
import com.max.appengine.springboot.megaiq.model.api.ApiUserTop;
import com.max.appengine.springboot.megaiq.model.api.ResponseTestResultPublic;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;

public abstract class AbstractApiController {
  protected ResponseEntity<ApiResponseBase> sendResponseTestResultList(
      List<ApiTestResult> testResultList, ApiUserPublic user) {
    return sendResponseOk(new ApiResponseTestResultList(testResultList, user));
  }

  protected ResponseEntity<ApiResponseBase> sendResponsePublicTestResult(ApiTestResult testResult,
      ApiUserPublic user) {
    return sendResponseOk(new ResponseTestResultPublic(testResult, user));
  }


  protected ResponseEntity<ApiResponseBase> sendResponsePublicTestResult(ApiTestResult testResult,
      ApiUser user) {
    return sendResponseOk(new ApiResponseTestResult(testResult, user));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseTestResult(ApiTestResult testResult,
      User user) {
    return sendResponseOk(new ApiResponseTestResult(testResult, new ApiUser(user)));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseUsersTop(List<ApiUserTop> apiUsersTop,
      List<ApiUserPublic> apiUsers, long count, List<ApiUserPublic> exampleProfiles,
      Locale locale) {
    return sendResponseOk(
        new ApiResponseUsersTop(apiUsersTop, apiUsers, count, exampleProfiles, locale));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseUsersList(List<ApiUserPublic> apiUsers,
      long count, Locale locale) {
    return sendResponseOk(new ApiResponseUsersList(apiUsers, count, locale));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseUserPublic(User user, Integer certificateProgress, Locale locale) {
    return sendResponseOk(new ApiResponseUserPublic(new ApiUserPublic(user, certificateProgress), locale));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseUser(User user, Locale locale) {
    return sendResponseOk(new ApiResponseUser(new ApiUser(user), locale));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseErrorRaw(String message, Locale locale) {
    return sendResponseOk(new ApiResponseError(message, locale));
  };

  protected ResponseEntity<ApiResponseBase> sendResponseError(String messageName,
      Table<String, Locale, String> configCache, Locale locale) {

    return sendResponseErrorRaw(getCacheValue(configCache, messageName, locale), locale);
  }

  protected ResponseEntity<ApiResponseBase> sendResponseBaseRaw(String message, Locale locale) {
    return sendResponseOk(new ApiResponseBase(message, locale));
  }

  protected ResponseEntity<ApiResponseBase> sendResponseBase(String messageName,
      Table<String, Locale, String> configCache, Locale locale) {

    return sendResponseBaseRaw(getCacheValue(configCache, messageName, locale), locale);
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
    Locale userLocale = ConfigurationService.DEFAULT_LOCALE;
    if (!locale.isPresent()) {
      return userLocale;
    }

    try {
      userLocale = Locale.valueOf(locale.get());
    } catch (IllegalArgumentException e) {
      userLocale = ConfigurationService.DEFAULT_LOCALE;
    }

    return userLocale;
  }

  protected void cacheValuesForAllLocales(ConfigurationService configurationService,
      Table<String, Locale, String> cache, String name) {
    for (Locale locale : Locale.values()) {
      cache.put(name, locale, configurationService.getConfigGlobal(name, locale));
    }
  }

  protected String getCacheValue(Table<String, Locale, String> cache, String name, Locale locale) {
    String value = cache.get(name, locale);

    if (value == null) {
      throw new RuntimeException(
          "Config value is empty. Name: " + name + ", locale=" + locale + ", Cache=" + cache);
    }

    return value;
  }
  
  protected String getRandomUserBackground() {
    Random rand = new Random();
    return "custom-bg" + (rand.nextInt(10) + 1);   // 1 to 10
  }
  
  protected String getRandomUserAvatar() {

    List<String> list = Arrays.asList(
        "https://lh3.googleusercontent.com/INTuvwHpiXTigV8UQWi5MpSaRt-0mimAQL_eyfGMOynRK_USId0_Z45KFIrKI3tp21J_q6panwRUfrDOBAqHbA",
        "https://lh3.googleusercontent.com/Pjnej65ZS1_DqA-akORx7OHfMtahUiwgtUDOszL2LcbpP3RbROVz5U48N5gcwd0RSBGhdvlaBUtmXQ7VfnM",
        "https://lh3.googleusercontent.com/UYZHF0NvpK-D7LFvXjHfWx3qf_FHEUz0LxCpSNoacI-BwTSUwvk1NFzKhL8L2Qn_uQ_vKJT1TC6m4WlRa5ntNQ",
        "https://lh3.googleusercontent.com/aMC2L9FNILZSHYIQX2BKcr1967r2JBXI__ihJf8P_ux0dyAhtKGTIemHhVdZtKKeX9CXrRxagsLRZ3Yi6Og",
        "https://lh3.googleusercontent.com/q5Yql4cOJt3zhloY6VPwq-jTh1Fev1WkdsIUWomCpgbnwjKXUjlfmVUeQwUbM3txG4dNOL4u6iYk1aSs1Qsg",
        "https://lh3.googleusercontent.com/U1XNjnXDG5l3YOFguuC4gxFeVZvTrs09dzGMDPA7yHh0J-5XtoXQgOcFjFipgieJqJeA88YHvdmKhlItCBc",
        "https://lh3.googleusercontent.com/lBNWn4fHC0NwZgDHXzNHqaEEEY58G233jLGsg0MIyG2fMT6xslJ-uMjx0yKHC2dYlz_uN82eEH7OCgu76dI",
        "https://lh3.googleusercontent.com/8m39DcXMIs7E8OCn8R0lirvIBK4sh1DK3QvapKqbfsrDAw9Q96TnRP1qYuHccYP7PDrAAaCB2bm6kQRjW3Qo",
        "https://lh3.googleusercontent.com/tuw6slWlwIeL3PewrRnDPVTfpuR5OPrDsMTNmDQnb3KQDBFqsuJl8MFfNAkCVXkPcmz0BoM6rvw2XxE10eGX",
        "https://lh3.googleusercontent.com/0afftGjZogSfSZ08FwQ2Ijg-QSFCAkSqTDw_WWEIoE-hKKhjh9tqDfkKNExNBWbuiJuEWDse_C5qrqPCMpM");


    Random rand = new Random();
    return list.get(rand.nextInt(list.size()));
  }
}
