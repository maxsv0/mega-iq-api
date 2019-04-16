package com.max.appengine.springboot.megaiq.integration;

import static org.junit.Assert.assertFalse;
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
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.FirebaseService;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class UserControllerIT extends AbstractIntegrationIT {
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
  public void testNewUserCreateAndDelete() throws Exception {
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    log.info("MvcResult is = {}", resultApi.getResponse().getContentAsString());
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

    firebaseService.deleteUser(user);
  }
  
  @Test
  public void testCreateUserTwice() throws Exception {
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    log.info("MvcResult is = {}", resultApi.getResponse().getContentAsString());

    UserRecord userRecord = firebaseService.getUserRecord(user);
    user.setUid(userRecord.getUid());
    
    MvcResult resultApiFail = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();
    log.info("MvcResult fail is = {}", resultApiFail.getResponse().getContentAsString());
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseBase responseBase =
        objectMapper.readValue(resultApiFail.getResponse().getContentAsString(), ApiResponseBase.class);
    log.info("responseBase = {}", responseBase);
    
    firebaseService.deleteUser(user);
    
    assertFalse(responseBase.isOk());
    assertNotNull(responseBase.getMsg());
    assertNotNull(responseBase.getLocale());
    assertNotNull(responseBase.getDate());
  }
}
