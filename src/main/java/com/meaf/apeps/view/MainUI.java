package com.meaf.apeps.view;

import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.content.ModelContent;
import com.meaf.apeps.view.content.ViewRouter;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;


@Title("HoltNG")
@Theme("valo")
@SpringUI
public class MainUI extends UI {

  private static final long serialVersionUID = 1L;

  @Autowired
  ModelContent modelContent;
  @Autowired
  ViewRouter router;
  @Autowired
  private SessionBean sessionBean;

  @Override
  protected void init(VaadinRequest request) {
    showLoginPrompt();
    setContent(router.route());
  }

  private void showLoginPrompt() {
    if (!sessionBean.isUserLoggedIn()) {
      LoginForm component = new LoginForm();
      component.addLoginListener(e -> {
        boolean isAuthenticated = sessionBean.authenticate(e);
        if (!isAuthenticated) {
          component.attach();
//                component.set`(true);
        }
      });
    }
  }

}
