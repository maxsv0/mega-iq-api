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

import java.util.Date;
import com.max.appengine.springboot.megaiq.model.User;

public class ApiResponseUser extends ApiResponseBase {
  private ApiUser user;
  private ApiToken token;

  public ApiUser getUser() {
    return user;
  }
  
  public ApiToken getToken() {
    return token;
  }

  public ApiResponseUser(User user) {
    super();
    this.user = new ApiUser(user);
    this.token = new ApiToken(user.getUserToken());

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
  }

  public ApiResponseUser() {
    super();
  }

  public ApiResponseUser(boolean ok, String msg, Date date, ApiUser user, ApiToken token) {
    super();
    this.user = user;
    this.token = token;
    this.setOk(ok);
    this.setMsg(msg);
    this.setDate(date);
  }

  @Override
  public String toString() {
    return "ApiResponseUser [user=" + user + ", token=" + token + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
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
    ApiResponseUser other = (ApiResponseUser) obj;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token))
      return false;
    if (user == null) {
      if (other.user != null)
        return false;
    } else if (!user.equals(other.user))
      return false;
    return true;
  }
  
}
