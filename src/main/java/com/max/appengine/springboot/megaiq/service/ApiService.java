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
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.api.ApiUser;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;


// TODO: REMOVE this file


@Service
public class ApiService {

  private static final Logger log = LoggerFactory.getLogger(ApiService.class);
  
  private final QuestionsService qestionsService;
  private final TestResultService testResultService;
  private final UserService userService;

  @Autowired
  public ApiService(QuestionsService qestionsService, TestResultService testResultService,
      UserService userService) {
    super();

    this.qestionsService = qestionsService;
    this.testResultService = testResultService;
    this.userService = userService;
  }

  public ApiResponseBase index(HttpServletRequest request) {
    ApiResponseBase result = new ApiResponseBase();
    result.setOk();
    result.setMsg("API v.0.0.1");
    result.setDate(new Date());
    return result;
  }

  public Optional<TestResult> iqTestDetailsPublic(UUID testCode, Locale locale) {
    return testResultService.getTestResultByCode(testCode, locale);
  }

  public TestResult submitUserAnswer(TestResult testResult, Integer questionIq, Integer answerUser) {
    return testResultService.submitUserAnswer(testResult, questionIq, answerUser);
  }

  public Optional<TestResult> iqTestDetailsPrivate(UUID testCode, User user, Locale locale) {
    Optional<TestResult> resultData = iqTestDetailsPublic(testCode, locale);

    if (!resultData.isPresent()) {
      return resultData;
    }

    // private result can be requested only be user himself
    if (!user.getId().equals(resultData.get().getUserId())) {
      return Optional.empty();
    } else {
      TestResult testData = testResultService.loadQuestions(resultData.get());
      return Optional.of(testData);
    }
  }

  public Optional<TestResult> startUserTest(IqTestType testType, User user, Locale locale) {
    List<Question> questions = qestionsService.getQuestionsSet(testType, locale);
    log.info("questions={}", questions);

    if (questions == null) {
      return Optional.empty();
    }

    return Optional.of(testResultService.startUserTest(user, testType, questions, locale));
  }

  public Optional<User> addNewUser(User user, Locale locale) {
    user.setLocale(locale);
    return userService.addUser(user);
  }

  public Optional<User> userLogin(String login, String password) {
    return userService.authUserLogin(login, password);
  }

  public Optional<User> getUserByToken(String token, Locale locale) {
    Optional<User> userResult = userService.getUserByToken(token, UserTokenType.ACCESS);

    if (!userResult.isPresent()) {
      return userResult;
    }

    User user = userResult.get();
    user.setTestResultList(loadAllResults(user.getId(), locale));

    return Optional.of(user);
  }

  public List<TestResult> loadAllResults(Integer userId, Locale locale) {
    return testResultService.findByUserId(userId, locale);
  }

  public Optional<User> getUserById(Integer userId) {
    return userService.getUserById(userId);
  }
}
