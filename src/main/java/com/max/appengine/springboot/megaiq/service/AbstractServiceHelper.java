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

public abstract class AbstractServiceHelper {

  public static String getCachedTitleByTest(Table<String, Locale, String> cache,
      TestResult testResult) {
    return getCacheValue(cache,
        "test_title_" + testResult.getType().toString().toLowerCase(), testResult.getLocale());
  }
  
  public static String getCachedTitlePromoByTest(Table<String, Locale, String> cache,
      TestResult testResult) {
    return getCacheValue(cache,
        "test_title_promo_" + testResult.getType().toString().toLowerCase(), testResult.getLocale());
  }

  public static String getCachedDomain(Table<String, Locale, String> cache, Locale locale) {
    return getCacheValue(cache, "domain", locale);
  }

  public static String getCacheValue(Table<String, Locale, String> cache, String name,
      Locale locale) {
    String value = cache.get(name, locale);

    if (value == null) {
      throw new RuntimeException(
          "Config value is empty. Name: " + name + ", locale=" + locale + ", Cache=" + cache);
    }

    return value;
  }

  public static void cacheInfoForAllTestType(ConfigurationService configurationService,
      Table<String, Locale, String> cache) {

    for (IqTestType type : IqTestType.values()) {

      String prefix = type.toString().toLowerCase();
      for (Locale locale : Locale.values()) {
        cache.put("test_title_" + prefix, locale,
            configurationService.getConfigValueByNameAndTypeAndLocale("test_title", locale, type));
        cache.put("test_url_" + prefix, locale,
            configurationService.getConfigValueByNameAndTypeAndLocale("test_url", locale, type));
        cache.put("test_title_promo_" + prefix, locale, configurationService
            .getConfigValueByNameAndTypeAndLocale("test_title_promo", locale, type));
        cache.put("test_pic_" + prefix, locale,
            configurationService.getConfigValueByNameAndTypeAndLocale("test_pic", locale, type));
        cache.put("test_questions_" + prefix, locale, configurationService
            .getConfigValueByNameAndTypeAndLocale("test_questions", locale, type));
        cache.put("test_time_" + prefix, locale,
            configurationService.getConfigValueByNameAndTypeAndLocale("test_time", locale, type));
      }
    }
  }

  protected static void cacheDomainForAllLocale(ConfigurationService configurationService,
      Table<String, Locale, String> cache) {

    cacheValuesForAllLocales(configurationService, cache, "domain");
  }

  protected static void cacheValuesForAllLocales(ConfigurationService configurationService,
      Table<String, Locale, String> cache, String name) {
    for (Locale locale : Locale.values()) {
      cache.put(name, locale, configurationService.getConfigGlobal(name, locale));
    }
  }

}
