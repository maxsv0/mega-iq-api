package com.max.appengine.springboot.megaiq.unit.service;

import static org.junit.Assert.assertEquals;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.FirebaseService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FirebaseServiceTest extends AbstractUnitTest {

  @Autowired
  private FirebaseService firebaseService;

  @Test
  public void testAddUser() throws FirebaseAuthException {
    User user = generateUser();
    UserRecord userResult = firebaseService.createUser(user);

    assertEquals(user.getId(), userResult.getUid());
    assertEquals(user.getName(), userResult.getDisplayName());
    assertEquals(user.getEmail(), userResult.getEmail());
    assertEquals(user.getPic(), userResult.getPhotoUrl());
    assertEquals(user.getIsEmailVerified(), userResult.isEmailVerified());
  }

  private User generateUser() {
    User user = new User("max.svistunov@gmail.com", "Max", "/user/1",
        "https://lh3.googleusercontent.com/INTuvwHpiXTigV8UQWi5MpSaRt-0mimAQL_eyfGMOynRK_USId0_Z45KFIrKI3tp21J_q6panwRUfrDOBAqHbA",
        "city", 40, 150, true, UUID.randomUUID().toString(), "ip", 0, Locale.EN);
    user.setId(1);
    user.setIsEmailVerified(true);
    return user;
  }
}
