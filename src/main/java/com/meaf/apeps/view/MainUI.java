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

  private final ViewRouter router;

  public MainUI(ViewRouter router) {
    this.router = router;
  }

  @Override
  protected void init(VaadinRequest request) {
    BaseContentHolder baseHolder = new BaseContentHolder();
    setContent(baseHolder);
    router.setHolder(baseHolder);
    router.route();
  }

}
