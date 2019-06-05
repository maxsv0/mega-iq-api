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

    // ** details here
    // https://stackoverflow.com/questions/45100138/how-to-configure-google-appengine-to-work-with-vector-graphic
    String fontConfig = System.getProperty("java.home") + File.separator + "lib" + File.separator
        + "fontconfig.Prodimage.properties";

    if (new File(fontConfig).exists())
      System.setProperty("sun.awt.fontconfig", fontConfig);
  }

  public String createUserCertificate(User user) throws IOException {
    if (this.imageTemplate == null) {
      this.imageTemplate = loadTemplate(CERTIFICATE_TEMPLATE);
    }

    if (this.imageTemplate == null) {
      throw new RuntimeException("Certificate template is not loaded");
    }

    // validate user info
    if (user.getName() == null) {
      return null;
    }
    if (user.getIq() == null) {
      return null;
    }

    int width = imageTemplate.getWidth();
    int height = imageTemplate.getHeight();

    BufferedImage imgCertificate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    imgCertificate.createGraphics().drawImage(imageTemplate, 0, 0, null);

    Graphics graphics = imgCertificate.getGraphics();
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
    graphics.drawString(user.getName(), 10, 25);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(imgCertificate, "png", outputStream);
    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

    String fileName = "certificate-" + user.getId() + ".png";
    String filePath = this.storageService.uploadCertificateToStorage(fileName, inputStream);

    return this.storageService.serveFileByPath(filePath);
  }

  private BufferedImage loadTemplate(String fileName) throws IOException {
    File imageTemplate = File.createTempFile("certificate-", ".tmp");
    imageTemplate.deleteOnExit();

    this.storageService.fetchFile(fileName, imageTemplate);

    return ImageIO.read(imageTemplate);
  }
}
