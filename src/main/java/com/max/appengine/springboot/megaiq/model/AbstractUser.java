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

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@MappedSuperclass
public abstract class AbstractUser {
    @Id
    private Integer id;
    private String email;
    private String name;
    private String url;
    private String pic;
    private String city;
    private Integer age;
    private Integer iq;
    private Boolean isPublic;
    private String ip;
    private Integer geoId;
    private Locale locale;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getPic() {
      return pic;
    }

    public void setPic(String pic) {
      this.pic = pic;
    }

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
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

    public Boolean getIsPublic() {
      return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
      this.isPublic = isPublic;
    }

    public String getIp() {
      return ip;
    }

    public void setIp(String ip) {
      this.ip = ip;
    }

    public Integer getGeoId() {
      return geoId;
    }

    public void setGeoId(Integer geoId) {
      this.geoId = geoId;
    }

    public Locale getLocale() {
      return locale;
    }

    public void setLocale(Locale locale) {
      this.locale = locale;
    }

  }

