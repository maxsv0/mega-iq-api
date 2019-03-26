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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Entity
@Table(name = "configuration")
public class Configuration {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 2)
  private Locale locale;

  private String name;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private IqTestType type;

  private String value;

  public Configuration() {
    super();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public IqTestType getType() {
    return type;
  }

  public void setType(IqTestType type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public String toString() {
    return "Configuration [id=" + id + ", locale=" + locale + ", name=" + name + ", type=" + type
        + ", value=" + value + "]";
  }
}
