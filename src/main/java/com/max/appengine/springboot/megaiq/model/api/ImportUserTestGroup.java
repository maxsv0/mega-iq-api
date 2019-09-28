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

public class ImportUserTestGroup {
  private Double math;

  private Double logic;

  private Double grammar;

  private Double horizons;

  public ImportUserTestGroup(Double math, Double logic, Double grammar, Double horizons) {
    super();
    this.math = math;
    this.logic = logic;
    this.grammar = grammar;
    this.horizons = horizons;
  }

  public Double getMath() {
    return math;
  }

  public void setMath(Double math) {
    this.math = math;
  }

  public Double getLogic() {
    return logic;
  }

  public void setLogic(Double logic) {
    this.logic = logic;
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

}
