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

package com.max.appengine.springboot.megaiq.integration.index;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.integration.AbstractIntegrationTest;
import com.max.appengine.springboot.megaiq.model.api.ApiResponseBase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class IndexControllerIT extends AbstractIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void testIndexPage() throws Exception {
    MvcResult resultApi = mvc
        .perform(MockMvcRequestBuilders.get("/")).andReturn();
    
    log.info("MvcResult is = {}", resultApi.getResponse().getContentAsString());
    ObjectMapper objectMapper = new ObjectMapper();
    ApiResponseBase response =
        objectMapper.readValue(resultApi.getResponse().getContentAsString(), ApiResponseBase.class);
    log.info("response = {}", response);
    
    assertTrue(response.isOk());
  }
}
