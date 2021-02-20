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

package com.max.appengine.springboot.megaiq.model.api;

import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

import java.util.StringJoiner;

public class ApiUserPublic {
  private Integer id;
  private String uid;
  private String name;
  private String pic;
  private String certificate;
  private String url;
  private Integer age;
  private Integer iq;
  private String location;
  private String background;
  private Integer certificateProgress;
  private Boolean isPublic;
  private String homepage;
  private Locale locale;

  public ApiUserPublic() {
    super();
  }

  public ApiUserPublic(User user) {
    super();

    this.setId(user.getId());
    this.setUid(user.getUid());
    this.setName(user.getName());
    this.setPic(user.getPic());
    this.setCertificate(user.getCertificate());
    this.setUrl(user.getUrl());
    this.setAge(user.getAge());
    this.setIq(user.getIq());
    this.setLocation(user.getLocation());
    this.setBackground(user.getBackground());
    this.setCertificateProgress(user.getCertificateProgress());
    this.setIsPublic(user.getIsPublic());
    this.setLocale(user.getLocale());
  }

  public ApiUserPublic(User user, String domain) {
    super();

    this.setId(user.getId());
    this.setUid(user.getUid());
    this.setName(user.getName());
    this.setPic(user.getPic());
    this.setCertificate(user.getCertificate());
    this.setUrl(user.getUrl());
    this.setAge(user.getAge());
    this.setIq(user.getIq());
    this.setLocation(user.getLocation());
    this.setBackground(user.getBackground());
    this.setCertificateProgress(user.getCertificateProgress());
    this.setIsPublic(user.getIsPublic());
    this.setLocale(user.getLocale());
    this.setHomepage(domain + user.getUrl());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPic() {
    return pic;
  }

  public void setPic(String pic) {
    this.pic = pic;
  }
  
  public String getCertificate() {
    return certificate;
  }

  public void setCertificate(String certificate) {
    this.certificate = certificate;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Integer getIq() {
    return iq;
  }

  public void setIq(Integer iq) {
    this.iq = iq;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getBackground() {
    return background;
  }

  public void setBackground(String background) {
    this.background = background;
  }

  public Integer getCertificateProgress() {
    return certificateProgress;
  }

  public void setCertificateProgress(Integer certificateProgress) {
    this.certificateProgress = certificateProgress;
  }
  
  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ApiUserPublic.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("uid='" + uid + "'")
            .add("name='" + name + "'")
            .add("pic='" + pic + "'")
            .add("certificate='" + certificate + "'")
            .add("url='" + url + "'")
            .add("age=" + age)
            .add("iq=" + iq)
            .add("location='" + location + "'")
            .add("background='" + background + "'")
            .add("certificateProgress=" + certificateProgress)
            .add("isPublic=" + isPublic)
            .add("homepage='" + homepage + "'")
            .add("locale='" + locale + "'")
            .toString();
  }
}
