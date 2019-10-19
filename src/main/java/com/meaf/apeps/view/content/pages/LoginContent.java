package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.view.beans.SessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.layouts.MHorizontalLayout;

@Component
public
class LoginContent extends ABaseContent{
  @Autowired
  private SessionBean sessionBean;

  @Override
  public com.vaadin.ui.Component getContent() {






    return new MHorizontalLayout();

  }


}
