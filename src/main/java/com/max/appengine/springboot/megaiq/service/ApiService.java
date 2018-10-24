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

package com.max.appengine.springboot.megaiq.service;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.max.appengine.springboot.megaiq.model.ApiResponseEntity;

@Service
public class ApiService {

	public ResponseEntity<ApiResponseEntity> index(HttpServletRequest request) {
		ApiResponseEntity result = new ApiResponseEntity();
		
		return new ResponseEntity<ApiResponseEntity>(result, HttpStatus.OK);
	}
	

	public ResponseEntity<ApiResponseEntity> iqTestDetails(UUID testId) {
		ApiResponseEntity result = new ApiResponseEntity();
		
		result.setOk();
		
		return new ResponseEntity<ApiResponseEntity>(result, HttpStatus.OK);
	}
	
}