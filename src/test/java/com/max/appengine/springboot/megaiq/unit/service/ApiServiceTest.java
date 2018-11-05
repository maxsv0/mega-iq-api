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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Date;
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
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.service.ApiService;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import com.max.appengine.springboot.megaiq.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApiServiceTest {
  @Autowired
  private QuestionsService qestionsService;

  @Autowired
  private TestResultService testResultService;

  @Autowired
  private UserService userService;

  private ApiService apiService;

  private String tokenUser;

  @Before
  public void doSetup() {
    this.apiService = new ApiService(qestionsService, testResultService, userService);

    User testUser = new User(1, "test@test.email", "test", "url", "pic", "city", 40, 150, true, "",
        "ip", 0, Locale.EN);

    Date dateNow = new Date();
    Date dateExpire = new Date(dateNow.getTime() + (1000 * 60 * 60 * 24 * 7));
    this.tokenUser = UUID.randomUUID().toString();

    UserToken testTokenAccess =
        new UserToken(1, UserTokenType.ACCESS, this.tokenUser, dateNow, dateExpire);

    testUser.setTokenList(new ArrayList<UserToken>());
    testUser.getTokenList().add(testTokenAccess);

    apiService.addNewUser(testUser);
  }

  @Test
  public void testTestResultsServiceBasis() {
    Optional<User> userResult = this.apiService.getUserByToken(this.tokenUser, Locale.EN);
    assertTrue(userResult.isPresent());
    assertEquals(tokenUser, userResult.get().getUserTokenByType(UserTokenType.ACCESS).getValue());
    
    TestResult testResult = this.apiService.startUserTest(IqTestType.MEGA_IQ, userResult.get(), Locale.EN);
    assertNotNull(testResult);
    assertEquals(userResult.get(), testResult.getUser());
  }
}
