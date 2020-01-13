package com.max.appengine.springboot.megaiq.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Service
public class CertificateService {
  public static final String CERTIFICATE_TEMPLATE_PATH = "certificate-template/";

  public static final String CERTIFICATE_TEMPLATE = "_normal.png";

  public static final Color COLOR_TEXT_SHADOW = new Color(255, 255, 170);

  public static final Color COLOR_TEXT = new Color(165, 115, 190);

  private final StorageService storageService;

  private Map<Locale, BufferedImage> imageTemplates = new HashMap<Locale, BufferedImage>();

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
    // validate user data
    if (user.getLocale() == null || user.getName() == null || user.getIq() == null) {
      return null;
    }

    if (!this.imageTemplates.containsKey(user.getLocale())) {
      BufferedImage imageTemplate =
          loadTemplate(CERTIFICATE_TEMPLATE_PATH + user.getLocale() + CERTIFICATE_TEMPLATE);
      if (imageTemplate == null) {
        throw new RuntimeException("Certificate template is not loaded");
      }

      this.imageTemplates.put(user.getLocale(), imageTemplate);
    }

    BufferedImage imageTemplate = this.imageTemplates.get(user.getLocale());

    int width = imageTemplate.getWidth();
    int height = imageTemplate.getHeight();

    BufferedImage imgCertificate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    imgCertificate.createGraphics().drawImage(imageTemplate, 0, 0, null);

    Graphics2D graphics = (Graphics2D) imgCertificate.getGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // write user name aligned to center
    // default left padding is 270px;
    int xName = width - 270*2 - (user.getName().length() / 2) * 40;
    if (xName < 250) {
      xName = 250;
    }
    if (xName > 500) {
      xName = 500;
    }
    
    writeGraphics(graphics, user.getName(), xName, 458, new Font(Font.SERIF, Font.ITALIC, 40));

    // user iq result
    writeGraphics(graphics, user.getIq().toString(), 440, 650,
        new Font(Font.SERIF, Font.ITALIC, 60));

    // current date
    DateTime dt = new DateTime();
    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    writeGraphics(graphics, dt.toString(dtf), 220, 805, new Font(Font.SERIF, Font.ITALIC, 40));

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(imgCertificate, "png", outputStream);
    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

    DateTimeFormatter fileDtf = DateTimeFormat.forPattern("dd-MM-yyyy-HH-mm-ss");
    String fileName = "certificate-" + user.getId() + "-" + dt.toString(fileDtf) + ".png";
    String filePath = this.storageService.uploadCertificateToStorage(fileName, inputStream);

    return this.storageService.serveFileByPath(filePath);
  }

  private void writeGraphics(Graphics2D graphics, String text, int x, int y, Font font) {
    graphics.setFont(font);

    graphics.setColor(COLOR_TEXT_SHADOW);
    graphics.drawString(text, x + 2, y + 2);

    graphics.setColor(COLOR_TEXT);
    graphics.drawString(text, x, y);
  }

  private BufferedImage loadTemplate(String fileName) throws IOException {
    File imageTemplate = File.createTempFile("certificate-", ".tmp");
    imageTemplate.deleteOnExit();

    this.storageService.fetchFile(fileName, imageTemplate);

    return ImageIO.read(imageTemplate);
  }
}
