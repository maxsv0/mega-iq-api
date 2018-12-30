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

package com.max.appengine.springboot.megaiq.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import com.max.appengine.springboot.megaiq.service.UserService;

@RestController
public class ApiTestController extends AbstractApiController {
  private final QuestionsService questionsService;

  private final UserService userService;

  private final TestResultService testResultService;

  @Autowired
  public ApiTestController(UserService userService, QuestionsService questionsService,
      TestResultService testResultService) {
    this.questionsService = questionsService;
    this.userService = userService;
    this.testResultService = testResultService;
  }

  @RequestMapping(value = "/test/start", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(HttpServletRequest request,
      @RequestParam IqTestType type, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    Optional<User> user = userService.getUserByToken(token.get(), UserTokenType.ACCESS);
    if (!user.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    List<Question> questions = this.questionsService.getQuestionsSet(type, userLocale);
    TestResult testResult =
        testResultService.startUserTest(user.get(), type, questions, userLocale);

    ApiTestResult apiTestResult = new ApiTestResult(this.questionsService, testResult, true);

    return sendResponseTestResult(apiTestResult);
  }

  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(HttpServletRequest request,
      @PathVariable UUID testCode, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (token.isPresent()) {
      Optional<User> userResult = userService.getUserByToken(token.get(), UserTokenType.ACCESS);

      if (userResult.isPresent()) {
        return iqTestDetailsPrivate(testCode, userResult.get(), userLocale);
      }
    }

    return iqTestDetailsPublic(testCode, userLocale);
  }

  @RequestMapping(value = "/list-my", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestListUserResults(HttpServletRequest request,
      @RequestParam Optional<String> locale) {

    Optional<String> token = getTokenFromHeader(request);
    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    Locale userLocale = loadLocale(locale);

    Optional<User> user = userService.getUserByToken(token.get(), UserTokenType.ACCESS);
    if (!user.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    List<TestResult> listResults = loadResultsByUserId(user.get().getId(), userLocale);

    List<ApiTestResult> usersPublicList = new ArrayList<ApiTestResult>();
    for (TestResult testResult : listResults) {
      usersPublicList.add(new ApiTestResult(this.questionsService, testResult, true));
    }

    return sendResponseTestResultList(usersPublicList);
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    Optional<TestResult> testResult = loadIqTestDetailsPrivate(testCode, user, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult =
          new ApiTestResult(this.questionsService, testResult.get(), true);

      return sendResponseTestResult(apiTestResult);
    } else {
      return sendResponseError(MESSAGE_WRONG_REQUEST);
    }
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {
    Optional<TestResult> testResult = loadIqTestDetailsPublic(testCode, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult =
          new ApiTestResult(this.questionsService, testResult.get(), false);

      return sendResponseTestResult(apiTestResult);
    } else {
      return sendResponseError(MESSAGE_WRONG_REQUEST);
    }
  }

  private List<TestResult> loadResultsByUserId(Integer userId, Locale locale) {
    return testResultService.findByUserId(userId, locale);
  }

  private Optional<TestResult> loadIqTestDetailsPublic(UUID testCode, Locale locale) {
    return testResultService.getTestResultByCode(testCode, locale);
  }

  private Optional<TestResult> loadIqTestDetailsPrivate(UUID testCode, User user, Locale locale) {
    Optional<TestResult> result = testResultService.getTestResultByCode(testCode, locale);

    // private result can be requested only be user himself
    if (!user.getId().equals(result.get().getUserId())) {
      return Optional.empty();
    } else {
      TestResult testData = testResultService.loadQuestions(result.get());
      return Optional.of(testData);
    }
  }
}
