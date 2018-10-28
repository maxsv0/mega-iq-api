package com.max.appengine.springboot.megaiq.model.api;

import java.util.Date;

public class ApiResponseError extends ApiResponseBase {
  
  public ApiResponseError(String msg) {
    super();
    this.setOk(false);
    this.setDate(new Date());
    this.setMsg(msg);
  }
}
