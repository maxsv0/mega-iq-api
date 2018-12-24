package com.max.appengine.springboot.megaiq.rest;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;

@RestController
public class GeoIpController extends AbstractApiController {
  
  @RequestMapping(value = "/ip", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> uploadFile(HttpServletRequest request) {
    String location = "Berlin, Germany";
    
    ApiResponseBase result = new ApiResponseBase();
    result.setOk();
    result.setMsg(location);
    result.setDate(new Date());
    
    return sendResponseOk(result);
  }
  
}
