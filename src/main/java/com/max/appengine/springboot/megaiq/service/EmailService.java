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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Service
public class EmailService {
  public static final String EMAIL_SUBJECT_NEW_USER = "Welcome to Mega-IQ";

  public static final String EMAIL_SUBJECT_EMAIL_VERIFY =
      "Your Mega-IQ account: Email address verification";

  public static final String EMAIL_SUBJECT_TEST_RESULT = "Test finished";

  public static final String EMAIL_FROM = "mail@mega-iq.com";

  public static final String EMAIL_FROM_NAME = "Mega-IQ";

  public String sendgridApiKey = "";

  public String domainUrl = "http://new.mega-iq.com";

  public boolean sendEmailRegistration(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("new-user-registration", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData, EMAIL_SUBJECT_NEW_USER, content);
  }

  public boolean sendEmailRegistrationWithVerify(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("new-user-registration-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("token_verify");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData, EMAIL_SUBJECT_NEW_USER, content);
  }

  public boolean sendEmailVerify(User user) {
    HashMap<String, String> userData = loadUserData(user);

    String content = loadTemplateFromPath("user-email-verify", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("token_verify");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData, EMAIL_SUBJECT_EMAIL_VERIFY, content);
  }

  public boolean sendTestResult(User user, TestResult testResult) {
    HashMap<String, String> userData = loadUserData(user);
    userData.put("test_url", domainUrl + testResult.getUrl());
    userData.put("test_iq_score", testResult.getPoints().toString());

    String content = loadTemplateFromPath("user-finish-test", user.getLocale());
    List<String> fieldsRequired = new ArrayList<String>();
    fieldsRequired.add("name");
    fieldsRequired.add("test_url");
    fieldsRequired.add("test_iq_score");
    content = insertFields(content, fieldsRequired, userData);

    return loadTemplateAndSend(user.getLocale(), userData, EMAIL_SUBJECT_TEST_RESULT, content);
  }

  private boolean loadTemplateAndSend(Locale locale, HashMap<String, String> userData,
      String subject, String content) {
    String template = loadTemplateFromPath("_template", locale);

    List<String> fieldsRequiredGlobal = new ArrayList<String>();
    fieldsRequiredGlobal.add("email_title");
    fieldsRequiredGlobal.add("email_preheader");
    fieldsRequiredGlobal.add("email_content");
    fieldsRequiredGlobal.add("link_unsubscribe");
    fieldsRequiredGlobal.add("token_access");

    userData.put("link_unsubscribe",
        domainUrl + "/login?token={token_access}&returnUrl=%2Fsettings");
    userData.put("email_title", subject);
    userData.put("email_preheader", subject);
    userData.put("email_content", content);

    String emailBody = insertFields(template, fieldsRequiredGlobal, userData);

    return sendEmail(userData.get("email"), subject, emailBody);
  }

  private String insertFields(String content, List<String> fieldsRequired,
      HashMap<String, String> data) {
    String contentNew = new String(content);

    for (String key : fieldsRequired) {
      String keyToReplace = "{" + key + "}";

      if (!data.containsKey(key)) {
        throw new RuntimeException("Email parsing failed. Data set missing key '" + key + "'");
      }

      if (!contentNew.contains(keyToReplace)) {
        throw new RuntimeException(
            "Email parsing failed. Content missing key '" + keyToReplace + "'");
      }

      contentNew = contentNew.replace(keyToReplace, data.get(key));
    }

    return contentNew;
  }

  private HashMap<String, String> loadUserData(User user) {
    HashMap<String, String> userData = new HashMap<String, String>();

    userData.put("email", user.getEmail());
    userData.put("name", user.getName());

    Optional<UserToken> tokenAccess = user.getUserTokenByType(UserTokenType.ACCESS);
    if (tokenAccess.isPresent()) {
      userData.put("token_access", tokenAccess.get().getValue());
    }

    Optional<UserToken> tokenForget = user.getUserTokenByType(UserTokenType.FORGET);
    if (tokenForget.isPresent()) {
      userData.put("token_forget", tokenForget.get().getValue());
    }

    Optional<UserToken> tokenVerify = user.getUserTokenByType(UserTokenType.VERIFY);
    if (tokenVerify.isPresent()) {
      userData.put("token_verify", tokenVerify.get().getValue());
    }

    return userData;
  }

  private String loadTemplateFromPath(String name, Locale locale) {
    String path = "email/" + locale.toString() + "/" + name + ".html";
    InputStream inputStream = EmailService.class.getClassLoader().getResourceAsStream(path);

    return new BufferedReader(new InputStreamReader(inputStream)).lines()
        .collect(Collectors.joining("\n"));
  }

  private boolean sendEmail(String to, String subject, String content) {
    if (sendgridApiKey == null)
      throw new RuntimeException("Email Service API key not set");

    Email fromEmail = new Email(EMAIL_FROM, EMAIL_FROM_NAME);
    Email toEmail = new Email(to);

    Content contentObj = new Content("text/html", content);
    Mail mail = new Mail(fromEmail, subject, toEmail, contentObj);

    SendGrid sg = new SendGrid(sendgridApiKey);
    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);

      if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
        return true;
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    return false;
  }
}
