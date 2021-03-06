package com.max.appengine.springboot.megaiq.rest;

import com.max.appengine.springboot.megaiq.model.TestResult;
import com.max.appengine.springboot.megaiq.model.api.ResponseStatKpiTestCount;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
public class StatController extends AbstractApiController {
    private final TestResultService testResultService;
    static final long DAY = 24 * 60 * 60 * 1000;

    @Autowired
    public StatController(TestResultService testResultService) {
        this.testResultService = testResultService;
    }

    @RequestMapping(value = "/stat/kpi-test-count", method = RequestMethod.GET, consumes = "application/json")
    public ResponseEntity<ResponseStatKpiTestCount> statKpiTestCount() {

        ResponseStatKpiTestCount response = new ResponseStatKpiTestCount();

        List<TestResult> listEn = this.testResultService.findLatestResult(Locale.EN, PageRequest.of(0, 50));
        listEn.removeIf(x -> notInLastDay(x.getCreateDate()));
        response.setCountEN(listEn.size());

        List<TestResult> listEs = this.testResultService.findLatestResult(Locale.ES, PageRequest.of(0, 50));
        listEs.removeIf(x -> notInLastDay(x.getCreateDate()));
        response.setCountES(listEs.size());

        List<TestResult> listDe = this.testResultService.findLatestResult(Locale.DE, PageRequest.of(0, 50));
        listDe.removeIf(x -> notInLastDay(x.getCreateDate()));
        response.setCountRU(listDe.size());

        List<TestResult> listRu = this.testResultService.findLatestResult(Locale.RU, PageRequest.of(0, 50));
        listRu.removeIf(x -> notInLastDay(x.getCreateDate()));
        response.setCountRU(listRu.size());

        return new ResponseEntity<ResponseStatKpiTestCount>(response, HttpStatus.OK);
    }

    public boolean notInLastDay(Date aDate) {
        return aDate.getTime() <= System.currentTimeMillis() - DAY;
    }
}
