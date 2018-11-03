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

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.UserToken;
import com.max.appengine.springboot.megaiq.model.enums.UserTokenType;
import com.max.appengine.springboot.megaiq.repository.UserReporitory;
import com.max.appengine.springboot.megaiq.repository.UserTokenReporitory;

@Service
public class UserService {
  private final UserReporitory userReporitory;
  private final UserTokenReporitory userTokenReporitory;
  
  @Autowired
  public UserService(UserReporitory userReporitory, UserTokenReporitory userTokenReporitory) {
    this.userReporitory = userReporitory;
    this.userTokenReporitory = userTokenReporitory;
  }

  public Optional<User> getUserById(Integer userId) {
    return userReporitory.findById(userId);
  }
  
  public Optional<User> getUserByToken(String token, UserTokenType tokenType) {
    // TODO: rework to join tables and avoid second query
    
    Optional<UserToken> userToken = userTokenReporitory.findByValueAndType(token, tokenType);
    if (!userToken.isPresent()) return Optional.empty(); 
    
    return userReporitory.findById(userToken.get().getUserId());
  }

}
