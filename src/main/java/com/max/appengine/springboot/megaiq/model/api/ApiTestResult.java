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

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
    result = prime * result + ((finishDate == null) ? 0 : finishDate.hashCode());
    result = prime * result + ((groupsGraph == null) ? 0 : groupsGraph.hashCode());
    result = prime * result + ((locale == null) ? 0 : locale.hashCode());
    result = prime * result + ((points == null) ? 0 : points.hashCode());
    result = prime * result + ((questionSet == null) ? 0 : questionSet.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((updateDate == null) ? 0 : updateDate.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApiTestResult other = (ApiTestResult) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    if (createDate == null) {
      if (other.createDate != null)
        return false;
    } else if (!createDate.equals(other.createDate))
      return false;
    if (finishDate == null) {
      if (other.finishDate != null)
        return false;
    } else if (!finishDate.equals(other.finishDate))
      return false;
    if (groupsGraph == null) {
      if (other.groupsGraph != null)
        return false;
    } else if (!groupsGraph.equals(other.groupsGraph))
      return false;
    if (locale != other.locale)
      return false;
    if (points == null) {
      if (other.points != null)
        return false;
    } else if (!points.equals(other.points))
      return false;
    if (questionSet == null) {
      if (other.questionSet != null)
        return false;
    } else if (!questionSet.equals(other.questionSet))
      return false;
    if (status != other.status)
      return false;
    if (type != other.type)
      return false;
    if (updateDate == null) {
      if (other.updateDate != null)
        return false;
    } else if (!updateDate.equals(other.updateDate))
      return false;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    return true;
  }

  public ApiTestResult() {
    super();
  }

  @Override
  public String toString() {
    return "ApiTestResult [code=" + code + ", url=" + url + ", type=" + type
        + ", locale=" + locale + ", status=" + status + ", createDate=" + createDate
        + ", updateDate=" + updateDate + ", finishDate=" + finishDate + ", points=" + points
        + ", groupsGraph=" + groupsGraph + ", questionSet=" + questionSet + "]";
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


}
