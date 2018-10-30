package com.max.appengine.springboot.megaiq.model;

import java.util.Date;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;

public class UserToken {
  private UserTokenType type;
  private String value;
  private Date createDate;
  private Date expireDate;

  public UserTokenType getType() {
    return type;
  }

  public void setType(UserTokenType type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(Date expireDate) {
    this.expireDate = expireDate;
  }

  public UserToken(UserTokenType type, String value, Date createDate, Date expireDate) {
    super();
    this.type = type;
    this.value = value;
    this.createDate = createDate;
    this.expireDate = expireDate;
  }

}
