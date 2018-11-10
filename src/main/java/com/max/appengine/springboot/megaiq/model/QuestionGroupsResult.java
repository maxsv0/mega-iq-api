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

import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QuestionGroupsResult implements Serializable {
  private Map<IqQuestionGroup, Integer> questionsNumber = new HashMap();

  public QuestionGroupsResult(Integer math, Integer grammar, Integer hor, Integer logic) {
    this.questionsNumber.put(IqQuestionGroup.MATH, math);
    this.questionsNumber.put(IqQuestionGroup.GRAMMAR, grammar);
    this.questionsNumber.put(IqQuestionGroup.HORIZONS, hor);
    this.questionsNumber.put(IqQuestionGroup.LOGIC, logic);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuestionGroupsResult)) {
      return false;
    }
    QuestionGroupsResult that = (QuestionGroupsResult) o;
    return Objects.equals(questionsNumber, that.questionsNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questionsNumber);
  }
}
