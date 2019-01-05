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
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@MappedSuperclass
public abstract class AbstractQuestionUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer testId;
  private Integer questionIq;
  private Integer points;
  private Integer answerCorrect;
  private Integer answerUser;

  @ElementCollection(targetClass = IqQuestionGroup.class)
  @CollectionTable(name = "question_user_groups",
      joinColumns = @JoinColumn(name = "question_user_id"),
      indexes = {@Index(columnList = "question_user_id"), @Index(columnList = "groups")})
  private List<IqQuestionGroup> groups;
  private Date createDate;
  private Date updateDate;

  @Enumerated(EnumType.STRING)
  @Column(length = 2)
  private Locale locale;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getTestId() {
    return testId;
  }

  public void setTestId(Integer testId) {
    this.testId = testId;
  }

  public Integer getQuestionIq() {
    return questionIq;
  }

  public void setQuestionIq(Integer questionIq) {
    this.questionIq = questionIq;
  }

  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  public Integer getAnswerCorrect() {
    return answerCorrect;
  }

  public void setAnswerCorrect(Integer answerCorrect) {
    this.answerCorrect = answerCorrect;
  }

  public Integer getAnswerUser() {
    return answerUser;
  }

  public void setAnswerUser(Integer answerUser) {
    this.answerUser = answerUser;
  }

  public List<IqQuestionGroup> getGroups() {
    return groups;
  }

  public void setGroups(List<IqQuestionGroup> groups) {
    this.groups = groups;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((answerCorrect == null) ? 0 : answerCorrect.hashCode());
    result = prime * result + ((answerUser == null) ? 0 : answerUser.hashCode());
    // result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
    // result = prime * result + ((groups == null) ? 0 : groups.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((locale == null) ? 0 : locale.hashCode());
    result = prime * result + ((points == null) ? 0 : points.hashCode());
    result = prime * result + ((questionIq == null) ? 0 : questionIq.hashCode());
    result = prime * result + ((testId == null) ? 0 : testId.hashCode());
    result = prime * result + ((updateDate == null) ? 0 : updateDate.hashCode());
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
    AbstractQuestionUser other = (AbstractQuestionUser) obj;
    if (answerCorrect == null) {
      if (other.answerCorrect != null)
        return false;
    } else if (!answerCorrect.equals(other.answerCorrect))
      return false;
    if (answerUser == null) {
      if (other.answerUser != null)
        return false;
    } else if (!answerUser.equals(other.answerUser))
      return false;
    // if (createDate == null) {
    // if (other.createDate != null)
    // return false;
    // } else if (!createDate.equals(other.createDate))
    // return false;
    // if (groups == null) {
    // if (other.groups != null)
    // return false;
    // } else if (!groups.equals(other.groups))
    // return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (locale != other.locale)
      return false;
    if (points == null) {
      if (other.points != null)
        return false;
    } else if (!points.equals(other.points))
      return false;
    if (questionIq == null) {
      if (other.questionIq != null)
        return false;
    } else if (!questionIq.equals(other.questionIq))
      return false;
    if (testId == null) {
      if (other.testId != null)
        return false;
    } else if (!testId.equals(other.testId))
      return false;
    if (updateDate == null) {
      if (other.updateDate != null)
        return false;
    } else if (!updateDate.equals(other.updateDate))
      return false;
    return true;
  }
}
