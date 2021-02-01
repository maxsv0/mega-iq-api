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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;

import java.util.StringJoiner;

@JsonInclude(Include.NON_NULL)
public class ApiTestInfo {
  private IqTestType type;
  private String name;
  private String url;
  private String pic;
  private String description;
  private String title;
  private Integer questions;
  private Integer time;
  private Integer expire;
  private String styleName;

  public IqTestType getType() {
    return type;
  }

  public void setType(IqTestType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPic() {
    return pic;
  }

  public void setPic(String pic) {
    this.pic = pic;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getQuestions() {
    return questions;
  }

  public void setQuestions(Integer questions) {
    this.questions = questions;
  }

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public Integer getExpire() {
    return expire;
  }

  public void setExpire(Integer expire) {
    this.expire = expire;
  }

  public String getStyleName() {
    return styleName;
  }

  public void setStyleName(String styleName) {
    this.styleName = styleName;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ApiTestInfo.class.getSimpleName() + "[", "]")
            .add("type=" + type)
            .add("name='" + name + "'")
            .add("url='" + url + "'")
            .add("pic='" + pic + "'")
            .add("description='" + description + "'")
            .add("title='" + title + "'")
            .add("questions=" + questions)
            .add("time=" + time)
            .add("expire=" + expire)
            .add("styleName='" + styleName + "'")
            .toString();
  }
}
