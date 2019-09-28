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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "result_groups")
public class QuestionGroupsResult {
  @Id
  private Integer id;

  private Double math;

  private Double grammar;

  private Double horizons;

  private Double logic;

  public QuestionGroupsResult() {
    super();
  }

  public QuestionGroupsResult(Integer id, Double math, Double grammar, Double horizons,
      Double logic) {
    super();
    this.id = id;
    this.math = math;
    this.grammar = grammar;
    this.horizons = horizons;
    this.logic = logic;
  }

  public Double getMath() {
    return math;
  }


  public void setMath(Double math) {
    this.math = math;
  }


  public Double getGrammar() {
    return grammar;
  }


  public void setGrammar(Double grammar) {
    this.grammar = grammar;
  }


  public Double getHorizons() {
    return horizons;
  }


  public void setHorizons(Double horizons) {
    this.horizons = horizons;
  }


  public Double getLogic() {
    return logic;
  }


  public void setLogic(Double logic) {
    this.logic = logic;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
    result = prime * result + ((horizons == null) ? 0 : horizons.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((logic == null) ? 0 : logic.hashCode());
    result = prime * result + ((math == null) ? 0 : math.hashCode());
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
    QuestionGroupsResult other = (QuestionGroupsResult) obj;
    if (grammar == null) {
      if (other.grammar != null)
        return false;
    } else if (!grammar.equals(other.grammar))
      return false;
    if (horizons == null) {
      if (other.horizons != null)
        return false;
    } else if (!horizons.equals(other.horizons))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (logic == null) {
      if (other.logic != null)
        return false;
    } else if (!logic.equals(other.logic))
      return false;
    if (math == null) {
      if (other.math != null)
        return false;
    } else if (!math.equals(other.math))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "QuestionGroupsResult [id=" + id + ", math=" + math + ", grammar=" + grammar
        + ", horizons=" + horizons + ", logic=" + logic + "]";
  }
}
