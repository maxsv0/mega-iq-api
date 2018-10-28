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

import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestQuestionsService extends AbstractUnitTest {
  @Autowired
  private AnswerReporitory answerReporitory;

  @Autowired
  private QuestionReporitory questionReporitory;

  @Before
  public void doSetup() {
    int answerId = 1;
    int questionId = 1;

    for (Locale locale : Locale.values()) {
      for (int i = 1; i <= 5; i++) {
        // 3 answers for each question
        // first answer is correct

        questionReporitory
            .save(new Question(questionId, "pic", 1, answerId, "test." + locale + " q" + i, "info",
                new ArrayList<IqQuestionGroup>(), new Date(), new Date(), locale));

        for (int j = 1; j <= 3; j++) {
          answerReporitory.save(new Answer(answerId++, "test." + locale + " q" + i + "a" + j,
              questionId, new Date(), new Date(), locale));
        }

        questionId++;
      }
    }

  }

  @Test
  public void testInitService() {
    log.info("answerReporitory={}", answerReporitory.findAll());
    log.info("questionReporitory={}", questionReporitory.findAll());

    QuestionsService questionsService = new QuestionsService(answerReporitory, questionReporitory);
    for (Locale locale : Locale.values()) {
      ArrayList<Question> questions = questionsService.getQuestions(locale);
      log.info("locale={}, got questions={}", locale, questions);
      assertFalse("Questions for locale=" + locale + " is empty", questions.isEmpty());
      
      for (Question question : questions) {
        ArrayList<Answer> answers = (ArrayList<Answer>) question.getAnswers();
        log.info("QuestionID={}, got answers={}", question.getId(), answers);
        assertFalse("Answers for questionID=" + question.getId() + " is empty", answers.isEmpty());
      }
    }
  }

}
