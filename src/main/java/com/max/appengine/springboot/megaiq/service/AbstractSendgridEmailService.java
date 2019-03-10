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

import com.google.common.annotations.GwtCompatible;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

abstract class AbstractSendgridEmailService {

  public static final String EMAIL_FROM = "mail@mega-iq.com";

  public static final String EMAIL_FROM_NAME = "Mega-IQ";

  public String sendgridApiKey = "";

  public String domainUrl = "http://new.mega-iq.com";

  protected boolean loadTemplateAndSend(Locale locale, HashMap<String, String> userData,
      String subject, String content) {
    String template = loadTemplateFromPath("_template", locale);

    List<String> fieldsRequiredGlobal = new ArrayList<String>();
    fieldsRequiredGlobal.add("email_title");
    fieldsRequiredGlobal.add("email_preheader");
    fieldsRequiredGlobal.add("email_content");
    fieldsRequiredGlobal.add("unsubscribe_block");

    userData.put("email_title", subject);
    userData.put("email_preheader", subject);
    userData.put("email_content", content);
    if (!userData.containsKey("unsubscribe_block")) {
      userData.put("unsubscribe_block", "");
    }

    String emailBody = insertFields(template, fieldsRequiredGlobal, userData);

    return sendEmail(userData.get("email"), subject, emailBody);
  }

  protected HashMap<String, String> loadUserData(User user) {
    HashMap<String, String> userData = new HashMap<String, String>();

    userData.put("email", user.getEmail());
    userData.put("name", user.getName());

    if (user.getToken() != null) {
      userData.put("token", user.getToken());
    }

    return userData;
  }

  protected String insertFields(String content, List<String> fieldsRequired,
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

  protected String loadTemplateFromPath(String name, Locale locale) {
    if (name == null) {
      throw new RuntimeException("template name is empty. Locale: " + locale);
    }
    
    if (locale == null) {
      throw new RuntimeException("template locale is empty. Template name: " + name);
    }

    String path = "email/" + locale.toString() + "/" + name + ".html";
    InputStream inputStream =
        AbstractSendgridEmailService.class.getClassLoader().getResourceAsStream(path);

    if (inputStream == null) {
      throw new RuntimeException("Can't read from input stream. Path: " + path);
    }

    return new BufferedReader(new InputStreamReader(inputStream)).lines()
        .collect(Collectors.joining("\n"));
  }

  private boolean sendEmail(String to, String subject, String content) {
    if (sendgridApiKey == null || sendgridApiKey.isEmpty())
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
