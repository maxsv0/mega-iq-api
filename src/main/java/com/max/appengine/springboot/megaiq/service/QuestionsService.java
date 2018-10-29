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

package com.max.appengine.springboot.megaiq.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;

@Service
public class QuestionsService {

  private List<Question> questionsList;
  private List<Answer> answersList;

  private final AnswerReporitory answerReporitory;
  private final QuestionReporitory questionReporitory;

  @Autowired
  public QuestionsService(AnswerReporitory answerReporitory,
      QuestionReporitory questionReporitory) {
    this.answerReporitory = answerReporitory;
    this.questionReporitory = questionReporitory;

    // load answers
    this.answersList = this.answerReporitory.findAll();

    // load question
    this.questionsList = this.questionReporitory.findAll();
    
    for (Question question : this.questionsList) {
      question.setAnswers(getAnswersByQuestion(question));
    }
  }

  public ArrayList<Question> getQuestions(Locale locale) {
    ArrayList<Question> questionList = new ArrayList<Question>();
    
    for (Question question : this.questionsList) {
      if (question.getLocale().equals(locale))
        questionList.add(question);
    }

    return questionList;
  }

  public Question getQuestionById(Integer questionId, Locale locale) {

    for (Question question : this.questionsList) {
      if (question.getId().equals(questionId) && question.getLocale().equals(locale))
        return question;
    }

    return null;
  }

  private ArrayList<Answer> getAnswersByQuestion(Question question) {
    return getAnswersByQuestionId(question.getId(), question.getLocale());
  }
  
  private ArrayList<Answer> getAnswersByQuestionId(Integer questionId, Locale locale) {
    ArrayList<Answer> answersList = new ArrayList<Answer>();

    for (Answer answer : this.answersList) {
      if (answer.getQuestionId().equals(questionId) && answer.getLocale().equals(locale))
        answersList.add(answer);
    }

    return answersList;
  }
}
