package com.max.appengine.springboot.megaiq.integration.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.integration.AbstractIntegrationTest;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.FirebaseService;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@SqlGroup({
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:beforeTestRun.sql"),
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:afterTestRun.sql") })
public class ApiControllerTest extends AbstractIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private ConfigurationService configurationService;
  
  private User user;
  
  @Before
  public void setup() {
    user = generateUser(); 

    this.configurationService.reloadConfiguration();
    
    new MockUp<EmailService>() {
      @Mock
      protected boolean sendEmail(String to, String subject, String content) {
        return true;
      }
    };
  }
  
  @Test
  public void testNewUser() throws Exception {
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    log.info("MvcResult is = {}", resultApi.getResponse().getContentAsString());
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseUser responseUser =
        objectMapper.readValue(resultApi.getResponse().getContentAsString(), ApiResponseUser.class);
    log.info("MvcResultUser = {}", responseUser);
    
    UserRecord userRecord = firebaseService.getUserRecord(user);
    user.setUid(userRecord.getUid());
    
    firebaseService.deleteUser(user);
  }
}
