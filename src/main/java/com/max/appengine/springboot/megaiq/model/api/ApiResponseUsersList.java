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
import java.util.List;

public class ApiResponseUsersList extends ApiResponseBase {
  private List<ApiUserPublic> users;

  public List<ApiUserPublic> getUsers() {
    return users;
  }
  
  public ApiResponseUsersList(List<ApiUserPublic> apiUsers) {
    super();
    this.users = apiUsers;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
  }

  public ApiResponseUsersList() {
    super();
  }

  public ApiResponseUsersList(boolean ok, String msg, Date date, List<ApiUserPublic> users, ApiToken token) {
    super();
    this.users = users;
    this.setOk(ok);
    this.setMsg(msg);
    this.setDate(date);
  }

  @Override
  public String toString() {
    return "ApiResponseUser [users=" + users + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((users == null) ? 0 : users.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApiResponseUsersList other = (ApiResponseUsersList) obj;
    if (users == null) {
      if (other.users != null)
        return false;
    } else if (!users.equals(other.users))
      return false;
    return true;
  }

}
