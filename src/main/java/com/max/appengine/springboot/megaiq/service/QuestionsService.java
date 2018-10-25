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
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Service
public class QuestionsService {

  private ArrayList<Question> questionsList;
  private ArrayList<Answer> answersList;

  public QuestionsService() {

    // TODO: questions repository
    // load answers
    this.answersList = new ArrayList<Answer>();
    
    
    
    // load question
    this.questionsList = new ArrayList<Question>();
  }

  public Question getQuestionById(Integer questionId, Locale locale) {

    for (Question question : this.questionsList) {
      if (question.getId().equals(questionId) && question.getLocale().equals(locale))
        return question;
    }

    return null;
  }

  public ArrayList<Answer> getAnswersByQuestionId(Integer questionId, Locale locale) {
    ArrayList<Answer> answersList = new ArrayList<Answer>();
    
    for (Answer answer : this.answersList) {
      if (answer.getQuestionId().equals(questionId) && answer.getLocale().equals(locale))
        answersList.add(answer);
    }

    return answersList;
  }
}
