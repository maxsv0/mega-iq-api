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
import java.util.List;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.AbstractQuestionUser;
import com.max.appengine.springboot.megaiq.model.Question;

public class ApiQuestion {
  private String pic;
  private String pic2x;
  private Integer answerCorrect;
  private Integer answerUser;
  private String title;
  private String description;
  private Date updateDate;
  private List<ApiAnswer> answers;

  public ApiQuestion() {
    super();
  }

  @Override
  public String toString() {
    return "ApiQuestion [pic=" + pic + ", pic2x=" + pic2x + ", answerCorrect=" + answerCorrect
        + ", answerUser=" + answerUser + ", title=" + title + ", description=" + description
        + ", updateDate=" + updateDate + ", answers=" + answers + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((answerCorrect == null) ? 0 : answerCorrect.hashCode());
    result = prime * result + ((answerUser == null) ? 0 : answerUser.hashCode());
    result = prime * result + ((answers == null) ? 0 : answers.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((pic == null) ? 0 : pic.hashCode());
    result = prime * result + ((pic2x == null) ? 0 : pic2x.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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
    ApiQuestion other = (ApiQuestion) obj;
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
    if (answers == null) {
      if (other.answers != null)
        return false;
    } else if (!answers.equals(other.answers))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (pic == null) {
      if (other.pic != null)
        return false;
    } else if (!pic.equals(other.pic))
      return false;
    if (pic2x == null) {
      if (other.pic2x != null)
        return false;
    } else if (!pic2x.equals(other.pic2x))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    if (updateDate == null) {
      if (other.updateDate != null)
        return false;
    } else if (!updateDate.equals(other.updateDate))
      return false;
    return true;
  }

  public ApiQuestion(AbstractQuestionUser questionUser, Question questionData) {
    super();

    this.setPic(questionData.getPic());
    this.setPic2x(questionData.getPic2x());
    this.setAnswerCorrect(questionUser.getAnswerCorrect());
    this.setAnswerUser(questionUser.getAnswerUser());
    this.setTitle(questionData.getTitle());
    this.setDescription(questionData.getDescription());

    this.answers = new ArrayList<ApiAnswer>();
    for (Answer answer : questionData.getAnswers()) {
      this.answers.add(new ApiAnswer(answer));
    }
  }

  public String getPic() {
    return pic;
  }

  public void setPic(String pic) {
    this.pic = pic;
  }

  public String getPic2x() {
    return pic2x;
  }

  public void setPic2x(String pic2x) {
    this.pic2x = pic2x;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public List<ApiAnswer> getAnswers() {
    return answers;
  }

  public void setAnswers(List<ApiAnswer> answers) {
    this.answers = answers;
  }
}
