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
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "question_user",
    indexes = {@Index(columnList = "locale"), @Index(columnList = "testId")})
public class QuestionUser extends AbstractQuestionUser {

  public QuestionUser() {
    super();
  }

  public QuestionUser(Question question) {
    super();

    this.setQuestionIq(question.getId());
    this.setPoints(question.getPoints());
    this.setAnswerCorrect(question.getAnswerCorrect());
    this.setGroups(question.getGroups());
    this.setCreateDate(new Date());
    this.setLocale(question.getLocale());
  }

  @Override
  public String toString() {
    // TODO: display getGroups()
    return "QuestionUser [id=" + getId() + ", testId=" + getTestId() + ", questionIq="
        + getQuestionIq() + ", points=" + getPoints() + ", answerCorrect=" + getAnswerCorrect()
        + ", answerUser=" + getAnswerUser() + ", groups=.., createDate="
        + getCreateDate() + ", updateDate=" + getUpdateDate() + ", locale=" + getLocale() + "]";
  }
}
