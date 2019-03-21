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
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.ConfigurationReporitory;

@Service
public class ConfigurationService {
  public static final String EMAIL_FROM = "mail@mega-iq.com";

  public static final String EMAIL_FROM_NAME = "Mega-IQ";

  public static final Locale DEFAULT_LOCALE = Locale.EN;

  private final ConfigurationReporitory configurationReporitory;

  private List<Configuration> config;
  
  // TODO: find a better way and return final to config
  public void reloadConfiguration() {
    this.config = this.configurationReporitory.findAll();
  }
  
  public List<Configuration> getConfig() {
    return config;
  }

  @Autowired
  public ConfigurationService(ConfigurationReporitory configurationReporitory) {
    this.configurationReporitory = configurationReporitory;

    this.config = this.configurationReporitory.findAll();
  }

  public String getDomainByLocale(Locale locale) {
    return getConfigValueByNameAndLocale("domain", locale);
  }

  public String getTestResultTitle(TestResult testResult) {
    return getConfigValueByNameAndLocale("title_" + testResult.getType().toString().toLowerCase(),
        testResult.getLocale());
  }

  public String getConfigValueByName(String name) {
    return getConfigValueByNameAndLocale(name, ConfigurationService.DEFAULT_LOCALE);
  }

  public String getConfigValueByNameAndLocale(String name, Locale locale) {
    for (Configuration configuration : this.getConfig()) {
      if (configuration.getLocale().equals(locale) && configuration.getName().equals(name))
        return configuration.getValue();
    }

    throw new RuntimeException("Config value not found for name=" + name + ", Locale=" + locale);
  }
}
