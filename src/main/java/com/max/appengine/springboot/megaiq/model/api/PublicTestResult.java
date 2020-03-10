package com.max.appengine.springboot.megaiq.model.api;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.max.appengine.springboot.megaiq.model.AbstractQuestionUser;
import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.model.enums.IqTestType;

@JsonInclude(Include.NON_NULL)
public class PublicTestResult {
  private String url;
  
  private IqTestType type;
  
  private IqTestStatus status;
  
  private Date createDate;
  
  private Date finishDate;
  
  private Integer points;
  
  private Integer questions;
  
  private Integer progress;
  
  private String userPic;
  
  private Integer userIq;
  
  private Boolean hasCertificate;
  
  public PublicTestResult(TestResult testResult, String userPic, Integer userIq, Boolean hasCertificate) {
    super();
    
    this.type = testResult.getType();
    this.status = testResult.getStatus();
    this.createDate = testResult.getCreateDate();
    this.finishDate = testResult.getFinishDate();
    this.points = testResult.getPoints();
    
    if (testResult.getStatus().equals(IqTestStatus.ACTIVE)) {

      if (testResult.getQuestionSet() != null) {
        this.setProgress(0);
        
        this.setQuestions(testResult.getQuestionSet().size());
        
        for (AbstractQuestionUser questionUser : testResult.getQuestionSet()) {
          if (questionUser.getAnswerUser() != null) {
            this.setProgress(this.getProgress() + 1);
          }
        }

        this.setProgress(
            (int) Math.floor(100 * this.getProgress() / testResult.getQuestionSet().size()));
      }
    } else {
      
      if (userPic != null) {
        this.url = testResult.getUrl();
        this.userIq = userIq;
        this.hasCertificate = hasCertificate;
      }
    }
    
    this.userPic = userPic;
  }

  public PublicTestResult() {
    super();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public IqTestType getType() {
    return type;
  }

  public void setType(IqTestType type) {
    this.type = type;
  }

  public IqTestStatus getStatus() {
    return status;
  }

  public void setStatus(IqTestStatus status) {
    this.status = status;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getFinishDate() {
    return finishDate;
  }

  public void setFinishDate(Date finishDate) {
    this.finishDate = finishDate;
  }

  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  public Integer getProgress() {
    return progress;
  }

  public void setProgress(Integer progress) {
    this.progress = progress;
  }
  
  public Integer getQuestions() {
    return questions;
  }

  public void setQuestions(Integer questions) {
    this.questions = questions;
  }

  public String getUserPic() {
    return userPic;
  }

  public void setUserPic(String userPic) {
    this.userPic = userPic;
  }

  public Integer getUserIq() {
    return userIq;
  }

  public void setUserIq(Integer userIq) {
    this.userIq = userIq;
  }

  public Boolean isHasCertificate() {
    return hasCertificate;
  }

  public void setHasCertificate(Boolean hasCertificate) {
    this.hasCertificate = hasCertificate;
  }

}
