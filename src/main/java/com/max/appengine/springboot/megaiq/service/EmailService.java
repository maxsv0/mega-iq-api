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

  public static final String EMAIL_SUBJECT_EMAIL_VERIFY = "email_subject_email_verify";

  public static final String EMAIL_SUBJECT_FORGET = "email_subject_forget";

  public static final String EMAIL_SUBJECT_DIRECT_LOGIN = "email_subject_direct_login";

  public static final String EMAIL_SUBJECT_TEST_RESULT = "email_subject_test_result";

  private final Table<String, Locale, String> configCache = HashBasedTable.create();

  @Autowired
  public EmailService(ConfigurationService configurationService) {
    cacheValuesForAllLocales(configurationService, configCache, EMAIL_SUBJECT_NEW_USER);
    cacheValuesForAllLocales(configurationService, configCache, EMAIL_SUBJECT_EMAIL_VERIFY);
    cacheValuesForAllLocales(configurationService, configCache, EMAIL_SUBJECT_FORGET);
    cacheValuesForAllLocales(configurationService, configCache, EMAIL_SUBJECT_DIRECT_LOGIN);
    cacheValuesForAllLocales(configurationService, configCache, EMAIL_SUBJECT_TEST_RESULT);
    cacheTitlesForAllTestType(configurationService, configCache);
    cacheDomainForAllLocale(configurationService, configCache);
  }

  public boolean sendEmailRegistration(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("new-user-registration", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        getCacheValue(configCache, EMAIL_SUBJECT_NEW_USER, user.getLocale()), content);
  }

  public boolean sendEmailRegistrationWithVerify(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("verify_link", link);

    String content = loadTemplateFromPath("new-user-registration-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("verify_link");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        getCacheValue(configCache, EMAIL_SUBJECT_NEW_USER, user.getLocale()), content);
  }

  public boolean sendEmailVerify(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("verify_link", link);

    String content = loadTemplateFromPath("user-email-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("verify_link");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        getCacheValue(configCache, EMAIL_SUBJECT_EMAIL_VERIFY, user.getLocale()), content);
  }

  public boolean sendEmailForget(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("forget_link", link);

    String content = loadTemplateFromPath("password-forget", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("forget_link");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        getCacheValue(configCache, EMAIL_SUBJECT_FORGET, user.getLocale()), content);
  }

  public boolean sendEmailDirectLogin(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("direct-login", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData,
        getCacheValue(configCache, EMAIL_SUBJECT_DIRECT_LOGIN, user.getLocale()), content);
  }

  public boolean sendIqTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = getCachedDomain(configCache, testResult.getLocale());

    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_iq_score", testResult.getPoints().toString());

    userData.put("unsubscribe_block",
        "<tr><td class=\"unsubscribe\">If you no longer wish to receive messages like this one, you can <a href=\""
            + domainUrl + "/login?token=" + user.getToken()
            + "&returnUrl=%2Fsettings\">unsubscribe</a>. </td></tr>");

    userData.put("test_type_title", getCachedTitleByTest(configCache, testResult));

    String content = loadTemplateFromPath("user-finish-iq-test", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_iq_score");
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_type_title");

    String subject = getCacheValue(configCache, EMAIL_SUBJECT_TEST_RESULT, user.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = getCachedDomain(configCache, testResult.getLocale());

    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_score", testResult.getPoints() + " / " + testResult.getQuestionSet().size());

    userData.put("unsubscribe_block",
        "<tr><td class=\"unsubscribe\">If you no longer wish to receive messages like this one, you can <a href=\""
            + domainUrl + "/login?token=" + user.getToken()
            + "&returnUrl=%2Fsettings\">unsubscribe</a>. </td></tr>");

    userData.put("test_type_title", getCachedTitleByTest(configCache, testResult));

    String content = loadTemplateFromPath("user-finish-test", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_score");
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_type_title");

    String subject = getCacheValue(configCache, EMAIL_SUBJECT_TEST_RESULT, user.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(testResult.getLocale(), userData, subject, content);
  }

}
