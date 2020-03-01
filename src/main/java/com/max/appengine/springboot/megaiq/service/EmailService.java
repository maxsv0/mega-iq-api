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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Service
public class EmailService extends AbstractSendgridEmailService {

  public static final String EMAIL_SUBJECT_NEW_USER = "email_subject_new_user";

  public static final String EMAIL_SUBJECT_IMPORT_USER = "email_subject_import_user";

  public static final String EMAIL_SUBJECT_EMAIL_VERIFY = "email_subject_email_verify";

  public static final String EMAIL_SUBJECT_FORGET = "email_subject_forget";

  public static final String EMAIL_SUBJECT_DIRECT_LOGIN = "email_subject_direct_login";

  public static final String EMAIL_SUBJECT_TEST_RESULT = "email_subject_test_result";

  private final Table<String, Locale, String> configCache = HashBasedTable.create();

  @Autowired
  public EmailService(ConfigurationService configurationService) {
    AbstractServiceHelper.cacheValuesForAllLocales(configurationService, configCache,
        EMAIL_SUBJECT_NEW_USER);

    AbstractServiceHelper.cacheValuesForAllLocales(configurationService, configCache,
        EMAIL_SUBJECT_IMPORT_USER);

    AbstractServiceHelper.cacheValuesForAllLocales(configurationService, configCache,
        EMAIL_SUBJECT_EMAIL_VERIFY);
    AbstractServiceHelper.cacheValuesForAllLocales(configurationService, configCache,
        EMAIL_SUBJECT_FORGET);
    AbstractServiceHelper.cacheValuesForAllLocales(configurationService, configCache,
        EMAIL_SUBJECT_DIRECT_LOGIN);
    AbstractServiceHelper.cacheValuesForAllLocales(configurationService, configCache,
        EMAIL_SUBJECT_TEST_RESULT);
    AbstractServiceHelper.cacheInfoForAllTestType(configurationService, configCache);
    AbstractServiceHelper.cacheDomainForAllLocale(configurationService, configCache);
  }

  public boolean sendEmailRegistration(User user) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("domain_url",
        AbstractServiceHelper.getCachedDomain(configCache, user.getLocale()));
    preparePromoFields(userData, user);

