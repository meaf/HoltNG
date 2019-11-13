package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.view.components.EToast;
import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class SessionBean {
  private UserBean userBean;
  private User user;

  public SessionBean(UserBean userBean) {
    this.userBean = userBean;
  }

  public boolean authenticate(LoginForm.LoginEvent e) {
    User user = userBean.findUser(e.getLoginParameter("username").toLowerCase(), e.getLoginParameter("password"));
    if (user != null) {
      this.user = user;
      e.getSource().getUI().getPage().reload();
      return true;
    }
    EToast.ERROR.show("Error", "Incorrect username or password8");
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

  public void register(User user) {
    userBean.createUser(user);
  }

  public boolean checkUsernameAvailable(String username) {
    return userBean.checkUsername(username);
  }
}
