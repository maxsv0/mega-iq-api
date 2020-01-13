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

package com.max.appengine.springboot.megaiq.unit;

import static org.junit.Assert.assertNotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalImagesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.service.CertificateService;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CertificateServiceTest extends AbstractUnitTest {
  private static final LocalServiceTestHelper helperServices = new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig(), 
      new LocalBlobstoreServiceTestConfig(), new LocalImagesServiceTestConfig());
  
  @Autowired
  private CertificateService certificateService;
  
  @Autowired
  private UserReporitory userReporitory;
  
  private User user;
  
  @Before
  public void setup() {
    user = userReporitory.save(generateUser());
    
    helperServices.setUp();
    
    new MockUp<CertificateService>() {
      @Mock
      public BufferedImage loadTemplate(String fileName) throws IOException  {
        InputStream imageTemplate = this.getClass().getClassLoader().getResourceAsStream("cert_blank.png");
        return ImageIO.read(imageTemplate);
      }
    };
  }

  @After
  public void tearDown() throws Exception {
    helperServices.tearDown();
  }
  
  @Test
  public void testCreateCertificate() throws IOException {
    log.info("Start create certificate");
    String certificate = this.certificateService.createUserCertificate(user);
    log.info("Got certificate = {}", certificate);
    assertNotNull(certificate);
  }
}
