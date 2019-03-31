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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.exception.MegaIQException;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;

@Service
public class QuestionsService {
  private final Map<IqTestType, Integer> questionsNumber;

  private List<Question> questionsList;

  private List<Answer> answersList;

  private final AnswerReporitory answerReporitory;

  private final QuestionReporitory questionReporitory;

  @Autowired
  public QuestionsService(AnswerReporitory answerReporitory, QuestionReporitory questionReporitory,
      ConfigurationService configurationService) {
    this.answerReporitory = answerReporitory;
    this.questionReporitory = questionReporitory;

    // load answers
    this.answersList = this.answerReporitory.findAll();

    // load question
    this.questionsList = this.questionReporitory.findAll();

    for (Question question : this.questionsList) {
      question.setAnswers(getAnswersByQuestion(question));
    }

    this.questionsNumber = new HashMap<IqTestType, Integer>();
    for (IqTestType type : IqTestType.values()) {
      this.questionsNumber.put(type, configurationService.getTestQuestionsLimit(type));
    }
  }

  public int getQuestionsLimitByType(IqTestType testType) {
    return this.questionsNumber.get(testType);
  }

  public List<Question> initQuestionsByTestType(IqTestType testType, Locale locale)
      throws MegaIQException {
    Integer groupMax = (int) Math.floor(this.questionsNumber.get(testType) / 4);

    Integer maxMath = groupMax, maxGrammar = groupMax, maxHorizons = groupMax, maxLogic = groupMax;

    switch (testType) {
      case PRACTICE_IQ:
        break;
      case STANDARD_IQ:
        break;
      case MEGA_IQ:
        break;
      case MATH:
        maxMath = this.questionsNumber.get(testType);
        maxGrammar = 0;
        maxHorizons = 0;
        maxLogic = 0;
        break;
      case GRAMMAR:
        maxMath = 0;
        maxGrammar = this.questionsNumber.get(testType);
        maxHorizons = 0;
        maxLogic = 0;
        break;

    }

    List<Question> questionSetList = getQuestionsByGroups(locale,
        this.questionsNumber.get(testType), maxMath, maxGrammar, maxHorizons, maxLogic);

    if (questionSetList.size() != this.questionsNumber.get(testType)) {
      throw new MegaIQException(Level.SEVERE,
          "Questions DB is broken. Set size=" + questionSetList.size() + ". Need="
              + this.questionsNumber.get(testType) + " for type=" + testType + ", locale="
              + locale);
    }

    return questionSetList;
  }

  public Question getQuestionById(Integer questionId, Locale locale) {
    for (Question question : this.questionsList) {
      if (question.getId().equals(questionId) && question.getLocale().equals(locale))
        return question;
    }

    return null;
  }

  private List<Question> getQuestionsByGroups(Locale locale, Integer total, Integer math,
      Integer grammar, Integer horizons, Integer logic) throws MegaIQException {
    List<Question> questionAllList = getAllQuestionsByLocale(locale);
    Collections.shuffle(questionAllList);

    HashMap<IqQuestionGroup, Integer> groupsMax = new HashMap<IqQuestionGroup, Integer>();
    groupsMax.put(IqQuestionGroup.MATH, math);
    groupsMax.put(IqQuestionGroup.GRAMMAR, grammar);
    groupsMax.put(IqQuestionGroup.HORIZONS, horizons);
    groupsMax.put(IqQuestionGroup.LOGIC, logic);

    HashMap<IqQuestionGroup, Integer> groups = new HashMap<IqQuestionGroup, Integer>();
    groups.put(IqQuestionGroup.MATH, 0);
    groups.put(IqQuestionGroup.GRAMMAR, 0);
    groups.put(IqQuestionGroup.HORIZONS, 0);
    groups.put(IqQuestionGroup.LOGIC, 0);

    List<Question> questionSetList = new ArrayList<Question>();
    int questionNumber = 1;

    for (Question question : questionAllList) {
      boolean addQuestion = false;

      List<IqQuestionGroup> questionGroups = new ArrayList<IqQuestionGroup>(question.getGroups());
      if (questionGroups.isEmpty()) {
        throw new MegaIQException(Level.SEVERE,
            "Questions groups missing for question ID=" + question.getId());
      }
      Collections.shuffle(questionGroups);

      IqQuestionGroup type = questionGroups.get(0);
      if (groups.get(type) < groupsMax.get(type)) {
        groups.put(type, groups.get(type) + 1);
        addQuestion = true;
      }

      // for (IqQuestionGroup type : question.getGroups()) {
      // if (addQuestion || groups.get(type) < groupsMax.get(type)) {
      // groups.put(type, groups.get(type) + 1);
      // addQuestion = true;
      // }
      // }

      if (addQuestion) {
        Question questionAdd = new Question(question);
        questionAdd.setAnswers(question.getAnswers());
        Collections.shuffle(questionAdd.getAnswers());
        
        questionSetList.add(questionAdd);
        questionNumber++;
      }

      if (questionNumber > total)
        break;
    }

    return questionSetList;
  }

  private List<Question> getAllQuestionsByLocale(Locale locale) throws MegaIQException {
    List<Question> questionList = new ArrayList<Question>();

    if (this.questionsList.isEmpty()) {
      throw new MegaIQException(Level.SEVERE, "Questions DB is empty");
    }

    for (Question question : this.questionsList) {
      if (question.getLocale().equals(locale))
        questionList.add(question);
    }

    return questionList;
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

    if (answersList.isEmpty()) {
      throw new IllegalStateException(
          "No answers for question ID=" + questionId + ", locale=" + locale);
    }

    return answersList;
  }
}
