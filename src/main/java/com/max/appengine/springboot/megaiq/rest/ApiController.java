/*
 * Copyright 2018 mega-iq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.max.appengine.springboot.megaiq.rest;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.max.appengine.springboot.megaiq.model.ApiResponseEntity;
import com.max.appengine.springboot.megaiq.service.ApiService;

@RestController
public class ApiController {
	private final ApiService service;

	@Autowired
	public ApiController(ApiService service) {
		this.service = service;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<ApiResponseEntity> index(HttpServletRequest request) {
		return service.index(request);
	}
	 
	@RequestMapping(value = "/test/{testId}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponseEntity> iqTestDetails(@PathVariable UUID testId) {
		return service.iqTestDetails(testId);
	}
}
