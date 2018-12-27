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

    // TODO: add code here
    String location = "Berlin, Germany";
    
    ApiResponseBase result = new ApiResponseBase();
    result.setOk();
    result.setMsg(location);
    result.setDate(new Date());
    
    return sendResponseOk(result);
  }
  
}
