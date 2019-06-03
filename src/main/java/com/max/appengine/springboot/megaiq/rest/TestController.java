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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuthException;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiRequestSubmitAnswer;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestInfoList;
import com.max.appengine.springboot.megaiq.model.api.ApiTestInfo;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiUser;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.exception.MegaIQException;
import com.max.appengine.springboot.megaiq.service.AbstractServiceHelper;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import com.max.appengine.springboot.megaiq.service.UserService;

@CrossOrigin
@RestController
public class TestController extends AbstractApiController {
  public static final String MESSAGE_START_TEST_FAIL = "message_start_test_fail";

  public static final String MESSAGE_DELETE_SUCCESS = "message_delete_success";

  public static final String MESSAGE_INVALID_ACCESS = "message_invalid_access";

  public static final String MESSAGE_WRONG_REQUEST = "message_wrong_request";

  private final QuestionsService questionsService;

  private final UserService userService;

  private final TestResultService testResultService;

  private final EmailService emailService;

  private final ConfigurationService configurationService;

  private final Table<String, Locale, String> configCache = HashBasedTable.create();

  @Autowired
  public TestController(UserService userService, QuestionsService questionsService,
      TestResultService testResultService, EmailService emailService,
      ConfigurationService configurationService) {
    this.questionsService = questionsService;
    this.userService = userService;
    this.testResultService = testResultService;
    this.emailService = emailService;
    this.configurationService = configurationService;

    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_START_TEST_FAIL);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_DELETE_SUCCESS);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_INVALID_ACCESS);
    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_WRONG_REQUEST);
    AbstractServiceHelper.cacheInfoForAllTestType(configurationService, configCache);
  }

  @RequestMapping(value = "/test", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> tests(HttpServletRequest request,
      @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);

    List<ApiTestInfo> tests = new ArrayList<ApiTestInfo>();
    for (IqTestType type : IqTestType.values()) {
      ApiTestInfo testInfo = new ApiTestInfo();

      testInfo.setType(type);
      testInfo.setName(
          getCacheValue(configCache, "test_title_" + type.toString().toLowerCase(), userLocale));

      testInfo.setTime(Integer.valueOf(
          getCacheValue(configCache, "test_time_" + type.toString().toLowerCase(), userLocale)));

      testInfo.setQuestions(Integer.valueOf(getCacheValue(configCache,
          "test_questions_" + type.toString().toLowerCase(), userLocale)));

      testInfo.setPic(
          getCacheValue(configCache, "test_pic_" + type.toString().toLowerCase(), userLocale));

      testInfo.setDescription(getCacheValue(configCache,
          "test_title_promo_" + type.toString().toLowerCase(), userLocale));

      testInfo.setUrl(
          getCacheValue(configCache, "test_url_" + type.toString().toLowerCase(), userLocale));

      testInfo.setStyleName(getCacheValue(configCache,
          "test_style_name_" + type.toString().toLowerCase(), userLocale));

      testInfo.setExpire(this.configurationService.getTestExpire(type));

      tests.add(testInfo);
    }

    return sendResponseOk(new ApiResponseTestInfoList(tests, userLocale));
  }

  @RequestMapping(value = "/test/start", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> startTest(HttpServletRequest request,
      @RequestParam IqTestType type, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> user = userService.getUserByToken(token.get());
    if (!user.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    List<Question> questions = null;
    try {
      questions = this.questionsService.initQuestionsByTestType(type, userLocale);
    } catch (MegaIQException e) {
      return sendResponseError(MESSAGE_START_TEST_FAIL, configCache, userLocale);
    }
    if (questions == null || questions.isEmpty()) {
      return sendResponseError(MESSAGE_START_TEST_FAIL, configCache, userLocale);
    }

    Optional<TestResult> testResult =
        testResultService.startUserTest(user.get(), type, questions, userLocale);
    if (!testResult.isPresent()) {
      return sendResponseError(MESSAGE_START_TEST_FAIL, configCache, userLocale);
    }

    ApiTestResult apiTestResult = new ApiTestResult(this.questionsService, testResult.get(), true);

    return sendResponseTestResult(apiTestResult);
  }

  @RequestMapping(value = "/test/finish", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> submitFinish(HttpServletRequest request,
      @RequestParam UUID testCode, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (token.isPresent()) {
      Optional<User> userResult = userService.getUserByToken(token.get());

      if (userResult.isPresent()) {
        User user = userResult.get();

        Optional<TestResult> testResult = loadIqTestDetailsPrivate(testCode, user, userLocale);

        if (testResult.isPresent()) {
          Optional<TestResult> testResultNew =
              this.testResultService.submitFinish(testResult.get());

          if (testResultNew.isPresent()) {
            Boolean showIq = this.testResultService.getIsEligibleToShowIq(user);
            if (showIq) {
              if (user.getIq() == null || user.getIq() < testResult.get().getPoints()) {
                this.userService.setUserIqScore(user, testResult.get().getPoints());
              }
            }

            if (!user.getIsUnsubscribed()) {
              // STANDARD_IQ and MEGA_IQ have separate mail template
              if (testResult.get().getType().equals(IqTestType.STANDARD_IQ)
                  || testResult.get().getType().equals(IqTestType.MEGA_IQ)) {
                emailService.sendIqTestResult(user, testResult.get());
              } else {
                emailService.sendTestResult(user, testResult.get());
              }
            }

            ApiTestResult apiTestResult =
                new ApiTestResult(this.questionsService, testResultNew.get(), true);

            return sendResponseTestResult(apiTestResult);
          } else {
            return sendResponseError(MESSAGE_WRONG_REQUEST, configCache, userLocale);
          }

        } else {
          return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
        }

      } else {
        return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
      }
    } else {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }
  }

  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(HttpServletRequest request,
      @PathVariable UUID testCode, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (token.isPresent()) {
      Optional<User> userResult = userService.getUserByToken(token.get());

      if (userResult.isPresent()) {
        return iqTestDetailsPrivate(testCode, userResult.get(), userLocale);
      }
    }

    return iqTestDetailsPublic(testCode, userLocale);
  }

  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.POST,
      consumes = "application/json")
  public ResponseEntity<ApiResponseBase> submitAnswer(HttpServletRequest request,
      @PathVariable UUID testCode, @RequestBody ApiRequestSubmitAnswer requestSubmitAnswer,
      @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (token.isPresent()) {
      Optional<User> userResult = userService.getUserByToken(token.get());

      if (userResult.isPresent()) {
        Optional<TestResult> testResult =
            loadIqTestDetailsPrivate(testCode, userResult.get(), userLocale);

        if (testResult.isPresent()) {
          TestResult testResultNew = this.testResultService.submitUserAnswer(testResult.get(),
              requestSubmitAnswer.getQuestion(), requestSubmitAnswer.getAnswer());

          ApiTestResult apiTestResult =
              new ApiTestResult(this.questionsService, testResultNew, true);

          return sendResponseTestResult(apiTestResult);
        } else {
          return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
        }

      } else {
        return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
      }

    } else {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }
  }

  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.DELETE)
  public ResponseEntity<ApiResponseBase> deleteTestResult(HttpServletRequest request,
      @PathVariable UUID testCode, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);

    Optional<String> token = getTokenFromHeader(request);
    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> userCurrentResult = userService.getUserByToken(token.get());
    if (!userCurrentResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<TestResult> testResult =
        loadIqTestDetailsPrivate(testCode, userCurrentResult.get(), userLocale);

    if (!testResult.isPresent()) {
      return sendResponseBase(MESSAGE_DELETE_SUCCESS, configCache, userLocale);
    }

    this.testResultService.deleteTestResult(testResult.get());

    return sendResponseBase(MESSAGE_DELETE_SUCCESS, configCache, userLocale);
  }

  @RequestMapping(value = "/list-my", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestListUserResults(HttpServletRequest request,
      @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);

    Optional<String> token = getTokenFromHeader(request);
    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> user;
    try {
      user = userService.getUserByTokenOrRegister(token.get(), getIp(request), userLocale);
    } catch (FirebaseAuthException | MegaIQException e) {
      return sendResponseErrorRaw(e.getLocalizedMessage(), userLocale);
    }
    if (!user.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    List<TestResult> listResults = loadResultsByUserId(user.get().getId(), userLocale);

    List<ApiTestResult> usersPublicList = new ArrayList<ApiTestResult>();
    for (TestResult testResult : listResults) {
      usersPublicList.add(new ApiTestResult(this.questionsService, testResult, true));
    }

    return sendResponseTestResultList(usersPublicList, new ApiUser(user.get()));
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    Optional<TestResult> testResult = loadIqTestDetailsPrivate(testCode, user, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult =
          new ApiTestResult(this.questionsService, testResult.get(), true);

      return sendResponseTestResult(apiTestResult);
    } else {
      return sendResponseError(MESSAGE_WRONG_REQUEST, configCache, locale);
    }
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {
    Optional<TestResult> testResult = loadIqTestDetailsPublic(testCode, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult =
          new ApiTestResult(this.questionsService, testResult.get(), false);

      return sendResponseTestResult(apiTestResult);
    } else {
      return sendResponseError(MESSAGE_WRONG_REQUEST, configCache, locale);
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
