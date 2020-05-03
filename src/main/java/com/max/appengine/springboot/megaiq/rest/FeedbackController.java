/*
 * Copyright 2020 mega-iq.com
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

package com.max.appengine.springboot.megaiq.rest;

import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.max.appengine.springboot.megaiq.model.Feedback;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.api.RequestFeedback;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.FeedbackReporitory;
import com.max.appengine.springboot.megaiq.service.ConfigurationService;
import com.max.appengine.springboot.megaiq.service.UserService;

@CrossOrigin
@RestController
public class FeedbackController extends AbstractApiController {

  public static final String MESSAGE_INVALID_ACCESS = "message_invalid_access";

  private final Table<String, Locale, String> configCache = HashBasedTable.create();

  private final UserService userService;

  private final FeedbackReporitory feedbackReporitory;

  @Autowired
  public FeedbackController(ConfigurationService configurationService, UserService userService,
      FeedbackReporitory feedbackReporitory) {
    this.userService = userService;
    this.feedbackReporitory = feedbackReporitory;

    cacheValuesForAllLocales(configurationService, configCache, MESSAGE_INVALID_ACCESS);
  }

  @RequestMapping(value = "/user/feedback", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ApiResponseBase> index(HttpServletRequest request,
      @RequestParam Optional<String> locale, @RequestBody RequestFeedback requestFeedback) {
    Locale userLocale = loadLocale(locale);


    Optional<String> token = getTokenFromHeader(request);

    if (!token.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Optional<User> userCurrentResult = userService.getUserByToken(token.get());
    if (!userCurrentResult.isPresent()) {
      return sendResponseError(MESSAGE_INVALID_ACCESS, configCache, userLocale);
    }

    Feedback feedback = new Feedback();
    feedback.setLocale(userLocale);
    feedback.setUserId(userCurrentResult.get().getId());
    feedback.setCode(requestFeedback.getCode());
    feedback.setQuestion(requestFeedback.getQuestion());
    feedback.setScore(requestFeedback.getScore());
    feedback.setComment(requestFeedback.getComment());
    feedback.setCreateDate(new Date());

    Feedback feedbackResult = feedbackReporitory.save(feedback);

    return sendResponseBaseRaw(feedbackResult.getId().toString(), userLocale);
  }
}
