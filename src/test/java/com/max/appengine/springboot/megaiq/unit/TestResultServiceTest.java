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

package com.max.appengine.springboot.megaiq.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.QuestionGroupsResult;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.QuestionUserRepository;
import com.max.appengine.springboot.megaiq.repository.TestResultReporitory;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;
import com.max.appengine.springboot.megaiq.service.TestResultService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestResultServiceTest extends AbstractUnitTest {

  @Autowired
  private UserReporitory userReporitory;

  @Autowired
  private TestResultReporitory testResultReporitory;

  @Autowired
  private QuestionUserRepository questionUserRepository;

  @Autowired
  private ConfigurationService configurationService;

  private TestResultService testResultService;

  private User testUserPublic;

  private TestResult testUserResultFinished;


  @Before
  public void doSetup() {
    this.testResultService = new TestResultService(userReporitory, testResultReporitory,
        questionUserRepository, configurationService);

    testUserPublic = new User("test" + UUID.randomUUID() + "@test.email", "test", "url", "pic",
        "city", 40, 150, true, "098f6bcd4621d373cade4e832627b4f6", "ip", 0, Locale.EN);
    testUserPublic = userReporitory.save(testUserPublic);

    UUID code = UUID.randomUUID();
    testUserResultFinished = new TestResult(1, code, "/iqtest/result/" + code,
        testUserPublic.getId(), IqTestType.MEGA_IQ, IqTestStatus.FINISHED, new Date(), new Date(),
        new Date(), 150, new QuestionGroupsResult(1, 1.0, 1.0, 1.0, 1.0), Locale.EN);
    testUserResultFinished = testResultReporitory.save(testUserResultFinished);

    testUserResultFinished.setUser(testUserPublic);
    testUserResultFinished.getUser().setPassword(null);
  }

  @Test
  public void testExpireTest() {
    List<Question> questionSetList = new ArrayList<Question>();

    Question question = new Question(-1, "pic", 5, 1, "title", "description",
        new ArrayList<IqQuestionGroup>(), new Date(), new Date(), testUserPublic.getLocale());
    questionSetList.add(question);

    Optional<TestResult> testResult = this.testResultService.startUserTest(testUserPublic,
        IqTestType.PRACTICE_IQ, questionSetList, testUserPublic.getLocale());
    assertTrue(testResult.isPresent());
    assertEquals(IqTestType.PRACTICE_IQ, testResult.get().getType());
    assertEquals(IqTestStatus.ACTIVE, testResult.get().getStatus());

    Optional<TestResult> testResultExpired = this.testResultService
        .getTestResultByCode(testResult.get().getCode(), testResult.get().getLocale());
    assertTrue(testResultExpired.isPresent());

    testResultExpired.get().setCreateDate(getDateYesterday());
    testResultReporitory.save(testResultExpired.get());
    this.testResultService.expireTestResults();

    Optional<TestResult> testResultExpiredAlready = this.testResultService
        .getTestResultByCode(testResult.get().getCode(), testResult.get().getLocale());
    assertTrue(testResultExpired.isPresent());
    assertEquals(IqTestStatus.EXPIRED, testResultExpiredAlready.get().getStatus());
    
    testResultReporitory.delete(testResultExpiredAlready.get());
  }

  @Test
  public void testGetTestResultById() {
    Optional<TestResult> testResult =
        this.testResultService.getTestResultById(testUserResultFinished.getId());
    assertTrue(testResult.isPresent());
    assertEquals(testUserResultFinished, testResult.get());
  }

  @Test
  public void testGetTestResultByCode() {
	Pageable page = PageRequest.of(0, 20);
	  
    List<TestResult> testResult =
        this.testResultService.findTestResultByUserId(testUserPublic.getId(), testUserPublic.getLocale(), page);
    assertEquals(1, testResult.size());
  }

  @Test
  public void testGetTestResultByUserId() {
    Optional<TestResult> testResult =
        this.testResultService.getTestResultById(testUserResultFinished.getId());
    assertTrue(testResult.isPresent());
    assertEquals(testUserResultFinished, testResult.get());
  }

  @Test
  public void testGetTestResultCount() {
    long count = this.testResultService.getResultCount();
    assertTrue(count > 0);
  }

  @Test
  public void testGetTestResultByCodeWrongLocale() {
    Optional<TestResult> testResult =
        this.testResultService.getTestResultByCode(testUserResultFinished.getCode(), Locale.DE);
    assertFalse(testResult.isPresent());
  }

  @Test
  public void testGetTestResultByCodeWrongCode() {
    Optional<TestResult> testResult = this.testResultService.getTestResultByCode(UUID.randomUUID(),
        testUserResultFinished.getLocale());
    assertFalse(testResult.isPresent());
  }

  @Test
  public void testGetTestResultByWrongId() {
    Optional<TestResult> testResult = this.testResultService.getTestResultById(-1);
    assertFalse(testResult.isPresent());
  }

  @Test
  public void testStartTestPracticeAndCompleteAndDelete() {
    List<Question> questionSetList = new ArrayList<Question>();

    Question question = new Question(-1, "pic", 5, 1, "title", "description",
        new ArrayList<IqQuestionGroup>(), new Date(), new Date(), testUserPublic.getLocale());
    questionSetList.add(question);

    Optional<TestResult> testResult = this.testResultService.startUserTest(testUserPublic,
        IqTestType.PRACTICE_IQ, questionSetList, testUserPublic.getLocale());
    assertTrue(testResult.isPresent());
    assertEquals(IqTestType.PRACTICE_IQ, testResult.get().getType());
    assertEquals(IqTestStatus.ACTIVE, testResult.get().getStatus());

    // try to submit finish => no success
    Optional<TestResult> finishTestResultFail =
        this.testResultService.submitFinish(testResult.get());
    assertFalse(finishTestResultFail.isPresent());

    // submit correct answer
    TestResult submitTestResult =
        this.testResultService.submitUserAnswer(testResult.get(), 1, question.getAnswerCorrect());
    assertEquals(IqTestStatus.ACTIVE, submitTestResult.getStatus());

    // submit finish
    Optional<TestResult> finishTestResult = this.testResultService.submitFinish(submitTestResult);
    assertTrue(finishTestResult.isPresent());
    assertEquals(IqTestType.PRACTICE_IQ, finishTestResult.get().getType());
    assertEquals(IqTestStatus.FINISHED, finishTestResult.get().getStatus());
    assertEquals(Integer.valueOf(1), finishTestResult.get().getPoints());

    // delete result
    this.testResultService.deleteTestResult(testResult.get());
  }

  private Date getDateYesterday() {
    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    return cal.getTime();
  }
}
