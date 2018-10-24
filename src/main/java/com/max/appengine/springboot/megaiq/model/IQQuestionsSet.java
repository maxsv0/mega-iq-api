package com.max.appengine.springboot.megaiq.model;

import java.util.ArrayList;

import com.max.appengine.springboot.megaiq.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.enums.Locale;

public class IQQuestionsSet {
	private Integer id;
	private Integer testId;
	private Integer questionIq;
	private Integer points;
	private Integer answerCorrect;
	private Integer answerUser;
	private ArrayList<IqQuestionGroup> groups;
	private Locale locale;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}

	public Integer getQuestionIq() {
		return questionIq;
	}

	public void setQuestionIq(Integer questionIq) {
		this.questionIq = questionIq;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Integer getAnswerCorrect() {
		return answerCorrect;
	}

	public void setAnswerCorrect(Integer answerCorrect) {
		this.answerCorrect = answerCorrect;
	}

	public Integer getAnswerUser() {
		return answerUser;
	}

	public void setAnswerUser(Integer answerUser) {
		this.answerUser = answerUser;
	}

	public ArrayList<IqQuestionGroup> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<IqQuestionGroup> groups) {
		this.groups = groups;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
