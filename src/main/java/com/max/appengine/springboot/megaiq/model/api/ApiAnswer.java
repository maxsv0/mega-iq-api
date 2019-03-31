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

package com.max.appengine.springboot.megaiq.model.api;

import com.max.appengine.springboot.megaiq.model.AbstractAnswer;

public class ApiAnswer {
  private Integer id;
  private String pic;
  private String pic2x;

  public ApiAnswer() {
    super();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((pic == null) ? 0 : pic.hashCode());
    result = prime * result + ((pic2x == null) ? 0 : pic2x.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApiAnswer other = (ApiAnswer) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (pic == null) {
      if (other.pic != null)
        return false;
    } else if (!pic.equals(other.pic))
      return false;
    if (pic2x == null) {
      if (other.pic2x != null)
        return false;
    } else if (!pic2x.equals(other.pic2x))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ApiAnswer [id=" + id + ", pic=" + pic + ", pic2x=" + pic2x + "]";
  }

  public ApiAnswer(AbstractAnswer answer) {
    super();

    this.setId(answer.getId());
    this.setPic(answer.getPic());
    this.setPic2x(answer.getPic2x());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPic() {
    return pic;
  }

  public void setPic(String pic) {
    this.pic = pic;
  }

  public String getPic2x() {
    return pic2x;
  }

  public void setPic2x(String pic2x) {
    this.pic2x = pic2x;
  }

}
