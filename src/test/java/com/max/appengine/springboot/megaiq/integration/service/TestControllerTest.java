package com.max.appengine.springboot.megaiq.integration.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.integration.AbstractIntegrationTest;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.FirebaseService;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class TestControllerTest extends AbstractIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private FirebaseService firebaseService;

  private User user;

  @Before
  public void setup() {
    user = generateUser();

    new MockUp<EmailService>() {
      @Mock
      protected boolean sendEmail(String to, String subject, String content) {
        return true;
      }
    };
  }

  @Test
  public void testPassMegaIqTestWithNewUser() throws Exception {
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseUser responseUser =
        objectMapper.readValue(resultApi.getResponse().getContentAsString(), ApiResponseUser.class);
    log.info("MvcResultUser = {}", responseUser);

    assertTrue(responseUser.isOk());
    assertNull(responseUser.getMsg());
    assertNotNull(responseUser.getLocale());
    assertNotNull(responseUser.getDate());
    assertNotNull(responseUser.getUser().getToken());

    UserRecord userRecord = firebaseService.getUserRecord(user);
    user.setUid(userRecord.getUid());

//    // start test
//    MvcResult resultApiTest = mvc
//        .perform(MockMvcRequestBuilders.get("/test/start?type=" + IqTestType.MEGA_IQ + "&locale=EN")
//            .header("Authorization", "Bearer " + responseUser.getUser().getToken()))
//        .andReturn();
//    objectMapper = new ObjectMapper();
//    ApiResponseTestResult responseTest = objectMapper
//        .readValue(resultApiTest.getResponse().getContentAsString(), ApiResponseTestResult.class);
//    log.info("responseTest = {}", responseTest);
//
//    assertTrue(responseTest.isOk());
//    assertNull(responseTest.getMsg());
//    assertNotNull(responseTest.getLocale());
//    assertNotNull(responseTest.getDate());

    firebaseService.deleteUser(user);
  }

}
