package com.max.appengine.springboot.megaiq.integration.service;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
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
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.integration.AbstractIntegrationTest;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.repository.UserTokenReporitory;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ApiControllerTest extends AbstractIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserTokenReporitory userTokenReporitory;
  
  @Test
  public void testNewUser() throws Exception {
    User user = new User();
    user.setTokenList(new ArrayList<UserToken>());
    user.setEmail("test@sometestemail.com");

    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    log.info("MvcResult is = {}", resultApi.getResponse().getContentAsString());
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseUser responseUser =
        objectMapper.readValue(resultApi.getResponse().getContentAsString(), ApiResponseUser.class);
    log.info("MvcResultUser = {}", responseUser);
    
    user.generateToken(userTokenReporitory, UserTokenType.ACCESS);
    log.info("user = {}", user);
    
    ApiResponseUser responseUserExpected = new ApiResponseUser(user);
    log.info("responseUserExpected = {}", user);
    assertEquals(responseUserExpected, responseUser);
  }

 
}
