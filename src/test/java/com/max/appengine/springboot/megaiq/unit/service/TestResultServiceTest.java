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

package com.max.appengine.springboot.megaiq.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.QuestionGroupsResult;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.TestResultReporitory;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestResultServiceTest extends AbstractUnitTest {

  @Autowired
  private UserReporitory userReporitory;

  @Autowired
  private TestResultReporitory testResultReporitory;

  private TestResultService testResultService;

  private User testUserPublic;

  private TestResult testUserResult;

  @Before
  public void doSetup() {
    this.testResultService = new TestResultService(userReporitory, testResultReporitory);

    testUserPublic = new User(1, "test@test.email", "test", "url", "pic", "city", 40, 150, true, "",
        0, Locale.EN);
    userReporitory.save(testUserPublic);

    UUID code = UUID.randomUUID();
    testUserResult = new TestResult(1, code, "/iqtest/result/" + code, 1, IqTestType.MEGA_IQ,
        IqTestStatus.FINISHED, new Date(), new Date(), new Date(), 150,
        new QuestionGroupsResult(1, 1, 1, 1), Locale.EN);
    testResultReporitory.save(testUserResult);
  }

  @Test
  public void testQuestionsServiceBasis() {
    Optional<TestResult> testResult = this.testResultService.getTestResultById(1);
    assertTrue(testResult.isPresent());
    assertEquals(testUserResult, testResult.get());
    
    testResult = this.testResultService.getTestResultByCode(testUserResult.getCode());
    assertTrue(testResult.isPresent());
    assertEquals(testUserResult, testResult.get());
  }
}
