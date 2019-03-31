package com.max.appengine.springboot.megaiq.integration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import java.util.UUID;
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
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.integration.AbstractIntegrationTest;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiQuestion;
import com.max.appengine.springboot.megaiq.model.api.ApiRequestSubmitAnswer;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseTestResult;
import com.max.appengine.springboot.megaiq.model.api.ApiTestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.service.EmailService;
import com.max.appengine.springboot.megaiq.service.UserService;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class TestControllerTest extends AbstractIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserReporitory userReporitory;

  private User user;

  @Before
  public void setup() {
    user = userReporitory.save(generateUser());

    new MockUp<EmailService>() {
      @Mock
      protected boolean sendEmail(String to, String subject, String content) {
        return true;
      }
    };

    new MockUp<UserService>() {
      @Mock
      public Optional<User> getUserByToken(String token) {
        return Optional.of(user);
      }
    };
  }

  @Test
  public void testPassPractivIqTest() throws Exception {
    ApiTestResult testResult = startTestAndFinish(IqTestType.PRACTICE_IQ);

    Integer answersCorrect = 0;
    for (ApiQuestion question : testResult.getQuestionSet()) {
      assertNotNull(question.getAnswerUser());

      if (question.getAnswerCorrect().equals(question.getAnswerUser())) {
        answersCorrect++;
      }
    }

    assertEquals(answersCorrect, testResult.getPoints());
    assertNull(testResult.getGroupsGraph());
  }
  
  @Test
  public void testPassStandardIqTest() throws Exception {
    ApiTestResult testResult = startTestAndFinish(IqTestType.STANDARD_IQ);
    assertTrue(testResult.getPoints() > 80);

    Integer answersCorrect = 0;
    for (ApiQuestion question : testResult.getQuestionSet()) {
      assertNotNull(question.getAnswerUser());

      if (question.getAnswerCorrect().equals(question.getAnswerUser())) {
        answersCorrect++;
      }
    }
    
    assertTrue(answersCorrect > 0);
    assertNotNull(testResult.getGroupsGraph());
    assertTrue((testResult.getGroupsGraph().getGrammar() * testResult.getGroupsGraph().getHorizons()
        * testResult.getGroupsGraph().getLogic() * testResult.getGroupsGraph().getMath()) > 0);
  }

  @Test
  public void testPassMegaIqTest() throws Exception {
    ApiTestResult testResult = startTestAndFinish(IqTestType.MEGA_IQ);
    assertTrue(testResult.getPoints() > 80);

    Integer answersCorrect = 0;
    for (ApiQuestion question : testResult.getQuestionSet()) {
      assertNotNull(question.getAnswerUser());

      if (question.getAnswerCorrect().equals(question.getAnswerUser())) {
        answersCorrect++;
      }
    }
    
    assertTrue(answersCorrect > 0);
    assertNotNull(testResult.getGroupsGraph());
    assertTrue((testResult.getGroupsGraph().getGrammar() * testResult.getGroupsGraph().getHorizons()
        * testResult.getGroupsGraph().getLogic() * testResult.getGroupsGraph().getMath()) > 0);
  }

  @Test
  public void testSendIncorrectQuestionId() throws Exception {
    // 1. start the test
    ApiResponseTestResult responseTest = startTest(IqTestType.MATH);
    
    assertTrue(responseTest.isOk());
    assertNull(responseTest.getMsg());
    assertNotNull(responseTest.getLocale());
    assertNotNull(responseTest.getDate());
    assertNotNull(responseTest.getTest());
    assertEquals(IqTestStatus.ACTIVE, responseTest.getTest().getStatus());
    
    // 2. send incorrect request
    ApiResponseTestResult responseTestAnswer =
        testAnswerQuestion(responseTest.getTest().getCode(), -1, 1);
    
    assertTrue(responseTest.isOk());
    assertNull(responseTest.getMsg());
    assertNotNull(responseTestAnswer.getTest());
    
    assertEquals(responseTest.getTest(), responseTestAnswer.getTest());
  }

  private ApiTestResult startTestAndFinish(IqTestType type) throws Exception {
    // 1. start the test
    ApiResponseTestResult responseTest = startTest(type);

    assertTrue(responseTest.isOk());
    assertNull(responseTest.getMsg());
    assertNotNull(responseTest.getLocale());
    assertNotNull(responseTest.getDate());
    assertNotNull(responseTest.getTest());
    assertEquals(IqTestStatus.ACTIVE, responseTest.getTest().getStatus());

    // 2. answer all questions
    Integer index = 1;
    for (ApiQuestion question : responseTest.getTest().getQuestionSet()) {
      log.info("Now answer the question={}", question);
      Integer answerUser = 1;

      ApiResponseTestResult responseTestAnswer =
          testAnswerQuestion(responseTest.getTest().getCode(), index, answerUser);

      assertTrue(responseTestAnswer.isOk());
      assertNull(responseTestAnswer.getMsg());
      assertNotNull(responseTestAnswer.getTest());
      assertEquals(IqTestStatus.ACTIVE, responseTest.getTest().getStatus());
      assertEquals(answerUser,
          responseTestAnswer.getTest().getQuestionSet().get(index - 1).getAnswerUser());
      index++;
    }

    // 3. submit finish test
    ApiResponseTestResult responseFinish = testFinish(responseTest.getTest().getCode());
    assertTrue(responseFinish.isOk());
    assertNull(responseFinish.getMsg());
    assertNotNull(responseFinish.getTest());
    assertEquals(IqTestStatus.FINISHED, responseFinish.getTest().getStatus());

    return responseFinish.getTest();
  }

  private ApiResponseTestResult startTest(IqTestType type) throws Exception {
    MvcResult resultApiTest = mvc.perform(
        MockMvcRequestBuilders.get("/test/start?type=" + type + "&locale=" + user.getLocale())
            .header("Authorization", "Bearer 123"))
        .andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseTestResult responseTest = objectMapper
        .readValue(resultApiTest.getResponse().getContentAsString(), ApiResponseTestResult.class);
    log.info("responseTest = {}", responseTest);

    return responseTest;
  }

  private ApiResponseTestResult testAnswerQuestion(UUID code, Integer questionId, Integer answerId)
      throws Exception {
    ApiRequestSubmitAnswer request = new ApiRequestSubmitAnswer();
    request.setAnswer(answerId);
    request.setQuestion(questionId);

    MvcResult resultApiAnswer = mvc
        .perform(MockMvcRequestBuilders.post("/test/" + code).content(asJsonString(request))
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer 123"))
        .andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseTestResult responseTestAnswer = objectMapper
        .readValue(resultApiAnswer.getResponse().getContentAsString(), ApiResponseTestResult.class);
    log.info("responseAnswer = {}", responseTestAnswer);

    return responseTestAnswer;
  }

  private ApiResponseTestResult testFinish(UUID code) throws Exception {
    MvcResult resultApiFinish = mvc.perform(
        MockMvcRequestBuilders.get("/test/finish?testCode=" + code + "&locale=" + user.getLocale())
            .header("Authorization", "Bearer 123"))
        .andReturn();
    ObjectMapper objectMapper = new ObjectMapper();

    ApiResponseTestResult responseFinish = objectMapper
        .readValue(resultApiFinish.getResponse().getContentAsString(), ApiResponseTestResult.class);
    log.info("responseTest = {}", responseFinish);

    return responseFinish;
  }
}
