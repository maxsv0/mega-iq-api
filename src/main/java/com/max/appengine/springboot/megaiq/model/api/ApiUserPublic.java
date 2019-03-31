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

public class ApiUserPublic {
  private Integer id;
  private String name;
  private String pic;
  private String url;
  private Integer age;
  private Integer iq;
  private String location;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((age == null) ? 0 : age.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((iq == null) ? 0 : iq.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pic == null) ? 0 : pic.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApiUserPublic other = (ApiUserPublic) obj;
    if (age == null) {
      if (other.age != null)
        return false;
    } else if (!age.equals(other.age))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (iq == null) {
      if (other.iq != null)
        return false;
    } else if (!iq.equals(other.iq))
      return false;
    if (location == null) {
      if (other.location != null)
        return false;
    } else if (!location.equals(other.location))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (pic == null) {
      if (other.pic != null)
        return false;
    } else if (!pic.equals(other.pic))
      return false;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    return true;
  }

  public ApiUserPublic() {
    super();
  }

  public ApiUserPublic(User user) {
    super();

    this.setId(user.getId());
    this.setName(user.getName());
    this.setPic(user.getPic());
    this.setUrl(user.getUrl());
    this.setAge(user.getAge());
    this.setIq(user.getIq());
    this.setLocation(user.getLocation());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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



}
