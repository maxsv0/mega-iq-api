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
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public class ApiResponseBase {
  private boolean ok;
  private String msg;
  private Date date;
  private Locale locale;

  public ApiResponseBase() {
    super();
  }

  public ApiResponseBase(String msg, Locale locale) {
    super();
    this.date = new Date();
    this.ok = true;
    this.msg = msg;
    this.locale = locale;
  }

  public boolean isOk() {
    return ok;
  }

  public void setOk() {
    this.ok = true;
  }

  public void setOk(boolean ok) {
    this.ok = ok;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
  
  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((locale == null) ? 0 : locale.hashCode());
    result = prime * result + ((msg == null) ? 0 : msg.hashCode());
    result = prime * result + (ok ? 1231 : 1237);
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
    ApiResponseBase other = (ApiResponseBase) obj;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    if (locale != other.locale)
      return false;
    if (msg == null) {
      if (other.msg != null)
        return false;
    } else if (!msg.equals(other.msg))
      return false;
    if (ok != other.ok)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ApiResponseBase [ok=" + ok + ", msg=" + msg + ", date=" + date + ", locale=" + locale
        + "]";
  }

}
