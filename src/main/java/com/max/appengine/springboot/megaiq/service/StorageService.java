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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
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
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@Service
public class StorageService {
  private static final int BUFFER_SIZE = 1024 * 512;

  public static final String GCS_BUCKET = "mega-iq-build";

  public static final String GCS_FOLDER_USER_CERTIFICATE = "user-certificate";

  public static final String GCS_FOLDER_USER_UPLOAD = "user-pic";

  public static final String URL_PARAMETER = "key";

  public static final String FORM_PARAMETER = "uploadFile";

  private ImagesService imagesService = ImagesServiceFactory.getImagesService();

  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
      .initialRetryDelayMillis(10).retryMaxAttempts(10).totalRetryPeriodMillis(15000).build());

  public String uploadFile(HttpServletRequest request) throws ServletException, IOException {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(FORM_PARAMETER);

    return blobKeys.get(0).getKeyString();
  }

  public String createUploadUrl() {
    UploadOptions uploadOptions = UploadOptions.Builder
        // .withMaxUploadSizeBytes(maxUploadSizeBytes) TODO: set it
        .withGoogleStorageBucketName(GCS_BUCKET + "/" + GCS_FOLDER_USER_UPLOAD);

    return blobstoreService.createUploadUrl("/storage/upload", uploadOptions);
  }

  public void fetchFile(String fileName, File file) throws IOException {
    BlobKey blobKey = blobstoreService.createGsBlobKey("/gs/" + GCS_BUCKET + "/" + fileName);

    // TODO: rewrite this procedure properly
    // check blobkey
    // know file size
    // don't throw
    // avoid void

    // if (blobKey != null) {
    //
    // }

    FileOutputStream stream = new FileOutputStream(file);

    long blockSize = BUFFER_SIZE;
    long inxStart = 0;
    long inxEnd = blockSize;
    boolean flag = false;

    do {
      try {
        byte[] b = blobstoreService.fetchData(blobKey, inxStart, inxEnd);

        stream.write(b);

        if (b.length < blockSize)
          flag = true;

        inxStart = inxEnd + 1;
        inxEnd += blockSize + 1;

      } catch (Exception e) {
        flag = true;
      }

    } while (!flag);

    stream.close();
  }

  public String serveFile(String key) {
    BlobKey blobKey = new BlobKey(key);

    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey).secureUrl(true);

    return imagesService.getServingUrl(options);
  }

  public String serveFileByPath(String path) {
    ServingUrlOptions options = ServingUrlOptions.Builder
        .withGoogleStorageFileName("/gs/" + GCS_BUCKET + "/" + path).secureUrl(true);

    return imagesService.getServingUrl(options);
  }

  public String uploadCertificateToStorage(InputStream inputCertificate) throws IOException {
    String filePath =  GCS_FOLDER_USER_CERTIFICATE + "/" + "";
    
    GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
    GcsFilename fileName = new GcsFilename(GCS_BUCKET, filePath);
    GcsOutputChannel outputChannel;
    outputChannel = gcsService.createOrReplace(fileName, instance);
    copy(inputCertificate, Channels.newOutputStream(outputChannel));
    
    return filePath;
  }

  private void copy(InputStream input, OutputStream output) throws IOException {
    try {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead = input.read(buffer);
      while (bytesRead != -1) {
        output.write(buffer, 0, bytesRead);
        bytesRead = input.read(buffer);
      }
    } finally {
      input.close();
      output.close();
    }
  }
}
