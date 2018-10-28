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

package com.max.appengine.springboot.megaiq.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.max.appengine.springboot.megaiq.model.enums.Locale;

@Entity
@Table(name = "answer")
public class Answer extends AbstractAnswer {

  public Answer() {
    super();
  }

  public Answer(Integer id, String pic, Integer questionId, Date createDate, Date updateDate, Locale locale) {
		super();
		
		this.setId(id);
		this.setCreateDate(createDate);
		this.setLocale(locale);
		this.setPic(pic);
		this.setQuestionId(questionId);
		this.setUpdateDate(updateDate);
	}

	public Answer(AbstractAnswer answer) {
		super();

		this.setId(answer.getId());
		this.setCreateDate(answer.getCreateDate());
		this.setLocale(answer.getLocale());
		this.setPic(answer.getPic());
		this.setQuestionId(answer.getQuestionId());
		this.setUpdateDate(answer.getUpdateDate());
	}

}
