package com.meaf.apeps.view;

import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.content.BaseContentHolder;
import com.meaf.apeps.view.content.ViewRouter;
import com.meaf.apeps.view.content.pages.ModelContent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;


@Title("HoltNG")
@Theme("valo")
@SpringUI
public class MainUI extends UI {

  private static final long serialVersionUID = 1L;

  @Autowired
  private ModelContent modelContent;
  @Autowired
  private ViewRouter router;
  @Autowired
  private SessionBean sessionBean;

  @Override
  protected void init(VaadinRequest request) {
    BaseContentHolder baseHolder = new BaseContentHolder();
    setContent(baseHolder);
    router.setHolder(baseHolder);
    router.route();
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
