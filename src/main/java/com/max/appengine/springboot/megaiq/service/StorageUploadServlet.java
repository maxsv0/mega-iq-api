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
import com.google.appengine.api.blobstore.UploadOptions;

@MultipartConfig
public class StorageUploadServlet extends HttpServlet {
  public static final String GCS_BUCKET_NAME = "msvhost.appspot.com/mega-iq/user-pic";
  
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
    UploadOptions uploadOptions = UploadOptions.Builder
        .withGoogleStorageBucketName(GCS_BUCKET_NAME);
    
    String uploadUrl = blobstoreService.createUploadUrl(url, uploadOptions);
    
    return uploadUrl.substring(26);
  }

}
