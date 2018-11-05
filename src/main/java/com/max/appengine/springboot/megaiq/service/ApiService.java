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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;

@Service
public class ApiService {

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

    return result;
  }

  public Optional<TestResult> iqTestDetailsPublic(UUID testCode, Locale locale) {
    return loadFullResultData(testCode, locale);
  }

  public Optional<TestResult> iqTestDetailsPrivate(UUID testCode, User user, Locale locale) {
    Optional<TestResult> resultData = loadFullResultData(testCode, locale);
    if (!resultData.isPresent()) {
      return resultData;
    }

    // private result can be requested only be user himself
    if (!user.getId().equals(resultData.get().getUserId())) {
      return Optional.empty();
    } else {
      return resultData;
    }
  }

  public TestResult startUserTest(IqTestType testType, User user, Locale locale) {
   
    ArrayList<Question> quesions = qestionsService.getQuestionsSet(testType, locale);
    // TODO: code here
    
    return null;
  }


  public Optional<User> userLogin(String login, String password) {
    return userService.authUserLogin(login, password);
  }

  public Optional<User> getUserByToken(String token, Locale locale) {
    Optional<User> userResult = userService.getUserByToken(token, UserTokenType.ACCESS);
    
    if (!userResult.isPresent()) return userResult;
        
    User user = userResult.get();
    user.setTestResultList(loadAllResults(user.getId(), locale));
    
    return Optional.of(user);
  }

  private Optional<TestResult> loadFullResultData(UUID testCode, Locale locale) {
    return testResultService.getTestResultByCode(testCode, locale);
  }
  
  private List<TestResult> loadAllResults(Integer userId, Locale locale) {
    return testResultService.findByUserId(userId, locale);
  }

  private Optional<User> getUserById(Integer userId) {
    return userService.getUserById(userId);
  }
}
