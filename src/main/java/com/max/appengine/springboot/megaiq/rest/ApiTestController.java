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
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.ApiService;
import com.max.appengine.springboot.megaiq.service.QuestionsService;

@RestController
public class ApiTestController extends AbstractApiController {
  private final ApiService serviceApi;
  
  private final QuestionsService serviceQuestions;

  @Autowired
  public ApiTestController(ApiService service, QuestionsService serviceQuestions) {
    this.serviceApi = service;
    this.serviceQuestions = serviceQuestions;
  }

  @RequestMapping(value = "/test/start", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(HttpServletRequest request,
      @RequestParam IqTestType type, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    Optional<User> user = serviceApi.getUserByToken(token.get(), userLocale);
    if (!user.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }

    Optional<TestResult> testResult = serviceApi.startUserTest(type, user.get(), userLocale);
    if (!testResult.isPresent()) {
      return sendResponseError(MESSAGE_WRONG_REQUEST);
    }

    ApiTestResult apiTestResult = new ApiTestResult(serviceQuestions, testResult.get(), true);

    return sendResponseTestResult(apiTestResult);
  }

  @RequestMapping(value = "/test/{testCode}", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> requestTestDetails(HttpServletRequest request,
      @PathVariable UUID testCode, @RequestParam Optional<String> locale) {

    Locale userLocale = loadLocale(locale);
    Optional<String> token = getTokenFromHeader(request);

    if (token.isPresent()) {
      Optional<User> user = serviceApi.getUserByToken(token.get(), userLocale);

      if (user.isPresent()) {
        return iqTestDetailsPrivate(testCode, user.get(), userLocale);
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
    
    Optional<User> user = serviceApi.getUserByToken(token.get(), userLocale);
    if (!user.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS);
    }
    
    List<TestResult> listResults = serviceApi.loadAllResults(user.get().getId(), userLocale);
    
    List<ApiTestResult> usersPublicList = new ArrayList<ApiTestResult>();
    for (TestResult testResult : listResults) {
      usersPublicList.add(new ApiTestResult(serviceQuestions, testResult, true));
    }
    
    return sendResponseTestResultList(usersPublicList);
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    Optional<TestResult> testResult = serviceApi.iqTestDetailsPrivate(testCode, user, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult = new ApiTestResult(serviceQuestions, testResult.get(), true);

      return sendResponseTestResult(apiTestResult);
    } else {
      return sendResponseError(MESSAGE_WRONG_REQUEST);
    }
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {
    Optional<TestResult> testResult = serviceApi.iqTestDetailsPublic(testCode, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult = new ApiTestResult(serviceQuestions, testResult.get(), false);

      return sendResponseTestResult(apiTestResult);
    } else {
      return sendResponseError(MESSAGE_WRONG_REQUEST);
    }
  }
}
