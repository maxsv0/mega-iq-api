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

public class ApiResponseTestInfoList extends ApiResponseBase {
  private List<ApiTestInfo> tests;

  public List<ApiTestInfo> getTests() {
    return tests;
  }

  public void setTests(List<ApiTestInfo> tests) {
    this.tests = tests;
  }

  public ApiResponseTestInfoList() {
    super();
  }

  public ApiResponseTestInfoList(List<ApiTestInfo> tests, Locale locale) {
    super();
    this.tests = tests;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
    this.setLocale(locale);
  }

  @Override
  public String toString() {
    return "ApiResponseTestInfoList [getTests()=" + getTests() + ", isOk()=" + isOk()
        + ", getMsg()=" + getMsg() + ", getDate()=" + getDate() + ", getLocale()=" + getLocale()
        + ", getClass()=" + getClass() + "]";
  }

}
