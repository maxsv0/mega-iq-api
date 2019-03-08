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

public class ApiResponseUserPublic extends ApiResponseBase {
  private ApiUserPublic user;

  public ApiUserPublic getUser() {
    return user;
  }

  public ApiResponseUserPublic(ApiUserPublic apiUser) {
    super();
    this.user = apiUser;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
  }

  public ApiResponseUserPublic() {
    super();
  }

  public ApiResponseUserPublic(boolean ok, String msg, Date date, ApiUser user) {
    super();
    this.user = user;
    this.setOk(ok);
    this.setMsg(msg);
    this.setDate(date);
  }

  @Override
  public String toString() {
    return "ApiResponseUser [user=" + user + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
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
    ApiResponseUserPublic other = (ApiResponseUserPublic) obj;
    if (user == null) {
      if (other.user != null)
        return false;
    } else if (!user.equals(other.user))
      return false;
    return true;
  }

}
