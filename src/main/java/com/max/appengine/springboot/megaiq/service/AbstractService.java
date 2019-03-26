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

import com.google.common.collect.Table;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

abstract class AbstractService {

  protected String getCachedTitleByTest(Table<String, Locale, String> cache,
      TestResult testResult) {

    return this.getCacheValue(cache, "title_" + testResult.getType().toString(),
        testResult.getLocale());
  }

  protected String getCachedDomain(Table<String, Locale, String> cache, Locale locale) {
    return this.getCacheValue(cache, "domain", locale);
  }

  protected String getCacheValue(Table<String, Locale, String> cache, String name, Locale locale) {
    String value = cache.get(name, locale);

    if (value == null) {
      throw new RuntimeException(
          "Config value is empty. Name: " + name + ", locale=" + locale + ", Cache=" + cache);
    }

    return value;
  }

  protected void cacheTitlesForAllTestType(ConfigurationService configurationService,
      Table<String, Locale, String> cache) {

    for (IqTestType type : IqTestType.values()) {
      for (Locale locale : Locale.values()) {
        String value =
            configurationService.getConfigValueByNameAndTypeAndLocale("title", locale, type);

        cache.put("title_" + type.toString(), locale, value);
      }
    }
  }

  protected void cacheDomainForAllLocale(ConfigurationService configurationService,
      Table<String, Locale, String> cache) {

    cacheValuesForAllLocales(configurationService, cache, "domain");
  }

  protected void cacheValuesForAllLocales(ConfigurationService configurationService,
      Table<String, Locale, String> cache, String name) {
    for (Locale locale : Locale.values()) {
      cache.put(name, locale, configurationService.getConfigGlobal(name, locale));
    }
  }



}
