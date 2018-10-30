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

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Entity
@Table(name = "users", indexes = {@Index(columnList = "locale"), @Index(columnList = "isPublic"),
    @Index(columnList = "iq")})
public class User extends AbstractUser {
  
  @ElementCollection(targetClass = UserToken.class, fetch = FetchType.LAZY)
  @CollectionTable(name = "user_token", joinColumns = @JoinColumn(name = "user_id"),
      indexes = {@Index(columnList = "user_id"), @Index(columnList = "token")})
  @Enumerated(EnumType.STRING)
  private List<UserToken> token; 

  public User() {
    super();
  }

  public User(Integer id, String email, String name, String url, String pic, String city,
      Integer age, Integer iq, Boolean isPublic, String ip, Integer geoId, Locale locale) {
    super();
    
    this.setId(id);
    this.setEmail(email);
    this.setName(name);
    this.setUrl(url);
    this.setPic(pic);
    this.setCity(city);
    this.setAge(age);
    this.setIq(iq);
    this.setIsPublic(isPublic);
    this.setIp(ip);
    this.setGeoId(geoId);
    this.setLocale(locale);
  }

}
