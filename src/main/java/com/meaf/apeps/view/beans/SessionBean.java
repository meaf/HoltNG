package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.User;
import com.vaadin.ui.LoginForm;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class SessionBean {

  private User user;

  public boolean authenticate(LoginForm.LoginEvent e) {
    return false;
  }

  public boolean isUserLoggedIn() {
    return user != null;
  }

  public User getUser() {
    return user;
  }
}
