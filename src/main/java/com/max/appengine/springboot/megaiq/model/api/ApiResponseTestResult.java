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

public class ApiResponseTestResult extends ApiResponseBase {
  private ApiTestResult test;

  private ApiUser user;
  
  public ApiTestResult getTest() {
    return test;
  }
  
  public ApiUserPublic getUser() {
    return user;
  }

  public ApiResponseTestResult() {
    super();
  }
  
  public ApiResponseTestResult(ApiTestResult testResult, ApiUser user) {
    super();
    this.test = testResult;
    this.user = user;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
    this.setLocale(testResult.getLocale());
  }
 

}
