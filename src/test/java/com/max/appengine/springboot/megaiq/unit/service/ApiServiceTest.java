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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.max.appengine.springboot.megaiq.model.QuestionUser;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;
import com.max.appengine.springboot.megaiq.service.ApiService;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import com.max.appengine.springboot.megaiq.service.UserService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApiServiceTest extends AbstractUnitTest {

  private static final String USER_PASSWORD = "test";
  private static final String USER_PASSWORD_HASH = "098f6bcd4621d373cade4e832627b4f6";
  private static final int GENERATE_QUESTIONS_LIMIT = 30;
  private static final int GENERATE_ANSWERS_LIMIT = 1;

  private QuestionsService questionsService;

  @Autowired
  private TestResultService testResultService;

  @Autowired
  private UserService userService;

  @Autowired
  private AnswerReporitory answerReporitory;

  @Autowired
  private QuestionReporitory questionReporitory;

  private ApiService apiService;

  private String tokenUser;

  @Before
  public void doSetup() {
    generateQuestionsAndAnswers(questionReporitory, answerReporitory, GENERATE_QUESTIONS_LIMIT,
        GENERATE_ANSWERS_LIMIT, Locale.EN);
    generateQuestionsAndAnswers(questionReporitory, answerReporitory, GENERATE_QUESTIONS_LIMIT,
        GENERATE_ANSWERS_LIMIT, Locale.DE);

    this.questionsService = new QuestionsService(answerReporitory, questionReporitory);
    this.apiService = new ApiService(this.questionsService, testResultService, userService);

    Optional<User> testUserResult = generateNewUser();
    assertTrue(testUserResult.isPresent());
    User testUser = testUserResult.get();

    Optional<User> userResult = testUserUsingLogin(testUser.getEmail());
    assertTrue(userResult.isPresent());
    assertNotNull(userResult.get().getUserToken());

    this.tokenUser = userResult.get().getUserToken().getValue();
  }

  @Test
  public void testApiRegisterDuplicateFails() {
    Optional<User> testUserResult = generateNewUser();
    User testUser = testUserResult.get();

    Optional<User> userResultDuplicate = apiService.addNewUser(testUser);
    log.info("Test duplicate result={}", userResultDuplicate);
    assertFalse(userResultDuplicate.isPresent());
  }

  @Test
  public void testApiRegisterAndLogin() {
    Optional<User> testUserResult = generateNewUser();
    assertTrue(testUserResult.isPresent());
    User testUser = testUserResult.get();
    assertNotNull(testUser.getUserToken());

    Optional<User> userResult = testUserUsingLogin(testUser.getEmail());
    assertTrue(userResult.isPresent());
    assertNotNull(userResult.get().getUserToken());
    assertEquals(testUser, userResult.get());
  }

  @Test
  public void testApiUserFoundAnyLocale() {
    for (Locale locale : Locale.values()) {
      Optional<User> userResult = testUserGetByToken(locale);
      assertTrue(userResult.isPresent());
      assertEquals(tokenUser, userResult.get().getUserToken().getValue());
    }
  }

  @Test
  public void testApiStartNewTest() {
    Optional<User> userResult = testUserGetByToken(Locale.EN);

    Optional<TestResult> testNewResult = startTestAndCheck(IqTestType.MEGA_IQ, userResult.get(),
        Locale.EN);
    assertEquals(userResult.get(), testNewResult.get().getUser());
  }

  @Test
  public void testApiGetTestNotFound() {
    Optional<User> userResult = testUserGetByToken(Locale.EN);

    Optional<TestResult> testNewResult = startTestAndCheck(IqTestType.MEGA_IQ, userResult.get(),
        Locale.EN);
    Optional<TestResult> testResultDataFail = getTestPublic(testNewResult.get().getCode(),
        Locale.DE);
    assertFalse(testResultDataFail.isPresent());
  }

  @Test
  public void testApiGetTestPublic() {
    Optional<User> userResult = testUserGetByToken(Locale.DE);

    Optional<TestResult> testNewResult = startTestAndCheck(IqTestType.PRACTICE_IQ, userResult.get(),
        Locale.DE);
    Optional<TestResult> testResultData = getTestPublic(testNewResult.get().getCode(), Locale.DE);
    assertTrue(testResultData.isPresent());
    assertEquals(testNewResult.get(), testResultData.get());
    assertNull(testResultData.get().getQuestionSet());
  }

  @Test
  public void testApiGetTestPrivate() {
    Optional<User> userResult = testUserGetByToken(Locale.EN);

    Optional<TestResult> testNewResult = startTestAndCheck(IqTestType.STANDART_IQ, userResult.get(),
        Locale.EN);
    Optional<TestResult> testResultPrivate = getTestPrivate(testNewResult.get().getCode(),
        userResult.get(), testNewResult.get().getLocale());
    assertTrue(testResultPrivate.isPresent());
    assertEquals(testNewResult.get(), testResultPrivate.get());
    assertNotNull(testResultPrivate.get().getQuestionSet());
    assertEquals(testResultPrivate.get().getQuestionSet(), testNewResult.get().getQuestionSet());
  }

  @Test
  public void submitUserAllAnswersPractice() {
    Optional<User> userResult = testUserGetByToken(Locale.EN);
    Optional<TestResult> testNewResult = startTestAndCheck(IqTestType.PRACTICE_IQ, userResult.get(),
        Locale.EN);
    assertNotNull(testNewResult.get().getQuestionSet());
    TestResult testResultSubmit = submitAllAnswers(testNewResult.get());

    Optional<TestResult> testFinish = getTestPublic(testResultSubmit.getCode(), testResultSubmit.getLocale());
    assertTrue(testFinish.isPresent());
    assertEquals(IqTestStatus.FINISHED, testFinish.get().getStatus());
    assertNotNull(testFinish.get().getFinishDate());
    assertNull(testFinish.get().getPoints());
    assertNull(testFinish.get().getGroupsGraph());
  }

  @Test
  public void submitUserAllAnswersAndGetScore() {
    Optional<User> userResult = testUserGetByToken(Locale.EN);
    Optional<TestResult> testNewResult = startTestAndCheck(IqTestType.STANDART_IQ, userResult.get(),
        Locale.EN);
    assertNotNull(testNewResult.get().getQuestionSet());
    TestResult testResultSubmit = submitAllAnswers(testNewResult.get());

    Optional<TestResult> testFinish = getTestPublic(testResultSubmit.getCode(), testResultSubmit.getLocale());
    assertTrue(testFinish.isPresent());
    assertEquals(IqTestStatus.FINISHED, testFinish.get().getStatus());
    assertNotNull(testFinish.get().getFinishDate());
    assertNotNull(testFinish.get().getPoints());
    assertNotNull(testFinish.get().getGroupsGraph());
  }

  private TestResult submitAllAnswers(TestResult testResult) {
    Integer userAnswer = 5;
    int i = 0;
    for (QuestionUser question : testResult.getQuestionSet()) {
      TestResult testResultDb = this.apiService.submitUserAnswer(testResult, question.getQuestionIq(), userAnswer);
      log.info("Test result saved ={}", testResultDb);

      assertEquals(testResult.getUser(), testResultDb.getUser());
      assertEquals(testResult.getQuestionSet(), testResultDb.getQuestionSet());

      if(i++ == testResult.getQuestionSet().size() - 1){
        assertEquals(IqTestStatus.FINISHED, testResultDb.getStatus());
      } else {
        assertEquals(IqTestStatus.ACTIVE, testResultDb.getStatus());
      }
    }

    return testResult;
  }

  private Optional<TestResult> startTestAndCheck(IqTestType type, User user, Locale locale) {
    Optional<TestResult> testNewResult =
        this.apiService.startUserTest(type, user, locale);
    log.info("Test Start New test={}", testNewResult);

    assertTrue(testNewResult.isPresent());
    assertNotNull(testNewResult.get().getQuestionSet());
    assertEquals(IqTestStatus.ACTIVE, testNewResult.get().getStatus());
    assertEquals(this.questionsService.getQuestionsLimitByType(type),
        testNewResult.get().getQuestionSet().size());

    return testNewResult;
  }

  private Optional<TestResult> getTestPublic(UUID code, Locale locale) {
    Optional<TestResult> testResult =
        this.apiService.iqTestDetailsPublic(code, locale);
    log.info("Test got Public result={}", testResult);

    return testResult;
  }

  private Optional<TestResult> getTestPrivate(UUID code, User user, Locale locale) {
    Optional<TestResult> testResult =
        this.apiService.iqTestDetailsPrivate(code, user, locale);
    log.info("Test got Private result={}", testResult);

    return testResult;
  }

  private Optional<User> generateNewUser() {
    String email = "test" + UUID.randomUUID() + "@test.email";
    Integer age = 40;
    Integer iq = 150;

    User testUser = new User(email, "test", "url", "pic", "city", age, iq, true,
        USER_PASSWORD_HASH, "ip", 0, Locale.EN);

    Optional<User> userResult = apiService.addNewUser(testUser);
    log.info("Test user result={}", userResult);

    return userResult;
  }

  private Optional<User> testUserGetByToken(Locale locale) {
    return this.apiService.getUserByToken(this.tokenUser, locale);
  }

  private Optional<User> testUserUsingLogin(String login) {
    return apiService.userLogin(login, USER_PASSWORD);
  }
}
