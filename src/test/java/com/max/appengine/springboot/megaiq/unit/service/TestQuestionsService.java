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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestQuestionsService extends AbstractUnitTest {
  private static final int GENERATE_QUESTIONS_LIMIT = 30;
  private static final int GENERATE_ANSWERS_LIMIT = 3;

  @Autowired
  private AnswerReporitory answerReporitory;

  @Autowired
  private QuestionReporitory questionReporitory;

  private QuestionsService questionsService;

  @Before
  public void doSetup() {
    int answerId = 1;
    int questionId = 1;
    List<IqQuestionGroup> groups = Arrays.asList(IqQuestionGroup.values());
    ArrayList<Question> questions = new ArrayList<Question>();
    ArrayList<Answer> answers = new ArrayList<Answer>();
    
    for (Locale locale : Locale.values()) {
      for (int i = 1; i <= GENERATE_QUESTIONS_LIMIT; i++) {
        // GENERATE_ANSWERS_LIMIT answers for each question
        // first answer is correct

        ArrayList<IqQuestionGroup> iqGroups = new ArrayList<IqQuestionGroup>();
        int iqGroupIndex = questionId % groups.size();
        iqGroups.add(groups.get(iqGroupIndex));
        iqGroupIndex = (questionId + 1) % groups.size();
        iqGroups.add(groups.get(iqGroupIndex));

        int questionPoint = questionId % GENERATE_ANSWERS_LIMIT + 1;

        questions.add(new Question(questionId, "pic", questionPoint, answerId,
            "test." + locale + " q" + i, "info", iqGroups, new Date(), new Date(), locale));

        for (int j = 1; j <= GENERATE_ANSWERS_LIMIT; j++) {
          answers.add(new Answer(answerId++, "test." + locale + " q" + i + "a" + j,
              questionId, new Date(), new Date(), locale));
        }

        questionId++;
      }
    }
    
    log.info("Answers generated: ", answers.size());
    log.info("Question generated: ", questions.size());

    questionReporitory.saveAll(questions);
    answerReporitory.saveAll(answers);

    this.questionsService = new QuestionsService(answerReporitory, questionReporitory);
  }

  @Test
  public void testQuestionsServiceBasis() {
    for (Locale locale : Locale.values()) {
      ArrayList<Question> questions = questionsService.getQuestions(locale);
      log.info("locale={}, got questions={}", locale, questions);
      assertFalse("Questions for locale=" + locale + " is empty", questions.isEmpty());

      for (Question question : questions) {
        ArrayList<Answer> answers = (ArrayList<Answer>) question.getAnswers();
        log.info("QuestionID={}, got answers={}", question.getId(), answers);
        assertFalse("Answers for questionID=" + question.getId() + " is empty", answers.isEmpty());
      }
      
      for (IqTestType type : IqTestType.values()) {
        ArrayList<Question> questionsSet = questionsService.getQuestionsSet(type, locale);
        log.info("questionsSet={}", questionsSet);
        
        log.info("locale={}, type={}, questions set size: {}", locale, type, questionsSet.size());
        assertEquals("Locale=" + locale + ", type=" + type + ". Questions number is incorrect",
            questionsService.getQuestionsLimitByType(type), questionsSet.size());
      }
      
      ArrayList<Question> questionsSet1 = questionsService.getQuestionsSet(IqTestType.PRACTICE_IQ, Locale.EN);
      ArrayList<Question> questionsSet2 = questionsService.getQuestionsSet(IqTestType.PRACTICE_IQ, Locale.EN);
      assertFalse(questionsSet1.equals(questionsSet2));
    }
  }

}
