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
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;

@Service
public class EmailService extends AbstractSendgridEmailService {
  private final ConfigurationService configurationService;

  @Autowired
  public EmailService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  public boolean sendEmailRegistration(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("new-user-registration", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    content = insertFields(content, fieldsRequired, userData);

    String subject = this.configurationService
        .getConfigValueByNameAndLocale("email_subject_new_user", user.getLocale());

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendEmailRegistrationWithVerify(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("verify_link", link);

    String content = loadTemplateFromPath("new-user-registration-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("verify_link");
    content = insertFields(content, fieldsRequired, userData);

    String subject = this.configurationService
        .getConfigValueByNameAndLocale("email_subject_new_user", user.getLocale());

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendEmailVerify(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("verify_link", link);

    String content = loadTemplateFromPath("user-email-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("verify_link");
    content = insertFields(content, fieldsRequired, userData);

    String subject = this.configurationService
        .getConfigValueByNameAndLocale("email_subject_email_verify", user.getLocale());

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendEmailForget(User user, String link) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("forget_link", link);

    String content = loadTemplateFromPath("password-forget", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("forget_link");
    content = insertFields(content, fieldsRequired, userData);

    String subject = this.configurationService.getConfigValueByNameAndLocale("email_subject_forget",
        user.getLocale());

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendEmailDirectLogin(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("direct-login", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    content = insertFields(content, fieldsRequired, userData);

    String subject = this.configurationService
        .getConfigValueByNameAndLocale("email_subject_direct_login", user.getLocale());

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendIqTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = this.configurationService.getDomainByLocale(testResult.getLocale());

    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_iq_score", testResult.getPoints().toString());

    userData.put("unsubscribe_block",
        "<tr><td class=\"unsubscribe\">If you no longer wish to receive messages like this one, you can <a href=\""
            + domainUrl + "/login?token=" + user.getToken()
            + "&returnUrl=%2Fsettings\">unsubscribe</a>. </td></tr>");

    userData.put("test_type_title", this.configurationService.getTestResultTitle(testResult));

    String content = loadTemplateFromPath("user-finish-iq-test", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_iq_score");
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_type_title");

    String subject = this.configurationService
        .getConfigValueByNameAndLocale("email_subject_test_result", testResult.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(user.getLocale(), userData, subject, content);
  }

  public boolean sendTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    String domainUrl = this.configurationService.getDomainByLocale(testResult.getLocale());

    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_score", testResult.getPoints() + " / " + testResult.getQuestionSet().size());

    userData.put("unsubscribe_block",
        "<tr><td class=\"unsubscribe\">If you no longer wish to receive messages like this one, you can <a href=\""
            + domainUrl + "/login?token=" + user.getToken()
            + "&returnUrl=%2Fsettings\">unsubscribe</a>. </td></tr>");

    userData.put("test_type_title", this.configurationService.getTestResultTitle(testResult));

    String content = loadTemplateFromPath("user-finish-test", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_score");
    content = insertFields(content, fieldsRequired, userData);

    List<String> fieldsRequiredSubject = new ArrayList<String>();
    fieldsRequiredSubject.add("test_type_title");

    String subject = this.configurationService
        .getConfigValueByNameAndLocale("email_subject_test_result", testResult.getLocale());
    subject = insertFields(subject, fieldsRequiredSubject, userData);

    return loadTemplateAndSend(testResult.getLocale(), userData, subject, content);
  }

}
