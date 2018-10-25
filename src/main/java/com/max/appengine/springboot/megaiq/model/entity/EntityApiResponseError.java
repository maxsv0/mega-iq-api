package com.max.appengine.springboot.megaiq.model.entity;

import java.util.Date;

public class EntityApiResponseError extends EntityApiResponseBase {
  
  public EntityApiResponseError(String msg) {
    super();
    this.setOk(false);
    this.setDate(new Date());
    this.setMsg(msg);
  }
}
