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

package com.max.appengine.springboot.megaiq.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.exception.MegaIQException;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class QuestionsServiceTest extends AbstractUnitTest {

  private static final int GENERATE_QUESTIONS_LIMIT = 100;
  private static final int GENERATE_ANSWERS_LIMIT = 2;

  @Autowired
  private AnswerReporitory answerReporitory;

  @Autowired
  private QuestionReporitory questionReporitory;

  private QuestionsService questionsService;

  @Test(expected = RuntimeException.class)
  public void testQuestionsServiceException() throws MegaIQException {
    this.questionsService = new QuestionsService(answerReporitory, questionReporitory);

    for (Locale locale : Locale.values()) {
      for (IqTestType type : IqTestType.values()) {
        questionsService.initQuestionsByTestType(type, locale);
      }
    }
  }

  @Test
  public void testQuestionsServiceBasis() throws MegaIQException {
    for (Locale locale : Locale.values()) {
      generateQuestionsAndAnswers(questionReporitory, answerReporitory, GENERATE_QUESTIONS_LIMIT,
          GENERATE_ANSWERS_LIMIT, locale);
    }

    this.questionsService = new QuestionsService(answerReporitory, questionReporitory);

    for (Locale locale : Locale.values()) {
      for (IqTestType type : IqTestType.values()) {
        List<Question> questionsSet = questionsService.initQuestionsByTestType(type, locale);
        log.info("questionsSet={}", questionsSet);

        log.info("TEST: locale={}, type={}, questions set size: {}", locale, type,
            questionsSet.size());
        assertEquals("Locale=" + locale + ", type=" + type + ". Questions number is incorrect",
            questionsService.getQuestionsLimitByType(type), questionsSet.size());
      }
    }
  }

  @Test
  public void testQuestionsServiceTwoTestsAreNotEqual() throws MegaIQException {
    generateQuestionsAndAnswers(questionReporitory, answerReporitory, 20, GENERATE_ANSWERS_LIMIT,
        Locale.EN);

    this.questionsService = new QuestionsService(answerReporitory, questionReporitory);

    List<Question> questionsSet1 =
        questionsService.initQuestionsByTestType(IqTestType.PRACTICE_IQ, Locale.EN);
    List<Question> questionsSet2 =
        questionsService.initQuestionsByTestType(IqTestType.PRACTICE_IQ, Locale.EN);
    assertFalse(questionsSet1.equals(questionsSet2));
  }

}
