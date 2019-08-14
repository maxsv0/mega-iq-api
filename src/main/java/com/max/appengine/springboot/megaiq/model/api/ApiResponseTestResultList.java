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

public class ApiResponseTestResultList extends ApiResponseBase {
  private List<ApiTestResult> tests;

  private ApiUserPublic user;

  public List<ApiTestResult> getTests() {
    return tests;
  }

  public ApiUserPublic getUser() {
    return user;
  }

  public ApiResponseTestResultList(List<ApiTestResult> tests, ApiUserPublic user) {
    super();
    this.tests = tests;
    this.user = user;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
  }

}
