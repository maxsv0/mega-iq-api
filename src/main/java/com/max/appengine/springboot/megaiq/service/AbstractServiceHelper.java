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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.google.common.collect.Table;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public abstract class AbstractServiceHelper {

  public static String getRandomUserAvatar() {
    List<String> list = Arrays.asList(
        "https://lh3.googleusercontent.com/INTuvwHpiXTigV8UQWi5MpSaRt-0mimAQL_eyfGMOynRK_USId0_Z45KFIrKI3tp21J_q6panwRUfrDOBAqHbA",
        "https://lh3.googleusercontent.com/Pjnej65ZS1_DqA-akORx7OHfMtahUiwgtUDOszL2LcbpP3RbROVz5U48N5gcwd0RSBGhdvlaBUtmXQ7VfnM",
        "https://lh3.googleusercontent.com/UYZHF0NvpK-D7LFvXjHfWx3qf_FHEUz0LxCpSNoacI-BwTSUwvk1NFzKhL8L2Qn_uQ_vKJT1TC6m4WlRa5ntNQ",
        "https://lh3.googleusercontent.com/aMC2L9FNILZSHYIQX2BKcr1967r2JBXI__ihJf8P_ux0dyAhtKGTIemHhVdZtKKeX9CXrRxagsLRZ3Yi6Og",
        "https://lh3.googleusercontent.com/q5Yql4cOJt3zhloY6VPwq-jTh1Fev1WkdsIUWomCpgbnwjKXUjlfmVUeQwUbM3txG4dNOL4u6iYk1aSs1Qsg",
        "https://lh3.googleusercontent.com/U1XNjnXDG5l3YOFguuC4gxFeVZvTrs09dzGMDPA7yHh0J-5XtoXQgOcFjFipgieJqJeA88YHvdmKhlItCBc",
        "https://lh3.googleusercontent.com/lBNWn4fHC0NwZgDHXzNHqaEEEY58G233jLGsg0MIyG2fMT6xslJ-uMjx0yKHC2dYlz_uN82eEH7OCgu76dI",
        "https://lh3.googleusercontent.com/8m39DcXMIs7E8OCn8R0lirvIBK4sh1DK3QvapKqbfsrDAw9Q96TnRP1qYuHccYP7PDrAAaCB2bm6kQRjW3Qo",
        "https://lh3.googleusercontent.com/tuw6slWlwIeL3PewrRnDPVTfpuR5OPrDsMTNmDQnb3KQDBFqsuJl8MFfNAkCVXkPcmz0BoM6rvw2XxE10eGX",
        "https://lh3.googleusercontent.com/0afftGjZogSfSZ08FwQ2Ijg-QSFCAkSqTDw_WWEIoE-hKKhjh9tqDfkKNExNBWbuiJuEWDse_C5qrqPCMpM");


    Random rand = new Random();
    return list.get(rand.nextInt(list.size()));
  }
  
  public static String getCachedTitleByTest(Table<String, Locale, String> cache,
      TestResult testResult) {
    return getCacheValue(cache, "test_title_" + testResult.getType().toString().toLowerCase(),
        testResult.getLocale());
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
        cache.put("test_style_name_" + prefix, locale, configurationService
            .getConfigValueByNameAndTypeAndLocale("test_style_name", locale, type));
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
