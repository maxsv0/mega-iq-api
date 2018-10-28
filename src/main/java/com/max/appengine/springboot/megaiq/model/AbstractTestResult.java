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

package com.max.appengine.springboot.megaiq.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@MappedSuperclass
public abstract class AbstractTestResult {
  @Id
  private Integer id;
  private UUID code;
  private String url;
  private Integer userId;
  private IqTestType type;
  private Locale locale;
  private IqTestStatus status;
  private Date createDate;
  private Date updateDate;
  private Date finishDate;
  private Integer points;
  private QuestionGroupsResult groupsGraph;

  @Transient
  private List<AbstractQuestionUser> questionSet;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
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

  public List<AbstractQuestionUser> getQuestionSet() {
    return questionSet;
  }

  public void setQuestionSet(List<AbstractQuestionUser> questionSet) {
    this.questionSet = questionSet;
  }

}
