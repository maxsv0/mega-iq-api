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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.QuestionGroupsResult;
import com.max.appengine.springboot.megaiq.model.QuestionUser;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;

public abstract class AbstractUnitTest {

  protected static final Logger log = LoggerFactory.getLogger(AbstractUnitTest.class);

  @Rule
  public TestName name = new TestName();

  @Before
  public void printTestStart() {
    log.info("UT Started: {}.{}", name.getClass(), name.getMethodName());
  }

  @After
  public void printTestEnd() {
    log.info("UT Ends: {}.{}", name.getClass(), name.getMethodName());
  }

  public static String asJsonString(final Object obj) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void generateQuestionsAndAnswers(QuestionReporitory questionReporitory,
      AnswerReporitory answerReporitory, Integer maxQuestions, Integer maxAnswers, Locale locale) {
    // TODO: fix this procedure to speed-up execution

    log.info("Will generate maxQuestions={}, maxAnswers={} for locale= {}", maxQuestions,
        maxAnswers, locale);

    int answerId = 1;
    int questionId = 1;
    if (locale.equals(Locale.DE)) {
      answerId = 1000;
      questionId = 1000;
    } else if (locale.equals(Locale.RU)) {
      answerId = 2000;
      questionId = 2000;
    }

    List<IqQuestionGroup> groups = Arrays.asList(IqQuestionGroup.values());
    ArrayList<Question> questions = new ArrayList<Question>();
    ArrayList<Answer> answers = new ArrayList<Answer>();

    for (int i = 1; i <= maxQuestions; i++) {
      // GENERATE_ANSWERS_LIMIT answers for each question
      // first answer is correct

      ArrayList<IqQuestionGroup> iqGroups = new ArrayList<IqQuestionGroup>();
      int iqGroupIndex = questionId % groups.size();
      iqGroups.add(groups.get(iqGroupIndex));
      iqGroupIndex = (questionId + 1) % groups.size();
      iqGroups.add(groups.get(iqGroupIndex));

      int questionPoint = questionId % maxAnswers + 1;

      questions.add(new Question(questionId, "pic", questionPoint, answerId,
          "test." + locale + " q" + i, "info", iqGroups, new Date(), new Date(), locale));

      for (int j = 1; j <= maxAnswers; j++) {
        answers.add(new Answer(answerId++, "test." + locale + " q" + i + "a" + j, questionId,
            new Date(), new Date(), locale));
      }

      questionId++;
    }

    log.info("Answers generated: {}", answers.size());
    log.info("Question generated: {}", questions.size());

    questionReporitory.saveAll(questions);
    answerReporitory.saveAll(answers);
  }

  protected TestResult generateTestResult(User user) {
    return generateTestResult(user.getId(), user.getLocale());
  }

  protected TestResult generateTestResult(Integer userId, Locale locale) {
    UUID code = UUID.randomUUID();

    TestResult testUserResult = new TestResult(1, code, "/iqtest/result/" + code, userId,
        IqTestType.MEGA_IQ, IqTestStatus.FINISHED, new Date(), new Date(), new Date(), 150,
        new QuestionGroupsResult(1, 1.0, 1.0, 1.0, 1.0), locale);

    testUserResult.setQuestionSet(new ArrayList<QuestionUser>());

    return testUserResult;
  }

  protected User generateUser() {
    return generateUser(ConfigurationService.DEFAULT_LOCALE);
  }

  protected User generateUser(Locale locale) {
    User user = new User("java-build-test+" + Math.random() + "@mega-iq.com", "TEST", "/user/1",
        "https://lh3.googleusercontent.com/INTuvwHpiXTigV8UQWi5MpSaRt-0mimAQL_eyfGMOynRK_USId0_Z45KFIrKI3tp21J_q6panwRUfrDOBAqHbA",
        "city", 40, 150, true, UUID.randomUUID().toString(), "ip", 0, locale);

    user.setToken(UUID.randomUUID().toString());
    user.setIsEmailVerified(true);

    assertNull(user.getUid());
    assertNotNull(user.getName());
    assertNotNull(user.getEmail());
    assertNotNull(user.getPic());
    assertNotNull(user.getIsEmailVerified());

    return user;
  }

  // TODO: remove unused code
//  protected void generateConfig(ConfigurationReporitory configurationReporitory) {
//    generateConfigValue(configurationReporitory, "domain", "www.mega-iq.com", null);
//
//    for (IqTestType type : IqTestType.values()) {
//      generateConfigValue(configurationReporitory, "title", type.toString().toLowerCase(), type);
//    }
//
//    generateConfigValue(configurationReporitory, "email_subject_new_user", "email_subject_new_user",
//        null);
//    generateConfigValue(configurationReporitory, "email_subject_email_verify",
//        "email_subject_email_verify", null);
//    generateConfigValue(configurationReporitory, "email_subject_test_result",
//        "{test_type_title} email_subject_test_result", null);
//    generateConfigValue(configurationReporitory, "email_subject_forget", "email_subject_forget",
//        null);
//    generateConfigValue(configurationReporitory, "email_subject_direct_login",
//        "email_subject_direct_login", null);
//  }
//
//  protected void generateConfigValue(ConfigurationReporitory configurationReporitory, String name,
//      String value, IqTestType type) {
//    for (Locale locale : Locale.values()) {
//      Configuration config = new Configuration();
//      config.setLocale(locale);
//      config.setType(type);
//      config.setName(name);
//      config.setValue(value + "_" + locale);
//      configurationReporitory.save(config);
//    }
//  }
//  
//  protected void cleanConfig(ConfigurationReporitory configurationReporitory) {
//    configurationReporitory.deleteAll();
//  }
}
