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

package com.max.appengine.springboot.megaiq.model;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;

@Entity
@Table(name = "user_token", indexes = {@Index(columnList = "userId"),
    @Index(columnList = "expireDate"), @Index(columnList = "value"), @Index(columnList = "type")})
public class UserToken extends AbstractUserToken {
  private static final Integer DEFAULT_EXPIRE_DAYS = 30;

  public UserToken() {
    super();
  }

  public UserToken(Integer userId, UserTokenType type) {
    this.setUserId(userId);
    this.setType(type);

    Date date = new Date();
    this.setCreateDate(date);

    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DATE, DEFAULT_EXPIRE_DAYS);
    this.setExpireDate(c.getTime());

    this.setValue(UUID.randomUUID().toString());
  }

  public UserToken(Integer userId, UserTokenType type, String value, Date createDate,
      Date expireDate) {
    super();

    this.setUserId(userId);
    this.setType(type);
    this.setValue(value);
    this.setCreateDate(createDate);
    this.setExpireDate(expireDate);
  }

  @Override
  public String toString() {
    return "UserToken [id=" + getId() + ", userId=" + getUserId() + ", type=" + getType()
        + ", value=" + getValue() + ", createDate=" + getCreateDate() + ", expireDate="
        + getExpireDate() + "]";
  }
}
