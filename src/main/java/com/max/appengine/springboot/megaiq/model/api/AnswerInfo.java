package com.max.appengine.springboot.megaiq.model.api;

import java.util.Objects;
import java.util.StringJoiner;

public class AnswerInfo {
    private Integer points;

    private Boolean isCorrect;

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AnswerInfo.class.getSimpleName() + "[", "]")
                .add("points=" + points)
                .add("isCorrect=" + isCorrect)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerInfo that = (AnswerInfo) o;
        return Objects.equals(points, that.points) &&
                Objects.equals(isCorrect, that.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, isCorrect);
    }
}
