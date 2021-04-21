package com.max.appengine.springboot.megaiq.model.api;

import java.util.Objects;
import java.util.StringJoiner;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", TestResultInfo.class.getSimpleName() + "[", "]")
                .add("duration='" + duration + "'")
                .add("questions=" + questions)
                .add("answersCorrect=" + answersCorrect)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestResultInfo that = (TestResultInfo) o;
        return Objects.equals(duration, that.duration) &&
                Objects.equals(questions, that.questions) &&
                Objects.equals(answersCorrect, that.answersCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, questions, answersCorrect);
    }
}
