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

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.QuestionGroupsResult;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class EmailServiceTest extends AbstractUnitTest {

  @Autowired
  private EmailService emailService;

  @Test
  public void testEmailNewUserRegistration() {
    List<UserTokenType> tokens = new ArrayList<UserTokenType>();
    tokens.add(UserTokenType.ACCESS);

    User user = generateUserWithTokens(tokens);

    boolean result = this.emailService.sendEmailRegistration(user);
    assertTrue(result);
  }

  @Test
  public void testEmailRegistrationWithVerify() {
    List<UserTokenType> tokens = new ArrayList<UserTokenType>();
    tokens.add(UserTokenType.ACCESS);
    tokens.add(UserTokenType.VERIFY);

    User user = generateUserWithTokens(tokens);

    boolean result = this.emailService.sendEmailRegistrationWithVerify(user);
    assertTrue(result);
  }

  @Test
  public void testEmailVerify() {
    List<UserTokenType> tokens = new ArrayList<UserTokenType>();
    tokens.add(UserTokenType.ACCESS);
    tokens.add(UserTokenType.VERIFY);

    User user = generateUserWithTokens(tokens);

    boolean result = this.emailService.sendEmailVerify(user);
    assertTrue(result);
  }
  
  @Test
  public void testEmailForget() {
    List<UserTokenType> tokens = new ArrayList<UserTokenType>();
    tokens.add(UserTokenType.ACCESS);

    User user = generateUserWithTokens(tokens);

    boolean result = this.emailService.sendEmailForget(user);
    assertTrue(result);
  }
  
  @Test
  public void testSendTestResult() {
    List<UserTokenType> tokens = new ArrayList<UserTokenType>();
    tokens.add(UserTokenType.ACCESS);
    tokens.add(UserTokenType.VERIFY);

    User user = generateUserWithTokens(tokens);

    UUID code = UUID.randomUUID();
    TestResult testUserResultFinished = new TestResult(1, code, "/iqtest/result/" + code,
        user.getId(), IqTestType.MEGA_IQ, IqTestStatus.FINISHED, new Date(), new Date(), new Date(),
        150, new QuestionGroupsResult(1, 1, 1, 1), Locale.EN);

    boolean result = this.emailService.sendTestResult(user, testUserResultFinished);
    assertTrue(result);
  }

  private User generateUserWithTokens(List<UserTokenType> tokenTypes) {
    User user = new User("max.svistunov@gmail.com", "Max", "/user/1", "pic",
        "city", 40, 150, true, UUID.randomUUID().toString(), "ip", 0, Locale.EN);
    user.setId(1);

    Date dateNow = new Date();
    Date dateExpire = new Date(dateNow.getTime() + (1000 * 60 * 60 * 24 * 7));

    user.setTokenList(new ArrayList<UserToken>());

    for (UserTokenType type : tokenTypes) {
      UserToken testToken =
          new UserToken(user.getId(), type, UUID.randomUUID().toString(), dateNow, dateExpire);
      user.getTokenList().add(testToken);
    }

    return user;
  }
}
