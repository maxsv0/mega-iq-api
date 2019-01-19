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
  public static final String EMAIL_FROM = "Mega-IQ <mail@mega-iq.com>";

  public String sendgridApiKey = "";

  public boolean sendEmailRegistration(User user, Locale locale) {
    String subject = "Welcome to Mega-IQ";
    String content = loadTemplateFromPath("new-user-registration", locale);

    content = insertUserInfo(user, content);

    return sendEmail(user.getEmail(), subject, content);
  }

  public boolean sendTestResult(User user, TestResult testResult, Locale locale) {
    String subject = "Test finished";
    String content = loadTemplateFromPath("user-finish-test", locale);

    content = insertUserInfo(user, content);
    content = insertTestResultInfo(testResult, content);

    return sendEmail(user.getEmail(), subject, content);
  }

  public boolean sendEmailVerify(User user, Locale locale) {
    String subject = "Your Mega-IQ account: Email address verification";
    String content = loadTemplateFromPath("user-email-verify", locale);

    content = insertUserInfo(user, content);

    return sendEmail(user.getEmail(), subject, content);
  }

  private String loadTemplateFromPath(String name, Locale locale) {
    String path = "email/" + locale.toString() + "/" + name + ".html";
    InputStream inputStream = EmailService.class.getClassLoader().getResourceAsStream(path);

    return new BufferedReader(new InputStreamReader(inputStream)).lines()
        .collect(Collectors.joining("\n"));
  }

  private String insertTestResultInfo(TestResult testResult, String content) {
    String contentNew = new String(content);
    contentNew = contentNew.replace("{test_url}", testResult.getUrl());

    return contentNew;
  }

  private String insertUserInfo(User user, String content) {
    String contentNew = new String(content);

    contentNew = contentNew.replace("{email}", user.getEmail());
    contentNew = contentNew.replace("{name}", user.getName());
    if (user.getIq() != null) {
      contentNew = contentNew.replace("{iq}", user.getIq().toString());
    }
    contentNew = contentNew.replace("{url}", user.getUrl());
    contentNew = contentNew.replace("{password}", user.getPassword());

    Optional<UserToken> tokenAccess = user.getUserTokenByType(UserTokenType.ACCESS);
    if (tokenAccess.isPresent()) {
      contentNew = contentNew.replace("{token_access}", tokenAccess.get().getValue());
    }

    Optional<UserToken> tokenForget = user.getUserTokenByType(UserTokenType.FORGET);
    if (tokenForget.isPresent()) {
      contentNew = contentNew.replace("{token_forget}", tokenForget.get().getValue());
    }
    
    Optional<UserToken> tokenVerify = user.getUserTokenByType(UserTokenType.VERIFY);
    if (tokenVerify.isPresent()) {
      contentNew = contentNew.replace("{token_verify}", tokenVerify.get().getValue());
    }

    return contentNew;
  }

  private boolean sendEmail(String to, String subject, String content) {
    if (sendgridApiKey == null)
      throw new RuntimeException("Email Service API key not set");

    Email fromEmail = new Email(EMAIL_FROM);
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
