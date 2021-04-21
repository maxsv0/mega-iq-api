/*
 * Copyright 2019 mega-iq.com
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.After;
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
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalImagesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.firebase.auth.UserRecord;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResultList;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUser;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseUsersTop;
import com.max.appengine.springboot.megaiq.model.api.ApiUserPublic;
import com.max.appengine.springboot.megaiq.model.api.ApiUserTop;
import com.max.appengine.springboot.megaiq.model.api.ImportUserTest;
import com.max.appengine.springboot.megaiq.model.api.ImportUserTestGroup;
import com.max.appengine.springboot.megaiq.model.api.RequestImportUser;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.repository.TestResultReporitory;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.service.CertificateService;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.FirebaseService;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class UserControllerIT extends AbstractIntegrationIT {
  private static final LocalServiceTestHelper helperServices =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
          new LocalBlobstoreServiceTestConfig(), new LocalImagesServiceTestConfig());

  @Autowired
  private MockMvc mvc;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private TestResultReporitory testResultReporitory;

  @Autowired
  private UserReporitory userReporitory;

  private User user;

  ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setup() {
    user = generateUser();

    new MockUp<EmailService>() {
      @Mock
      protected boolean sendEmail(String to, String subject, String content) {
        return true;
      }
    };

    helperServices.setUp();

    // TODO: better to upload this file to cloud storage
    new MockUp<CertificateService>() {
      @Mock
      public BufferedImage loadTemplate(String fileName) throws IOException {
        InputStream imageTemplate =
            this.getClass().getClassLoader().getResourceAsStream("cert_blank.png");
        return ImageIO.read(imageTemplate);
      }
    };
  }

  @After
  public void tearDown() throws Exception {
    helperServices.tearDown();
  }

  @Test
  public void testNewUserCreateAndDelete() throws Exception {
    ApiResponseUser responseUser = createNewUser(user);

    assertTrue(responseUser.isOk());
    assertNull(responseUser.getMsg());
    assertNotNull(responseUser.getLocale());
    assertNotNull(responseUser.getDate());
    //assertNotNull(responseUser.getUser().getToken());

    UserRecord userRecord = firebaseService.getUserRecord(user);
    user.setUid(userRecord.getUid());

    firebaseService.deleteUser(user);
  }

  @Test
  public void testCreateUserTwice() throws Exception {
    createNewUser(user);

    UserRecord userRecord = firebaseService.getUserRecord(user);
    user.setUid(userRecord.getUid());

    MvcResult resultApiFail = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();
    log.info("MvcResult fail is = {}", resultApiFail.getResponse().getContentAsString());
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseBase responseBase = objectMapper
        .readValue(resultApiFail.getResponse().getContentAsString(), ApiResponseBase.class);
    log.info("responseBase = {}", responseBase);

    firebaseService.deleteUser(user);

    assertFalse(responseBase.isOk());
    assertNotNull(responseBase.getMsg());
    assertNotNull(responseBase.getLocale());
    assertNotNull(responseBase.getDate());
  }

  @Test
  public void testUsersTop() throws Exception {
    User userOther = generateUser();
    userOther.setCreateDate(new Date());
    userOther.setUpdateDate(new Date());
    this.userReporitory.save(userOther);
    
    User user = generateUser();
    user.setCreateDate(new Date());
    user.setUpdateDate(new Date());
    user = this.userReporitory.save(user);

    TestResult testResult = generateTestResult(user.getId(), IqTestType.MEGA_IQ, user.getLocale());
    testResultReporitory.save(testResult);
    
    TestResult testResultOther = generateTestResult(userOther.getId(), IqTestType.MEGA_IQ, userOther.getLocale());
    testResultReporitory.save(testResultOther);

    MvcResult resultApi = mvc.perform(MockMvcRequestBuilders.get("/user/top")).andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseUsersTop response = objectMapper
        .readValue(resultApi.getResponse().getContentAsString(), ApiResponseUsersTop.class);

    log.info("response top = {}", response);

    assertTrue(foundInList(response.getExampleProfiles(), user.getId()));

    assertTrue(foundInList(response.getUsers(), user.getId()));

    boolean foundUser = false;
    for (ApiUserTop userFound : response.getUsersTop()) {
      if (userFound.getId().equals(user.getId())) {
        foundUser = true;
        break;
      }
    }
    assertTrue("UserID=" + user.getId() + " not found is UsersTop of response= " + response, foundUser);
  }

  @Test
  public void testUserImport() throws Exception {
    User user = generateUser();
    user.setId(99999);
    user.setCreateDate(new Date());

    RequestImportUser importUser = new RequestImportUser();
    importUser.setId(user.getId());
    importUser.setLocale(user.getLocale());
    importUser.setEmail(user.getEmail());
    importUser.setName(user.getName());
    importUser.setCreateDate(user.getCreateDate());
    importUser.setLocation(user.getLocation());
    importUser.setAge(user.getAge());
    importUser.setIp(user.getIp());
    importUser.setIq(user.getIq());
    importUser.setIsPublic(user.getIsPublic());
    importUser.setPic(user.getPic());
    
    List<ImportUserTest> testList = new ArrayList<ImportUserTest>();
    
    for (IqTestType type : IqTestType.values()) {
      testList.add(new ImportUserTest(type, new Date(), new Date(), user.getIq(),
          new ImportUserTestGroup(55.3, 99.0, 11.2, 55.3)));
    }
    
    importUser.setTests(testList);
    
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/import").content(asJsonString(importUser))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiResponseUser response =
        objectMapper.readValue(resultApi.getResponse().getContentAsString(), ApiResponseUser.class);

    log.info("response import = {}", response);
    assertTrue(response.isOk());
    assertNull(response.getMsg());
    assertNotNull(response.getUser());
    assertNotNull(response.getUser().getCertificate());

    MvcResult resultApiUser =
        mvc.perform(MockMvcRequestBuilders.get("/user/" + user.getId())).andReturn();

    ApiResponseTestResultList responseUser = objectMapper.readValue(
        resultApiUser.getResponse().getContentAsString(), ApiResponseTestResultList.class);
    log.info("response user = {}", responseUser);
    assertTrue(responseUser.isOk());
    assertNull(responseUser.getMsg());
    assertNotNull(responseUser.getUser());
    assertNotNull(responseUser.getUser().getCertificate());
    assertEquals(IqTestType.values().length, responseUser.getTests().size());

    UserRecord userRecord = firebaseService.getUserRecord(user);
    user.setUid(userRecord.getUid());
    firebaseService.deleteUser(user);
  }

  private ApiResponseUser createNewUser(User user) throws Exception {
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.post("/user/new").content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    log.info("User create result = {}", resultApi.getResponse().getContentAsString());
    ObjectMapper objectMapper = new ObjectMapper();

    return objectMapper.readValue(resultApi.getResponse().getContentAsString(),
        ApiResponseUser.class);
  }

  private boolean foundInList(List<ApiUserPublic> listProfiles, Integer id) {
    log.info("Search for User Id={} in a list = {}", id, listProfiles);

    boolean foundUser = false;
    for (ApiUserPublic userFound : listProfiles) {
      if (userFound.getId().equals(id)) {
        foundUser = true;
        break;
      }
    }

    log.info("Search for User in a list = {}, result = {}", listProfiles, foundUser);

    return foundUser;
  }
}