    String content = loadTemplateFromPath("new-user-registration", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("domain_url");
    preparePromoFieldsRequired(fieldsRequired);

    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        AbstractServiceHelper.getCacheValue(configCache, EMAIL_SUBJECT_NEW_USER, user.getLocale()),
        content);
  }

  public boolean sendEmailRegistrationWithVerify(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("domain_url",
        AbstractServiceHelper.getCachedDomain(configCache, user.getLocale()));
    userData.put("verify_link", link);
    preparePromoFields(userData, user);

    String content = loadTemplateFromPath("new-user-registration-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("verify_link");
    fieldsRequired.add("domain_url");
    preparePromoFieldsRequired(fieldsRequired);

    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        AbstractServiceHelper.getCacheValue(configCache, EMAIL_SUBJECT_NEW_USER, user.getLocale()),
        content);
  }

  public boolean sendEmailVerify(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("verify_link", link);

    String content = loadTemplateFromPath("user-email-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("verify_link");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData, AbstractServiceHelper
        .getCacheValue(configCache, EMAIL_SUBJECT_EMAIL_VERIFY, user.getLocale()), content);
  }

  public boolean sendEmailForget(User user, String link, Locale locale) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("forget_link", link);

    String content = loadTemplateFromPath("password-forget", locale);
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("forget_link");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(locale, userData,
        AbstractServiceHelper.getCacheValue(configCache, EMAIL_SUBJECT_FORGET, locale), content);
  }

  public boolean sendEmailDirectLogin(User user) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("domain_url",
        AbstractServiceHelper.getCachedDomain(configCache, user.getLocale()));

    String content = loadTemplateFromPath("direct-login", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("domain_url");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData, AbstractServiceHelper
        .getCacheValue(configCache, EMAIL_SUBJECT_DIRECT_LOGIN, user.getLocale()), content);
  }

  public boolean sendIqTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = AbstractServiceHelper.getCachedDomain(configCache, testResult.getLocale());
    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_iq_score", testResult.getPoints().toString());
    userData.put("test_iq_result", "IQ " + testResult.getPoints().toString());

    userData.put("unsubscribe_block",
        "<tr><td class=\"unsubscribe\">If you no longer wish to receive messages like this one, you can <a href=\""
            + domainUrl + "/login?token=" + user.getToken()
            + "&returnUrl=%2Fsettings\">unsubscribe</a>. </td></tr>");

    userData.put("test_type_title",
        AbstractServiceHelper.getCachedTitleByTest(configCache, testResult));

    String content = loadTemplateFromPath("user-finish-iq-test", testResult.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_iq_score");
    fieldsRequired.add("test_type_title");
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_type_title");
    fieldsRequiredSubject.add("test_iq_result");
    
    String subject = AbstractServiceHelper.getCacheValue(configCache, EMAIL_SUBJECT_TEST_RESULT,
        testResult.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(testResult.getLocale(), userData, subject, content);
  }

  public boolean sendTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = AbstractServiceHelper.getCachedDomain(configCache, testResult.getLocale());

    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_score", testResult.getPoints() + " / " + testResult.getQuestionSet().size());
    userData.put("test_iq_result", testResult.getPoints() + " / " + testResult.getQuestionSet().size());

    userData.put("unsubscribe_block",
        "<tr><td class=\"unsubscribe\">If you no longer wish to receive messages like this one, you can <a href=\""
            + domainUrl + "/login?token=" + user.getToken()
            + "&returnUrl=%2Fsettings\">unsubscribe</a>. </td></tr>");

    userData.put("test_type_title",
        AbstractServiceHelper.getCachedTitleByTest(configCache, testResult));

    String content = loadTemplateFromPath("user-finish-test", testResult.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_score");
    fieldsRequired.add("test_type_title");
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_type_title");
    fieldsRequiredSubject.add("test_iq_result");

    String subject = AbstractServiceHelper.getCacheValue(configCache, EMAIL_SUBJECT_TEST_RESULT,
        testResult.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(testResult.getLocale(), userData, subject, content);
  }
  
  public boolean sendRegistrationImportUser(User user) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = AbstractServiceHelper.getCachedDomain(configCache, user.getLocale());
    userData.put("profile_link", domainUrl + user.getUrl());
    userData.put("test_iq_score", user.getIq().toString());
    userData.put("domain_url",
        AbstractServiceHelper.getCachedDomain(configCache, user.getLocale()));

    preparePromoFields(userData, user);
    
    String content = loadTemplateFromPath("import-user", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("domain_url");
    fieldsRequired.add("test_iq_score");
    fieldsRequired.add("profile_link");
    preparePromoFieldsRequired(fieldsRequired);
    
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_iq_score");

    String subject = AbstractServiceHelper.getCacheValue(configCache, EMAIL_SUBJECT_IMPORT_USER,
        user.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }
  

  private void preparePromoFields(HashMap<String, String> userData, User user) {
    userData.put("test_url_mega_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_url_mega_iq", user.getLocale()));
    userData.put("test_title_mega_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_title_mega_iq", user.getLocale()));
    userData.put("test_title_promo_mega_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_title_promo_mega_iq", user.getLocale()));
    userData.put("test_pic_mega_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_pic_mega_iq", user.getLocale()));
    userData.put("test_questions_mega_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_questions_mega_iq", user.getLocale()));
    userData.put("test_time_mega_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_time_mega_iq", user.getLocale()));

    userData.put("test_url_standard_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_url_standard_iq", user.getLocale()));
    userData.put("test_title_standard_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_title_standard_iq", user.getLocale()));
    userData.put("test_title_promo_standard_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_title_promo_standard_iq", user.getLocale()));
    userData.put("test_pic_standard_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_pic_standard_iq", user.getLocale()));
    userData.put("test_questions_standard_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_questions_standard_iq", user.getLocale()));
    userData.put("test_time_standard_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_time_standard_iq", user.getLocale()));

    userData.put("test_url_practice_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_url_practice_iq", user.getLocale()));
    userData.put("test_title_practice_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_title_practice_iq", user.getLocale()));
    userData.put("test_title_promo_practice_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_title_promo_practice_iq", user.getLocale()));
    userData.put("test_pic_practice_iq",
        AbstractServiceHelper.getCacheValue(configCache, "test_pic_practice_iq", user.getLocale()));
    userData.put("test_questions_practice_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_questions_practice_iq", user.getLocale()));
    userData.put("test_time_practice_iq", AbstractServiceHelper.getCacheValue(configCache,
        "test_time_practice_iq", user.getLocale()));
  }


  private void preparePromoFieldsRequired(List<String> fieldsRequired) {
    fieldsRequired.add("test_url_mega_iq");
    fieldsRequired.add("test_title_mega_iq");
    fieldsRequired.add("test_title_promo_mega_iq");
    fieldsRequired.add("test_pic_mega_iq");
    fieldsRequired.add("test_questions_mega_iq");
    fieldsRequired.add("test_time_mega_iq");

    fieldsRequired.add("test_url_standard_iq");
    fieldsRequired.add("test_title_standard_iq");
    fieldsRequired.add("test_title_promo_standard_iq");
    fieldsRequired.add("test_pic_standard_iq");
    fieldsRequired.add("test_questions_standard_iq");
    fieldsRequired.add("test_time_standard_iq");

    fieldsRequired.add("test_url_practice_iq");
    fieldsRequired.add("test_title_practice_iq");
    fieldsRequired.add("test_title_promo_practice_iq");
    fieldsRequired.add("test_pic_practice_iq");
    fieldsRequired.add("test_questions_practice_iq");
    fieldsRequired.add("test_time_practice_iq");
  }
}
