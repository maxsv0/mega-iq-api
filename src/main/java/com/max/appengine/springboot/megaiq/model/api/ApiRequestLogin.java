package com.max.appengine.springboot.megaiq.model.api;

public class ApiRequestLogin {
  private String login;
  
  private String password;

  public ApiRequestLogin() {
    super();
  }
  
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
