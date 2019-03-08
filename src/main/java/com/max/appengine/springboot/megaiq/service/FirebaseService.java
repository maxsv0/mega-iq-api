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
import java.io.InputStream;
import org.springframework.stereotype.Service;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.max.appengine.springboot.megaiq.model.User;

@Service
public class FirebaseService {
  
  private static final InputStream serviceAccountJson =
      FirebaseService.class.getClassLoader().getResourceAsStream("firebase.json");
  
  private static final FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
      .setCredentials(getCertCredential(serviceAccountJson))
      .setDatabaseUrl("https://megaiq637.firebaseio.com/").build();
  
  private final FirebaseApp firebaseApp;
  private FirebaseAuth auth;

  public FirebaseService() throws IOException {
    if(FirebaseApp.getApps().isEmpty()) {
      firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
    } else {
      firebaseApp = FirebaseApp.getInstance();
    }
    
    auth = FirebaseAuth.getInstance(firebaseApp);
  }
  
  public String getPasswordResetLink(String email) throws FirebaseAuthException {
    return auth.generatePasswordResetLink(email);
  }

  public String getEmailVerificationLink(String email) throws FirebaseAuthException {
    return auth.generateEmailVerificationLink(email);
  }

  public String generateToken(Integer userId) throws FirebaseAuthException {
    return auth.createCustomToken(userId.toString());
  }

  public FirebaseToken checkToken(String token) throws FirebaseAuthException {
    return auth.verifyIdToken(token);
  }

  public Integer getUserIdByToken(String token) throws FirebaseAuthException {
    FirebaseToken firebaseToken = checkToken(token);
    return Integer.valueOf(firebaseToken.getUid());
  }
  
  public UserRecord saveUser(User user) throws FirebaseAuthException {
    UpdateRequest request = new UpdateRequest(user.getUid());
    request.setEmail(user.getEmail());
    request.setEmailVerified(user.getIsEmailVerified());
    request.setDisplayName(user.getName());
    request.setPhotoUrl(user.getPic());
    return auth.updateUser(request);
  }

  public UserRecord createUser(User user) throws FirebaseAuthException {
    CreateRequest request = new CreateRequest();
    request.setEmail(user.getEmail());
    request.setEmailVerified(user.getIsEmailVerified());
    request.setDisplayName(user.getName());
    request.setPassword(user.getPassword());
    request.setPhotoUrl(user.getPic());
    return auth.createUser(request);
  }

  public void deleteUser(User user) throws FirebaseAuthException {
    auth.deleteUser(user.getUid());
  }

  public UserRecord getUserRecord(User user) throws FirebaseAuthException {
    return auth.getUserByEmail(user.getEmail());
  }
  
  public static GoogleCredentials getCertCredential(InputStream stream) {
    try {
      return GoogleCredentials.fromStream(stream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
