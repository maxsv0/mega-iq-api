package com.max.appengine.springboot.megaiq.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.service.UserService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest extends AbstractUnitTest {

  @Autowired
  private UserReporitory userReporitory;

  private UserService userService;

  private User testUserPublic;

  @Before
  public void doSetup() {
    this.userService = new UserService(userReporitory);

    testUserPublic = new User(1, "test@test.email", "test", "url", "pic", "city", 40, 150, true, "",
        0, Locale.EN);
    userReporitory.save(testUserPublic);
  }

  @Test
  public void testQuestionsServiceBasis() {
    Optional<User> user = this.userService.getUserById(1);
    assertTrue(user.isPresent());
    assertEquals(testUserPublic, user.get());
  }
}
