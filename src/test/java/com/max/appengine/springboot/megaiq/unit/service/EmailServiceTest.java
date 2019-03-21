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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.ConfigurationReporitory;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class EmailServiceTest extends AbstractUnitTest {
  private EmailService emailService;

  private ConfigurationService configurationService;

  @Autowired
  private ConfigurationReporitory configurationReporitory;

  @Before
  public void doSetup() {

    new MockUp<EmailService>() {
      @Mock
      protected boolean sendEmail(String to, String subject, String content) {
        return true;
      }
    };

    generateConfig(this.configurationReporitory);

    this.configurationService = new ConfigurationService(this.configurationReporitory);
    this.emailService = new EmailService(this.configurationService);
  }

  @Test
  public void checkAllEmails() {
    for (Locale locale : Locale.values()) {
      User user = generateUser(locale);

      boolean result = testEmailNewUserRegistration(user);
      assertTrue(result);

      result = testEmailRegistrationWithVerify(user);
      assertTrue(result);

      result = testEmailVerify(user);
      assertTrue(result);

      result = testEmailForget(user);
      assertTrue(result);

      result = testEmailDirectLogin(user);
      assertTrue(result);

      result = testSendIqTestResult(user);
      assertTrue(result);

      result = testSendTestResult(user);
      assertTrue(result);
    }
  }

  public boolean testEmailNewUserRegistration(User user) {
    return this.emailService.sendEmailRegistration(user);
  }

  public boolean testEmailRegistrationWithVerify(User user) {
    return this.emailService.sendEmailRegistrationWithVerify(user, "http://mega-iq.com");
  }

  public boolean testEmailVerify(User user) {
    return this.emailService.sendEmailVerify(user, "http://mega-iq.com");
  }

  public boolean testEmailForget(User user) {
    return this.emailService.sendEmailForget(user, "http://mega-iq.com");
  }

  public boolean testEmailDirectLogin(User user) {
    return this.emailService.sendEmailDirectLogin(user);
  }

  public boolean testSendIqTestResult(User user) {
    TestResult testUserResultFinished = generateTestResult(user);

    return this.emailService.sendIqTestResult(user, testUserResultFinished);
  }

  public boolean testSendTestResult(User user) {
    TestResult testUserResultFinished = generateTestResult(user);

    return this.emailService.sendTestResult(user, testUserResultFinished);
  }
}
