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

package com.max.appengine.springboot.megaiq.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public abstract class AbstractIntegrationTest {

  protected static final Logger log = LoggerFactory.getLogger(AbstractIntegrationTest.class);

  @Rule
  public TestName name = new TestName();

  @Before
  public void printTestStart() {
    log.info("IT Started: {}.{}", name.getClass(), name.getMethodName());
  }

  @After
  public void printTestEnd() {
    log.info("IT Ends: {}.{}", name.getClass(), name.getMethodName());
  }

  public static String asJsonString(final Object obj) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected User generateUser() {
    User user = new User("java-build-test+" + Math.random() + "@mega-iq.com", "TEST", "/user/1",
        "https://lh3.googleusercontent.com/INTuvwHpiXTigV8UQWi5MpSaRt-0mimAQL_eyfGMOynRK_USId0_Z45KFIrKI3tp21J_q6panwRUfrDOBAqHbA",
        "city", 40, 150, true, UUID.randomUUID().toString(), "ip", 0, Locale.EN);

    user.setToken(UUID.randomUUID().toString());
    user.setIsEmailVerified(true);

    assertNull(user.getUid());
    assertNotNull(user.getName());
    assertNotNull(user.getEmail());
    assertNotNull(user.getPic());
    assertNotNull(user.getIsEmailVerified());

    return user;
  }

}
