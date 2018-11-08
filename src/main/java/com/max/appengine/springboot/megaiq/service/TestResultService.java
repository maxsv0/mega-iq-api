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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.QuestionUser;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.QuestionUserRepository;
import com.max.appengine.springboot.megaiq.repository.TestResultReporitory;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;

@Service
public class TestResultService {
  private static final Logger log = LoggerFactory.getLogger(TestResultService.class);
  private final UserReporitory userReporitory;
  private final TestResultReporitory testResultReporitory;
  private final QuestionUserRepository questionUserRepository;

  @Autowired
  public TestResultService(UserReporitory userReporitory, TestResultReporitory testResultReporitory,
      QuestionUserRepository questionUserRepository) {
    this.userReporitory = userReporitory;
    this.testResultReporitory = testResultReporitory;
    this.questionUserRepository = questionUserRepository;
  }

  public void saveUserResults(User user) {
    testResultReporitory.saveAll(user.getTestResultList());
  }

  public Optional<TestResult> getTestResultById(Integer testId) {
    Optional<TestResult> testResult = testResultReporitory.findById(testId);

    if (testResult.isPresent()) {
      TestResult testResultDetails = loadTestDetails(testResult.get());

      return Optional.of(testResultDetails);
    }

    return testResult;
  }

  public Optional<TestResult> getTestResultByCode(UUID code, Locale locale) {
    Optional<TestResult> testResult = testResultReporitory.findByCodeAndLocale(code, locale);

    if (testResult.isPresent()) {
      TestResult testResultDetails = loadTestDetails(testResult.get());

      return Optional.of(testResultDetails);
    }

    return testResult;
  }

  public TestResult startUserTest(User user, IqTestType testType, List<Question> questions, Locale locale) {
    TestResult testResult = new TestResult(user.getId(), testType, locale);
    testResult.newQuestionSet(questions);
    testResult.setUser(user);
    TestResult testResultDb = testResultReporitory.save(testResult);
    log.info("New test created = {}", testResult);

    for (QuestionUser question : testResult.getQuestionSet()) {
      question.setTestId(testResultDb.getId());
    }
    questionUserRepository.saveAll(testResult.getQuestionSet());

    return testResultDb;
  }

  public List<TestResult> findByUserId(Integer userId, Locale locale) {
    return testResultReporitory.findByUserIdAndLocale(userId, locale);
  }

  public TestResult loadQuestions(TestResult testResult) {
    List<QuestionUser> questions = questionUserRepository.findByTestId(testResult.getId());

    if (!questions.isEmpty()) {
      testResult.setQuestionSet(questions);
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
