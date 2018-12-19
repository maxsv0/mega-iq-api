package com.max.appengine.springboot.megaiq.rest;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseError;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.ApiService;
import com.max.appengine.springboot.megaiq.service.QuestionsService;

@RestController
public class ApiTestController extends AbstractApiController {
  public static final String MESSAGE_INVALID_ACCESS = "Can't access. Please log in again";

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
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }

    Optional<TestResult> testResult = serviceApi.startUserTest(type, user.get(), userLocale);
    if (!testResult.isPresent()) {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }

    ApiTestResult apiTestResult = new ApiTestResult(serviceQuestions, testResult.get(), true);

    return new ResponseEntity<ApiResponseBase>(new ApiResponseTestResult(apiTestResult),
        HttpStatus.OK);
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

  private ResponseEntity<ApiResponseBase> iqTestDetailsPrivate(UUID testCode, User user,
      Locale locale) {

    Optional<TestResult> testResult = serviceApi.iqTestDetailsPrivate(testCode, user, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult = new ApiTestResult(serviceQuestions, testResult.get(), true);

      ApiResponseBase resultResponse = new ApiResponseTestResult(apiTestResult);

      return new ResponseEntity<ApiResponseBase>(resultResponse, HttpStatus.OK);
    } else {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }
  }

  private ResponseEntity<ApiResponseBase> iqTestDetailsPublic(UUID testCode, Locale locale) {
    Optional<TestResult> testResult = serviceApi.iqTestDetailsPublic(testCode, locale);

    if (testResult.isPresent()) {
      ApiTestResult apiTestResult = new ApiTestResult(serviceQuestions, testResult.get(), false);

      ApiResponseBase resultResponse = new ApiResponseTestResult(apiTestResult);

      return new ResponseEntity<ApiResponseBase>(resultResponse, HttpStatus.OK);
    } else {
      return new ResponseEntity<ApiResponseBase>(new ApiResponseError("Wrong request"),
          HttpStatus.OK);
    }
  }
}
