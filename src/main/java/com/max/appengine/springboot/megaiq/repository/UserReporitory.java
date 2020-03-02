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

package com.max.appengine.springboot.megaiq.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.max.appengine.springboot.megaiq.model.User;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

public interface UserReporitory extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUid(String uid);
  
  List<User> findByLocaleAndUpdateDateAfterAndIsPublicIsTrueAndIqIsNotNullOrderByIqDesc(
      Locale locale, Date createDate, Pageable pageable);
  
  List<User> findByIdIn(Collection<Integer> userIds);
  
  Optional<User> findOneByIqGreaterThanAndLocaleAndIsPublicIsTrueOrderByUpdateDate(Integer iq, Locale locale);
  
  List<User> findTop5ByIqGreaterThanAndLocaleAndIsPublicIsTrueOrderByUpdateDate(Integer iq, Locale locale);
  
  @Query("SELECT coalesce(max(id), 0) FROM User")
  Integer getMaxId();

}
