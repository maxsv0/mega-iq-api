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

package com.max.appengine.springboot.megaiq.model.api;

import java.time.Duration;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.max.appengine.springboot.megaiq.model.AbstractQuestionUser;
import com.max.appengine.springboot.megaiq.model.QuestionGroupsResult;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.QuestionsService;

@JsonInclude(Include.NON_NULL)
public class ApiTestResult {
  private UUID code;
  private String url;
  private IqTestType type;
  private Locale locale;
  private IqTestStatus status;
  private Date createDate;
  private Date updateDate;
  private Date finishDate;
  private Integer points;
  private Integer progress;
  private QuestionGroupsResult groupsGraph;
  private ArrayList<ApiQuestion> questionSet;
  private TestResultInfo info;
  private ArrayList<AnswerInfo> answerInfo;

  public ApiTestResult() {
    super();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ApiTestResult.class.getSimpleName() + "[", "]")
            .add("code=" + code)
            .add("url='" + url + "'")
            .add("type=" + type)
            .add("locale=" + locale)
            .add("status=" + status)
            .add("createDate=" + createDate)
            .add("updateDate=" + updateDate)
            .add("finishDate=" + finishDate)
            .add("points=" + points)
            .add("progress=" + progress)
            .add("groupsGraph=" + groupsGraph)
            .add("questionSet=" + questionSet)
            .add("info=" + info)
            .add("answerInfo=" + answerInfo)
            .toString();
  }

  public ApiTestResult(QuestionsService serviceQuestions, TestResult testResult,
                       boolean showPrivate) {
    super();

    this.setCode(testResult.getCode());
    this.setUrl(testResult.getUrl());
    this.setType(testResult.getType());
    this.setLocale(testResult.getLocale());
    this.setStatus(testResult.getStatus());
    this.setFinishDate(testResult.getFinishDate());
    this.setPoints(testResult.getPoints());
    this.setGroupsGraph(testResult.getGroupsGraph());
    this.setProgress(0);

    if (showPrivate) {
      this.setCreateDate(testResult.getCreateDate());
      this.setUpdateDate(testResult.getUpdateDate());
      this.setQuestionSet(new ArrayList<ApiQuestion>());

      if (testResult.getQuestionSet() != null) {
        for (AbstractQuestionUser questionUser : testResult.getQuestionSet()) {
          ApiQuestion apiQuestion = new ApiQuestion(questionUser, serviceQuestions
              .getQuestionById(questionUser.getQuestionIq(), questionUser.getLocale()));

          if (testResult.getStatus().equals(IqTestStatus.ACTIVE)) {
            apiQuestion.setAnswerCorrect(null);
            apiQuestion.setDescription(null);
          }
          
          if (questionUser.getAnswerUser() != null) {
            this.setProgress(this.getProgress() + 1);
          }

          this.getQuestionSet().add(apiQuestion);
        }

        this.setProgress(
            (int) Math.floor(100 * this.getProgress() / testResult.getQuestionSet().size()));
      }
    }

    TestResultInfo info = new TestResultInfo();
    ArrayList<AnswerInfo> answerInfoList = new ArrayList<>();

    if (testResult.getQuestionSet() != null) {
      info.setQuestions(testResult.getQuestionSet().size());

      int questionsCorrect = 0;
      for (AbstractQuestionUser questionUser : testResult.getQuestionSet()) {
        AnswerInfo answerInfo = new AnswerInfo();

        answerInfo.setCorrect(false);
        if (questionUser.getAnswerCorrect().equals(questionUser.getAnswerUser())) {
          questionsCorrect++;

          answerInfo.setCorrect(true);
        }

        answerInfo.setPoints(questionUser.getPoints());
        answerInfoList.add(answerInfo);
      }

      info.setAnswersCorrect(questionsCorrect);
    }

    if (testResult.getFinishDate() != null && testResult.getCreateDate() != null) {
      long diff = testResult.getFinishDate().getTime() - testResult.getCreateDate().getTime();
      long diffSeconds = diff / 1000 % 60;
      long diffMinutes = diff / (60 * 1000) % 60;

      info.setDuration(diffMinutes + ":" + diffSeconds);
    }

    this.setInfo(info);
    this.setAnswerInfo(answerInfoList);
  }

  public UUID getCode() {
    return code;
  }

  public void setCode(UUID code) {
    this.code = code;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public IqTestType getType() {
    return type;
  }

  public void setType(IqTestType type) {
    this.type = type;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public IqTestStatus getStatus() {
    return status;
  }

  public void setStatus(IqTestStatus status) {
    this.status = status;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public Date getFinishDate() {
    return finishDate;
  }

  public void setFinishDate(Date finishDate) {
    this.finishDate = finishDate;
  }

  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  public Integer getProgress() {
    return progress;
  }

  public void setProgress(Integer progress) {
    this.progress = progress;
  }

  public QuestionGroupsResult getGroupsGraph() {
    return groupsGraph;
  }

  public void setGroupsGraph(QuestionGroupsResult groupsGraph) {
    this.groupsGraph = groupsGraph;
  }

  public ArrayList<ApiQuestion> getQuestionSet() {
    return questionSet;
  }

  public void setQuestionSet(ArrayList<ApiQuestion> questionSet) {
    this.questionSet = questionSet;
  }

  public TestResultInfo getInfo() {
    return info;
  }

  public void setInfo(TestResultInfo info) {
    this.info = info;
  }

  public ArrayList<AnswerInfo> getAnswerInfo() {
    return answerInfo;
  }

  public void setAnswerInfo(ArrayList<AnswerInfo> answerInfo) {
    this.answerInfo = answerInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ApiTestResult that = (ApiTestResult) o;
    return Objects.equals(code, that.code) &&
            Objects.equals(url, that.url) &&
            type == that.type &&
            locale == that.locale &&
            status == that.status &&
            Objects.equals(createDate, that.createDate) &&
            Objects.equals(updateDate, that.updateDate) &&
            Objects.equals(finishDate, that.finishDate) &&
            Objects.equals(points, that.points) &&
            Objects.equals(progress, that.progress) &&
            Objects.equals(groupsGraph, that.groupsGraph) &&
            Objects.equals(questionSet, that.questionSet) &&
            Objects.equals(info, that.info) &&
            Objects.equals(answerInfo, that.answerInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, url, type, locale, status, createDate, updateDate, finishDate, points, progress, groupsGraph, questionSet, info, answerInfo);
  }
}
