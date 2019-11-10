package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.view.beans.StateBean;
import com.meaf.apeps.view.events.state.StateChangePublisher;
import com.vaadin.ui.Component;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ABaseContent {
  @Autowired
  private StateChangePublisher eventPublisher;

  abstract Component getContent();

  protected void changeState(StateBean.EState state) {
    eventPublisher.publishStateChange(state);
  }

}
