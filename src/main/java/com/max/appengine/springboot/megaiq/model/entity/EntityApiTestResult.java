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

package com.max.appengine.springboot.megaiq.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import com.max.appengine.springboot.megaiq.model.QuestionGroupsResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.QuestionsService;

public class EntityApiTestResult {
  private UUID code;
  private String url;
  private EntityApiUser user;
  private IqTestType type;
  private Locale locale;
  private IqTestStatus status;
  private Date createDate;
  private Date updateDate;
  private Date finishDate;
  private Integer points;
  private QuestionGroupsResult groupsGraph;
  private ArrayList<EntityApiQuestion> questionSet;

  @Autowired
  private QuestionsService serviceQuestions;

  public EntityApiTestResult(EntityTestResult testResult, boolean showPrivate) {
    super();

    this.setCode(testResult.getCode());
    this.setUrl(testResult.getUrl());
    this.setType(testResult.getType());
    this.setLocale(testResult.getLocale());
    this.setStatus(testResult.getStatus());
    this.setFinishDate(testResult.getFinishDate());
    this.setPoints(testResult.getPoints());
    this.setGroupsGraph(testResult.getGroupsGraph());

    if (showPrivate) {
      this.setCreateDate(testResult.getCreateDate());
      this.setUpdateDate(testResult.getUpdateDate());
      this.setQuestionSet(new ArrayList<EntityApiQuestion>());

      for (EntityQuestionUser questionUser : testResult.getQuestionSet()) {
        EntityApiQuestion apiQuestion = new EntityApiQuestion(questionUser,
            serviceQuestions.getQuestionById(questionUser.getId(), questionUser.getLocale()));
        this.getQuestionSet().add(apiQuestion);
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

  public EntityApiUser getUser() {
    return user;
  }

  public void setUser(EntityApiUser user) {
    this.user = user;
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

  public QuestionGroupsResult getGroupsGraph() {
    return groupsGraph;
  }

  public void setGroupsGraph(QuestionGroupsResult groupsGraph) {
    this.groupsGraph = groupsGraph;
  }

  public ArrayList<EntityApiQuestion> getQuestionSet() {
    return questionSet;
  }

  public void setQuestionSet(ArrayList<EntityApiQuestion> questionSet) {
    this.questionSet = questionSet;
  }


}
