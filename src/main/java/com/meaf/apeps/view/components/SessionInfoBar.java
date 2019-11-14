package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.view.beans.SessionBean;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MHorizontalLayout;


public class SessionInfoBar extends MHorizontalLayout {


  private final SessionBean sessionBean;

  public SessionInfoBar(SessionBean sessionBean, Component... additionalComponents) {
    super();
    this.sessionBean = sessionBean;
    additionalComponentsFill(additionalComponents);
    sessionalComponentsFill(sessionBean);
  }

  private void additionalComponentsFill(Component... additionalComponents) {
    add(additionalComponents);
    setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
    expand(additionalComponents);
  }

  private void sessionalComponentsFill(SessionBean sessionBean) {
    Label lblUserInfo = new Label(getSessionCaption());
    com.vaadin.ui.Button sessionButton = sessionBean.isUserLoggedIn()
        ? createLogoutBtn()
        : createLoginBtn();

    MHorizontalLayout sessionalContent = new MHorizontalLayout(lblUserInfo, sessionButton);
    sessionalContent.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
    sessionalContent.setComponentAlignment(lblUserInfo, Alignment.MIDDLE_RIGHT);
    add(sessionalContent);
    setComponentAlignment(sessionalContent, Alignment.TOP_RIGHT);
    setWidth(100, Unit.PERCENTAGE);

  }


  private String getSessionCaption() {
    User loggedInUser = sessionBean.getLoggedInUser();
    StringBuilder sb = new StringBuilder();
    if (loggedInUser == null) {
      sb.append("Guest");
    } else {
      sb.append(loggedInUser.getName());
      sb.append(" | ");
      sb.append(loggedInUser.getAdmin() ? "Admin" : "User");
    }
    return sb.toString();
  }

  private com.vaadin.ui.Button createLoginBtn() {
    com.vaadin.ui.Button btnLogin = new com.vaadin.ui.Button("Login");
    btnLogin.addClickListener(e -> new LoginPopup(e, sessionBean::authenticate, u -> register(e, u)));
    btnLogin.setStyleName("primary");
    return btnLogin;
  }

  private void register(com.vaadin.ui.Button.ClickEvent e, User user) {
    if (sessionBean.checkUsernameAvailable(user.getName())) {
      sessionBean.register(user);
      new LoginPopup(e, sessionBean::authenticate, u -> register(e, u));
    } else EToast.ERROR.show("Error", "this username is already taken");
  }

  private com.vaadin.ui.Button createLogoutBtn() {
    com.vaadin.ui.Button btnLogout = new com.vaadin.ui.Button("Logout");
    btnLogout.addClickListener(sessionBean::logout);
    btnLogout.setStyleName("danger");
    return btnLogout;
  }


}
