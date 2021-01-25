package com.max.appengine.springboot.megaiq.model.api;

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
}
