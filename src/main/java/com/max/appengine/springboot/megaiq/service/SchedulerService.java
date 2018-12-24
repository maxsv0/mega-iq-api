package com.max.appengine.springboot.megaiq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

  private final TestResultService testResultService;
  
  @Autowired
  public SchedulerService(TestResultService testResultService) {
    super();

    this.testResultService = testResultService;
  }
  
  @Scheduled(cron = "5 * * * * ?")
  private void triggerScheduleJobs() {
    
    testResultService.expireTestResults();
    
  }
}
