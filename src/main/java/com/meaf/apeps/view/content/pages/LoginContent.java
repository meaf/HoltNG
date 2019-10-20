package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.beans.StateBean;
import com.vaadin.ui.Button;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.layouts.MHorizontalLayout;

@Component
public
class LoginContent extends ABaseContent {
  private final SessionBean sessionBean;

  public LoginContent(SessionBean sessionBean) {
    this.sessionBean = sessionBean;
  }

  @Override
  public com.vaadin.ui.Component getContent() {
    return new MHorizontalLayout(createCalculateButton());
  }

  private Button createCalculateButton() {
    return new Button("Calculate", e -> changeState(StateBean.EState.Model));
  }


}
