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

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.service.StorageService;

@CrossOrigin
@RestController
public class StorageController extends AbstractApiController {
  private final StorageService storageService;

  @Autowired
  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @RequestMapping(value = "/storage/create", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> createUploadUrl(HttpServletRequest request,
      @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);
    
    String uploadUrl = storageService.createUploadUrl();

    return sendResponseBaseRaw(uploadUrl.substring(22), userLocale);
  }

  @RequestMapping(value = "/storage/serve", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> serveFile(HttpServletRequest request,
      @RequestParam String key, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);
    
    String servedUrl = storageService.serveFile(key);

    return sendResponseBaseRaw(servedUrl, userLocale);
  }

  @RequestMapping(value = "/storage/serve2", method = RequestMethod.GET)
  public ResponseEntity<ApiResponseBase> serveFileByPath(HttpServletRequest request,
      @RequestParam String path, @RequestParam Optional<String> locale) {
    Locale userLocale = loadLocale(locale);
    
    String servedUrl = storageService.serveFileByPath(path);

    return sendResponseBaseRaw(servedUrl, userLocale);
  }

}
