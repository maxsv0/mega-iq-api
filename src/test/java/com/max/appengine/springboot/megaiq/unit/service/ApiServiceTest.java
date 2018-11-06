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
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.QuestionUser;
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
  private static final int GENERATE_ANSWERS_LIMIT = 2;

  private QuestionsService qestionsService;

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

    this.qestionsService = new QuestionsService(answerReporitory, questionReporitory);
    this.apiService = new ApiService(this.qestionsService, testResultService, userService);

    User testUser = new User("test@test.email", "test", "url", "pic", "city", 40, 150, true,
        USER_PASSWORD_HASH, "ip", 0, Locale.EN);

    testUser = apiService.addNewUser(testUser);

    Optional<User> userResult = apiService.userLogin(testUser.getEmail(), USER_PASSWORD);
    assertTrue(userResult.isPresent());
    assertNotNull(userResult.get().getUserToken());

    this.tokenUser = userResult.get().getUserToken().getValue();
  }

  @Test
  public void testApiServiceBasis() {
    Optional<User> userResult = this.apiService.getUserByToken(this.tokenUser, Locale.DE);
    assertTrue(userResult.isPresent());
    assertEquals(tokenUser, userResult.get().getUserTokenByType(UserTokenType.ACCESS).getValue());

    userResult = this.apiService.getUserByToken(this.tokenUser, Locale.EN);
    assertTrue(userResult.isPresent());
    assertEquals(tokenUser, userResult.get().getUserTokenByType(UserTokenType.ACCESS).getValue());

    Optional<TestResult> testNewResult =
        this.apiService.startUserTest(IqTestType.MEGA_IQ, userResult.get(), Locale.EN);
    log.info("Got New test={}", testNewResult);

    assertTrue(testNewResult.isPresent());
    assertEquals(userResult.get(), testNewResult.get().getUser());
    assertNotNull(testNewResult.get().getQuestionSet());
    assertEquals(this.qestionsService.getQuestionsLimitByType(IqTestType.MEGA_IQ),
        testNewResult.get().getQuestionSet().size());

    Optional<TestResult> testResultDataFail =
        this.apiService.iqTestDetailsPublic(testNewResult.get().getCode(), Locale.DE);
    assertFalse(testResultDataFail.isPresent());

    Optional<TestResult> testResultPublic = this.apiService
        .iqTestDetailsPublic(testNewResult.get().getCode(), testNewResult.get().getLocale());
    log.info("Got Public testResultData={}", testResultPublic);
    assertTrue(testResultPublic.isPresent());
    assertEquals(testNewResult.get(), testResultPublic.get());
    assertNull(testResultPublic.get().getQuestionSet());

    Optional<TestResult> testResultPrivate = this.apiService.iqTestDetailsPrivate(
        testNewResult.get().getCode(), userResult.get(), testNewResult.get().getLocale());
    log.info("Got Private testResultData={}", testResultPrivate);
    assertTrue(testResultPrivate.isPresent());
    assertEquals(testNewResult.get(), testResultPrivate.get());
    assertNotNull(testResultPrivate.get().getQuestionSet());
    assertEquals(testResultPrivate.get().getQuestionSet(), testNewResult.get().getQuestionSet());
  }
}
