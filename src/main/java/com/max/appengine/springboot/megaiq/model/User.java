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
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.max.appengine.springboot.megaiq.model.api.RequestImportUser;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Entity
@Table(name = "users", indexes = {@Index(columnList = "locale"), @Index(columnList = "isPublic"),
    @Index(columnList = "iq")})
public class User extends AbstractUser {

  @Transient
  private List<TestResult> testResultList;

  @Transient
  private String token;

  @Transient
  private String password;
  
  @Transient
  private Integer certificateProgress;
  

  public List<TestResult> getTestResultList() {
    return testResultList;
  }

  public void setTestResultList(List<TestResult> testResultList) {
    this.testResultList = testResultList;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public Integer getCertificateProgress() {
    return certificateProgress;
  }

  public void setCertificateProgress(Integer certificateProgress) {
    this.certificateProgress = certificateProgress;
  }

  public User() {
    super();
  }

  public User(RequestImportUser importUser) {
    
    this.setId(importUser.getId());
    this.setLocale(importUser.getLocale());
    this.setEmail(importUser.getEmail());
    this.setName(importUser.getName());
    this.setCreateDate(importUser.getCreateDate());
    this.setLocation(importUser.getLocation());
    this.setAge(importUser.getAge());
    this.setIp(importUser.getIp());
    this.setIq(importUser.getIq());
    this.setIsPublic(importUser.getIsPublic());  
    this.setPic(importUser.getPic());
    this.setPassword(UUID.randomUUID().toString());
    this.setIsEmailVerified(true);
  }

  // TODO: remove this constructor, not needed
  public User(String email, String name, String url, String pic, String location, Integer age,
      Integer iq, Boolean isPublic, String password, String ip, Integer geoId, Locale locale) {
    super();

    this.setEmail(email);
    this.setName(name);
    this.setUrl(url);
    this.setPic(pic);
    this.setLocation(location);
    this.setAge(age);
    this.setIq(iq);
    this.setIsPublic(isPublic);
    this.setPassword(password);
    this.setIp(ip);
    this.setGeoId(geoId);
    this.setLocale(locale);
  }

  @Override
  public String toString() {
    return "User [id=" + getId() + ", email=" + getEmail() + ", name=" + getName() + ", url="
        + getUrl() + ", pic=" + getPic() + ", location=" + getLocation() + ", age=" + getAge()
        + ", iq=" + getIq() + ", isPublic=" + getIsPublic() + ", password=" + getPassword()
        + ", ip=" + getIp() + ", geoId=" + getGeoId() + ", locale=" + getLocale() + "] Token="
        + getToken();
  }
}
