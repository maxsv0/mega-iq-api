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

package com.max.appengine.springboot.megaiq.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.repository.TestResultReporitory;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;

@Service
public class TestResultService {
  private final UserReporitory userReporitory;
  private final TestResultReporitory testResultReporitory;

  @Autowired
  public TestResultService(UserReporitory userReporitory,
      TestResultReporitory testResultReporitory) {
    this.userReporitory = userReporitory;
    this.testResultReporitory = testResultReporitory;
  }

  public Optional<TestResult> getTestResultById(Integer userId) {
    Optional<TestResult> testResult = testResultReporitory.findById(userId);

    if (testResult.isPresent()) {
      TestResult testResultDetails = loadTestDetails(testResult.get());
      
      return Optional.of(testResultDetails);
    }

    return testResult;
  }

  public Optional<TestResult> getTestResultByCode(UUID code) {
    Optional<TestResult> testResult = testResultReporitory.findByCode(code);
    
    if (testResult.isPresent()) {
      TestResult testResultDetails = loadTestDetails(testResult.get());
      
      return Optional.of(testResultDetails);
    }

    return testResult;
  }
  
  private TestResult loadTestDetails(TestResult testResult) {
    Optional<User> user = userReporitory.findById(testResult.getUserId());
    if (user.isPresent()) {
      testResult.setUser(user.get());
    }
    
    return testResult;
  }


}
