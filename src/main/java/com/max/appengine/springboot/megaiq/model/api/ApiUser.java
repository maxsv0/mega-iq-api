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

public class ApiUser extends ApiUserPublic {
  private String email;
  private String password;
  private String token;
  private Boolean isPublic;
  private Boolean isEmailVerified;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((isEmailVerified == null) ? 0 : isEmailVerified.hashCode());
    result = prime * result + ((isPublic == null) ? 0 : isPublic.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((token == null) ? 0 : token.hashCode());
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
    ApiUser other = (ApiUser) obj;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (isEmailVerified == null) {
      if (other.isEmailVerified != null)
        return false;
    } else if (!isEmailVerified.equals(other.isEmailVerified))
      return false;
    if (isPublic == null) {
      if (other.isPublic != null)
        return false;
    } else if (!isPublic.equals(other.isPublic))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token))
      return false;
    return true;
  }

  public ApiUser() {
    super();
  }

  public ApiUser(User user) {
    super();

    this.setId(user.getId());
    this.setIsPublic(user.getIsPublic());
    this.setIsEmailVerified(user.getIsEmailVerified());
    this.setName(user.getName());
    this.setPic(user.getPic());
    this.setUrl(user.getUrl());
    this.setAge(user.getAge());
    this.setIq(user.getIq());
    this.setLocation(user.getLocation());

    this.setEmail(user.getEmail());
    this.setPassword(user.getPassword());

    if (user.getToken() != null) {
      this.setToken(user.getToken());
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  public Boolean getIsEmailVerified() {
    return isEmailVerified;
  }

  public void setIsEmailVerified(Boolean isEmailVerified) {
    this.isEmailVerified = isEmailVerified;
  }

  @Override
  public String toString() {
    return "ApiUser [email=" + email + ", password=" + password + ", token=" + token + ", isPublic="
        + isPublic + ", isEmailVerified=" + isEmailVerified + "]";
  }
}
