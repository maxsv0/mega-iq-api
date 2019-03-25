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

package com.max.appengine.springboot.megaiq.model.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MegaIQException extends Throwable {
  private static final Logger log = Logger.getLogger(MegaIQException.class.getName());

  public MegaIQException(Level level, String msg, Throwable cause) throws RuntimeException {
    String message = "Error level: " + level + ". " + msg;
    log.log(Level.SEVERE, message, cause);
    throw new RuntimeException(message,  cause);
  }
  
  public MegaIQException(Level level, String msg) throws RuntimeException {
    String message = "Error level: " + level + ". " + msg;
    log.log(Level.SEVERE, message);
    throw new RuntimeException(message);
  }
}
