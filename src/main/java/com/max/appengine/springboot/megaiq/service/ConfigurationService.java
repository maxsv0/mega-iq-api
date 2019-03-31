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

package com.max.appengine.springboot.megaiq.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Configuration;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.ConfigurationReporitory;

@Service
public class ConfigurationService {
  public static final String EMAIL_FROM = "mail@mega-iq.com";

  public static final String EMAIL_FROM_NAME = "Mega-IQ";

  public static final Locale DEFAULT_LOCALE = Locale.EN;

  private final ConfigurationReporitory configurationReporitory;

  private final List<Configuration> config;

  public List<Configuration> getConfig() {
    return config;
  }

  @Autowired
  public ConfigurationService(ConfigurationReporitory configurationReporitory) {
    this.configurationReporitory = configurationReporitory;

    this.config = this.configurationReporitory.findAll();
  }

  public Integer getTestExpire(IqTestType type) {
    return Integer.valueOf(getConfigValueByNameAndType("test_expire", type));
  }

  public Integer getTestQuestionsLimit(IqTestType type) {
    return Integer.valueOf(getConfigValueByNameAndType("test_questions", type));
  }

  // TODO: remove unused code
  // public String getDomainByLocale(Locale locale) {
  // return getConfigGlobal("domain", locale);
  // }
  //
  // public String getTestResultTitle(TestResult testResult) {
  // return getConfigValueByNameAndTypeAndLocale("test_title", testResult.getLocale(),
  // testResult.getType());
  // }

  public String getConfigValueByNameAndType(String name, IqTestType type) {
    for (Configuration configuration : this.getConfig()) {
      if (configuration.getName().equals(name) && configuration.getType().equals(type))
        return configuration.getValue();
    }

    throw new RuntimeException("Config value not found for name=" + name + ", Type=" + type
        + ". Config = " + this.getConfig());
  }

  public String getConfigGlobal(String name, Locale locale) {
    for (Configuration configuration : this.getConfig()) {
      if (configuration.getName().equals(name) && configuration.getLocale().equals(locale))
        return configuration.getValue();
    }

    throw new RuntimeException("Config value not found for name=" + name + ", Locale=" + locale
        + ". Config = " + this.getConfig());
  }

  public String getConfigValueByNameAndTypeAndLocale(String name, Locale locale, IqTestType type) {
    for (Configuration configuration : this.getConfig()) {
      if (configuration.getName().equals(name)
          && (configuration.getLocale() == null || configuration.getLocale().equals(locale))
          && (configuration.getType() == null || configuration.getType().equals(type)))
        return configuration.getValue();
    }

    throw new RuntimeException("Config value not found for name=" + name + ", Locale=" + locale
        + ", Type=" + type + ". Config = " + this.getConfig());
  }
}
