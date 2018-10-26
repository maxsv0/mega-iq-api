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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.entity.EntityAnswer;
import com.max.appengine.springboot.megaiq.model.entity.EntityQuestion;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;

@Service
public class QuestionsService {

	private List<EntityQuestion> questionsList;
	private List<EntityAnswer> answersList;

	private final AnswerReporitory answerReporitory;
	private final QuestionReporitory questionReporitory;

	@Autowired
	public QuestionsService(AnswerReporitory answerReporitory, QuestionReporitory questionReporitory) {
		this.answerReporitory = answerReporitory;
		this.questionReporitory = questionReporitory;

		// load answers
		this.answersList = this.answerReporitory.findAll();

		// load question
		this.questionsList = this.questionReporitory.findAll();
	}

	public Question getQuestionById(Integer questionId, Locale locale) {

		for (EntityQuestion question : this.questionsList) {
			if (question.getId().equals(questionId) && question.getLocale().equals(locale))
				return new Question(question);
		}

		return null;
	}

	public ArrayList<Answer> getAnswersByQuestionId(Integer questionId, Locale locale) {
		ArrayList<Answer> answersList = new ArrayList<Answer>();

		for (EntityAnswer answer : this.answersList) {
			if (answer.getQuestionId().equals(questionId) && answer.getLocale().equals(locale))
				answersList.add(new Answer(answer));
		}

		return answersList;
	}
}
