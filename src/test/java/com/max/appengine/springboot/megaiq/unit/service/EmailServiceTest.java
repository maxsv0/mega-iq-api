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
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
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
    User user = new User("max.svistunov@gmail.com", "Max Svistunov", "url", "pic", "city", 40, 150,
        true, UUID.randomUUID().toString(), "ip", 0, Locale.EN);

    Date dateNow = new Date();
    Date dateExpire = new Date(dateNow.getTime() + (1000 * 60 * 60 * 24 * 7));

    user.setTokenList(new ArrayList<UserToken>());
    
    UserToken testTokenAccess = new UserToken(user.getId(), UserTokenType.ACCESS,
        UUID.randomUUID().toString(), dateNow, dateExpire);
    user.getTokenList().add(testTokenAccess);

    UserToken testTokenForget = new UserToken(user.getId(), UserTokenType.FORGET,
        UUID.randomUUID().toString(), dateNow, dateExpire);
    user.getTokenList().add(testTokenForget);

    UserToken testTokenVerify = new UserToken(user.getId(), UserTokenType.VERIFY,
        UUID.randomUUID().toString(), dateNow, dateExpire);
    user.getTokenList().add(testTokenVerify);

    boolean result = this.emailService.sendEmailRegistration(user, Locale.EN);
    assertTrue(result);
  }
}
