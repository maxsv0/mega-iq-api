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

package com.max.appengine.springboot.megaiq.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.service.FirebaseService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FirebaseServiceTest extends AbstractUnitTest {

  @Autowired
  private FirebaseService firebaseService;

  @Test(expected = FirebaseAuthException.class)
  public void firebaseUserNotExistThrowException() throws FirebaseAuthException {
    User user = generateUser();
    firebaseService.getUserRecord(user);
  }

  @Test
  public void testAddUserAndDelete() throws FirebaseAuthException {
    User user = generateUser();
    UserRecord userResult = firebaseService.createUser(user);

    assertNotNull(userResult.getUid());
    assertEquals(user.getName(), userResult.getDisplayName());
    assertEquals(user.getEmail(), userResult.getEmail());
    assertEquals(user.getPic(), userResult.getPhotoUrl());
    assertEquals(user.getIsEmailVerified(), userResult.isEmailVerified());

    // save UID to user object
    user.setUid(userResult.getUid());
    firebaseService.deleteUser(user);
  }
}
