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
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

@Service
public class StorageService  {
  public static final String URL_PARAMETER = "key";
  
  public static final String GCS_BUCKET_NAME = "msvhost.appspot.com/mega-iq/user-pic";

  public static final String FORM_PARAMETER = "uploadFile";
  
  private ImagesService imagesService = ImagesServiceFactory.getImagesService();
  
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  
  public String uploadFile(HttpServletRequest request) throws ServletException, IOException {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(FORM_PARAMETER);

    return blobKeys.get(0).getKeyString();
  }

  public String createUploadUrl() {
    UploadOptions uploadOptions =
        UploadOptions.Builder
        //.withMaxUploadSizeBytes(maxUploadSizeBytes) TODO: set it
        .withGoogleStorageBucketName(GCS_BUCKET_NAME);

    String uploadUrl = blobstoreService.createUploadUrl("/storage/upload", uploadOptions);

    // remove: https://msvhost.appspot.com         
    // TODO: use URL class
    return uploadUrl.substring(26);
  }
  
  public String serveFile(String key) {
    BlobKey blobKey = new BlobKey(key);
    
    ServingUrlOptions options = ServingUrlOptions.Builder
        .withBlobKey(blobKey)
        .secureUrl(true);
    
    return imagesService.getServingUrl(options);
  }
  
  public String serveFileByPath(String path) {
    ServingUrlOptions options = ServingUrlOptions.Builder
        .withGoogleStorageFileName(path)
        .secureUrl(true);
    
    return imagesService.getServingUrl(options);
  }
}
