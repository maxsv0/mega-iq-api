/*
 * Copyright 2019 mega-iq.com
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

import java.util.Date;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;

public class ImportUserTest {
  private IqTestType type;

  private Date createDate;

  private Date finishDate;

  private Integer points;

  private ImportUserTestGroup groups;

  public ImportUserTest() {
    super();
  }

  public ImportUserTest(IqTestType type, Date createDate, Date finishDate, Integer points,
      ImportUserTestGroup groups) {
    super();
    this.type = type;
    this.createDate = createDate;
    this.finishDate = finishDate;
    this.points = points;
    this.groups = groups;
  }

  public IqTestType getType() {
    return type;
  }

  public void setType(IqTestType type) {
    this.type = type;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
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

  public ImportUserTestGroup getGroups() {
    return groups;
  }

  public void setGroups(ImportUserTestGroup groups) {
    this.groups = groups;
  }

}
