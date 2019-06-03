/*
 * Copyright 2019 mega-iq.com
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

package com.max.appengine.springboot.megaiq.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.User;

@Service
public class CertificateService {
  public static final String CERTIFICATE_TEMPLATE = "cert_blank_en.png";

  private final StorageService storageService;

  private BufferedImage imageTemplate;

  @Autowired
  public CertificateService(StorageService storageService) throws IOException {
    this.storageService = storageService;

    // this.imageTemplate = loadTemplate(CERTIFICATE_TEMPLATE);

    // setup for usage fonts
//    String fontConfig = System.getProperty("java.home") + File.separator + "lib" + File.separator
//        + "fontconfig.Prodimage.properties";
//    if (new File(fontConfig).exists())
//      System.setProperty("sun.awt.fontconfig", fontConfig);
  }

  public String createUserCertificate(User user) throws IOException {
    this.imageTemplate = loadTemplate(CERTIFICATE_TEMPLATE);
    
    int width = imageTemplate.getWidth();
    int height = imageTemplate.getHeight();

    BufferedImage imgCertificate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    imgCertificate.createGraphics().drawImage(imageTemplate, 0, 0, null);

//    Graphics graphics = imageTemplate.getGraphics();
//    graphics.setColor(Color.BLACK);
//    graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
//    graphics.drawString(user.getName(), 10, 25);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(imgCertificate, "jpg", os);
    InputStream is = new ByteArrayInputStream(os.toByteArray());
    String filePath = this.storageService.uploadCertificateToStorage(is);

    return this.storageService.serveFileByPath(filePath);
  }

  private BufferedImage loadTemplate(String fileName) throws IOException {
    File imageTemplate = File.createTempFile("certificate-", ".tmp");
    imageTemplate.deleteOnExit();

    this.storageService.fetchFile(fileName, imageTemplate);

    return ImageIO.read(imageTemplate);
  }
}
