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
    return new ResponseEntity<ApiResponseBase>(new ApiResponseUsersList(apiUsers), HttpStatus.OK);
  }
  
  protected ResponseEntity<ApiResponseBase> sendResponseUser(ApiUserPublic apiUser) {
    return new ResponseEntity<ApiResponseBase>(new ApiResponseUser(apiUser), HttpStatus.OK);
  }

  protected ResponseEntity<ApiResponseBase> sendResponseError(String message) {
    return new ResponseEntity<ApiResponseBase>(new ApiResponseError(message), HttpStatus.OK);
  }

  protected String getIp(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  protected String getTokenFromHeader(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaders("Authorization");
    while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
      String value = headers.nextElement();
      if ((value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
        return value.substring(BEARER_TYPE.length()).trim();
      }
    }
    return null;
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
