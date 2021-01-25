package com.max.appengine.springboot.megaiq.model.api;

import java.time.Duration;

public class TestResultInfo {
    private String duration;

    private Integer questions;

    private Integer answersCorrect;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getQuestions() {
        return questions;
    }

    public void setQuestions(Integer questions) {
        this.questions = questions;
    }

    public Integer getAnswersCorrect() {
        return answersCorrect;
    }

    public void setAnswersCorrect(Integer answersCorrect) {
        this.answersCorrect = answersCorrect;
    }
}
