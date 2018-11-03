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
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Entity
@Table(name = "user_test_result",
    indexes = {@Index(columnList = "locale"), @Index(columnList = "finishDate"),
        @Index(columnList = "userId"), @Index(columnList = "code"), @Index(columnList = "type"),
        @Index(columnList = "status"), @Index(columnList = "points")})
public class TestResult extends AbstractTestResult {

  @Transient
  private User user;
  
  @Transient
  private List<QuestionUser> questionSet;
  
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<QuestionUser> getQuestionSet() {
    return questionSet;
  }

  public void setQuestionSet(List<QuestionUser> questionSet) {
    this.questionSet = questionSet;
  }
  
  public TestResult() {
    super();
  }

  public TestResult(Integer id, UUID code, String url, Integer userId, IqTestType type,
      IqTestStatus status, Date createDate, Date updateDate, Date finishDate, Integer points,
      QuestionGroupsResult groupsGraph, Locale locale) {
    super();
    
    this.setId(id);
    this.setCode(code);
    this.setUrl(url);
    this.setUserId(userId);
    this.setType(type);
    this.setStatus(status);
    this.setCreateDate(createDate);
    this.setUpdateDate(updateDate);
    this.setFinishDate(finishDate);
    this.setPoints(points);
    this.setGroupsGraph(groupsGraph);
    this.setLocale(locale);
  }

}
