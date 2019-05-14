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
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public class ApiResponseUsersTop extends ApiResponseBase {
  private List<ApiUserPublic> users;

  private List<ApiUserTop> usersTop;

  private long count;

  public List<ApiUserPublic> getUsers() {
    return users;
  }

  public void setUsers(List<ApiUserPublic> users) {
    this.users = users;
  }

  public List<ApiUserTop> getUsersTop() {
    return usersTop;
  }

  public void setUsersTop(List<ApiUserTop> usersTop) {
    this.usersTop = usersTop;
  }

  public long getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public ApiResponseUsersTop(List<ApiUserTop> apiUsersTop, List<ApiUserPublic> apiUsers, long count,
      Locale locale) {
    super();
    this.usersTop = apiUsersTop;
    this.users = apiUsers;
    this.count = count;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
    this.setLocale(locale);
  }

  public ApiResponseUsersTop() {
    super();
  }

  @Override
  public String toString() {
    return "ApiResponseUsersTop [users=" + users + ", usersTop=" + usersTop + ", count=" + count
        + "]";
  }
}
