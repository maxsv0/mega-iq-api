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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Entity
@Table(name = "question",
    indexes = {@Index(columnList = "locale"), @Index(columnList = "createDate")})
public class Question extends AbstractQuestion {

  @Transient
  private List<Answer> answers;

  public List<Answer> getAnswers() {
    return answers;
  }

  public Question() {
    super();
  }

  public Question(AbstractQuestion question) {
    super();

    this.setId(question.getId());
    this.setPic(question.getPic());
    this.setPoints(question.getPoints());
    this.setAnswerCorrect(question.getAnswerCorrect());
    this.setTitle(question.getTitle());
    this.setDescription(question.getDescription());
    this.setGroups(question.getGroups());
    this.setCreateDate(question.getCreateDate());
    this.setUpdateDate(question.getUpdateDate());
    this.setLocale(question.getLocale());
  }

  public Question(Integer id, String pic, Integer points, Integer answerCorrect, String title,
      String description, ArrayList<IqQuestionGroup> groups, Date createDate, Date updateDate,
      Locale locale) {
    super();

    this.setId(id);
    this.setPic(pic);
    this.setPoints(points);
    this.setAnswerCorrect(answerCorrect);
    this.setTitle(title);
    this.setDescription(description);
    this.setGroups(groups);
    this.setCreateDate(createDate);
    this.setUpdateDate(updateDate);
    this.setLocale(locale);
  }
}
