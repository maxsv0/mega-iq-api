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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
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
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.repository.UserTokenReporitory;
import com.max.appengine.springboot.megaiq.service.UserService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest extends AbstractUnitTest {
  private static final String USER_PASSWORD = "test";
  private static final String USER_PASSWORD_HASH = "098f6bcd4621d373cade4e832627b4f6";

  @Autowired
  private UserReporitory userReporitory;

  @Autowired
  private UserTokenReporitory userTokenReporitory;

  private UserService userService;

  private User testUserPublic;

  private UserToken testTokenAccess;

  private UserToken testTokenForget;

  @Before
  public void doSetup() {
    this.userService = new UserService(userReporitory, userTokenReporitory);

    testUserPublic = new User(1, "test@test.email", "test", "url", "pic", "city", 40, 150, true,
        USER_PASSWORD_HASH, "ip", 0, Locale.EN);
    userReporitory.save(testUserPublic);

    Date dateNow = new Date();
    Date dateExpire = new Date(dateNow.getTime() + (1000 * 60 * 60 * 24 * 7));

    testTokenAccess = new UserToken(1, 1, UserTokenType.ACCESS, UUID.randomUUID().toString(),
        dateNow, dateExpire);
    userTokenReporitory.save(testTokenAccess);

    testTokenForget = new UserToken(2, 1, UserTokenType.FORGET, UUID.randomUUID().toString(),
        dateNow, dateExpire);
    userTokenReporitory.save(testTokenForget);
  }

  @After
  public void doFinish() {
    userTokenReporitory.delete(testTokenAccess);
    userTokenReporitory.delete(testTokenForget);
    userReporitory.delete(testUserPublic);
  }

  @Test
  public void testUserServiceBasis() {
    Optional<User> userResult = this.userService.getUserById(testUserPublic.getId());
    assertTrue(userResult.isPresent());
    assertEquals(testUserPublic, userResult.get());

    userResult = this.userService.getUserByToken(testTokenAccess.getValue(), UserTokenType.ACCESS);
    assertTrue(userResult.isPresent());
    assertEquals(testUserPublic, userResult.get());

    userResult = this.userService.getUserByToken(testTokenForget.getValue(), UserTokenType.ACCESS);
    assertFalse(userResult.isPresent());
  }

  @Test
  public void testUserAuth() {
    Optional<User> userResult = this.userService.authUserLogin(testUserPublic.getEmail(), USER_PASSWORD);
    assertTrue(userResult.isPresent());
    assertEquals(testUserPublic, userResult.get());
    assertEquals(testTokenAccess, userResult.get().getUserTokenByType(UserTokenType.ACCESS));
    assertEquals(testTokenForget, userResult.get().getUserTokenByType(UserTokenType.FORGET));

    userResult = this.userService.authUserLogin(testUserPublic.getEmail(), USER_PASSWORD+"123");
    assertFalse(userResult.isPresent());
  }
}
