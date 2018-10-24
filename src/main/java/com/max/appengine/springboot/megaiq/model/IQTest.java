/*
 * Copyright 2018 mega-iq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.max.appengine.springboot.megaiq.model;

import java.util.Date;
import java.util.UUID;

import com.max.appengine.springboot.megaiq.enums.IqTestStatus;
import com.max.appengine.springboot.megaiq.enums.IqTestType;
import com.max.appengine.springboot.megaiq.enums.Locale;

public class IQTest {
	private Integer id;
	private UUID code;
	private String url;
	private User user;
	private IqTestType type;
	private Locale locale;
	private IqTestStatus status;
	private Date finishedAt;
	private Integer points;
	private IQGroups groupsGraph;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UUID getCode() {
		return code;
	}

	public void setCode(UUID code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public IqTestType getType() {
		return type;
	}

	public void setType(IqTestType type) {
		this.type = type;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public IqTestStatus getStatus() {
		return status;
	}

	public void setStatus(IqTestStatus status) {
		this.status = status;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public IQGroups getGroupsGraph() {
		return groupsGraph;
	}

	public void setGroupsGraph(IQGroups groupsGraph) {
		this.groupsGraph = groupsGraph;
	}

}
