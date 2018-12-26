package com.max.appengine.springboot.megaiq.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@MultipartConfig
public class StorageUploadServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
    response.setContentType("text/plain");
    response.getWriter().write("{\"ok\":true,\"msg\":\"" + this.createUploadUrl("/storage/upload") +  "\",\"date\":\"2018-12-25T09:53:16.716+0000\"}");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("uploadFile");

    String url = "/api/v1/storage/serve?key=" + blobKeys.get(0).getKeyString();

    response.setContentType("text/plain");
    response.getWriter().write("{\"ok\":true,\"msg\":\"" + url +  "\",\"date\":\"2018-12-25T09:53:16.716+0000\"}");
  }

  public String uploadFile(HttpServletRequest request) throws ServletException, IOException {

    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("uploadFile");

    return blobKeys.get(0).getKeyString();
  }

  public String createUploadUrl(String url) {
    String uploadUrl = blobstoreService.createUploadUrl(url);
    
    return uploadUrl.substring(26);
  }

}
