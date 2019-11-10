package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.User;
import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class SessionBean {
  private UserBean userBean;

  public SessionBean(UserBean userBean) {
    this.userBean = userBean;
  }

  private User user;

  public boolean authenticate(LoginForm.LoginEvent e) {
    User user = userBean.findUser(e.getLoginParameter("username"), e.getLoginParameter("password"));
    if(user != null) {
      this.user = user;
      e.getSource().getUI().getPage().reload();
    }

    return false;
  }

  public boolean isUserLoggedIn() {
    return user != null;
  }

  public User getLoggedInUser() {
    return user;
  }

  public void logout(Button.ClickEvent e) {
    user = null;
    e.getButton().getUI().getPage().reload();
  }
}
