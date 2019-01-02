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
  
  private Integer math; 
  
  private Integer grammar;
  
  private Integer horizons;
  
  private Integer logic;
  
  public QuestionGroupsResult() {
    super();
  }

  public QuestionGroupsResult(Integer math, Integer grammar, Integer horizons, Integer logic) {
    super();
    this.math = math;
    this.grammar = grammar;
    this.horizons = horizons;
    this.logic = logic;
  }

  public Integer getMath() {
    return math;
  }


  public void setMath(Integer math) {
    this.math = math;
  }


  public Integer getGrammar() {
    return grammar;
  }


  public void setGrammar(Integer grammar) {
    this.grammar = grammar;
  }


  public Integer getHorizons() {
    return horizons;
  }


  public void setHorizons(Integer horizons) {
    this.horizons = horizons;
  }


  public Integer getLogic() {
    return logic;
  }


  public void setLogic(Integer logic) {
    this.logic = logic;
  }
}
