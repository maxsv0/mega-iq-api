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

public class ApiResponsePublicResultList extends ApiResponseBase {
  private List<PublicTestResult> testsActive;

  private List<PublicTestResult> tests;

  private long count;

  public List<PublicTestResult> getTestsActive() {
    return testsActive;
  }

  public void setTestsActive(List<PublicTestResult> testsActive) {
    this.testsActive = testsActive;
  }

  public List<PublicTestResult> getTests() {
    return tests;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public void setTests(List<PublicTestResult> tests) {
    this.tests = tests;
  }

  public ApiResponsePublicResultList() {
    super();
  }

  public ApiResponsePublicResultList(List<PublicTestResult> testsActive,
      List<PublicTestResult> tests, long count, Locale locale) {
    super();
    this.testsActive = testsActive;
    this.tests = tests;
    this.count = count;

    this.setOk();
    this.setDate(new Date());
    this.setMsg(null);
    this.setLocale(locale);
  }

  @Override
  public String toString() {
    return "ApiResponsePublicResultList [testsActive=" + testsActive + ", tests=" + tests
        + ", count=" + count + "]";
  }

}
