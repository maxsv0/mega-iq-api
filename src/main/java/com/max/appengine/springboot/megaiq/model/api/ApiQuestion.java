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
  private Integer answerCorrect;
  private Integer answerUser;
  private String title;
  private String description;
  private Date updateDate;
  private List<ApiAnswer> answers;

  public ApiQuestion(AbstractQuestionUser questionUser, Question questionData) {
    super();

    this.setPic(questionData.getPic());
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
