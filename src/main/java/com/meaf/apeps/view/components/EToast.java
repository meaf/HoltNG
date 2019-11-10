package com.meaf.apeps.view.components;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;

public enum EToast {

  ERROR("error"),
  SUCCESS("");

  private final String style;

  EToast(String s) {
    this.style = s;
  }

  public void show(String title, String descr) {
    com.vaadin.ui.Notification notification = new com.vaadin.ui.Notification(title, descr);
    notification.setPosition(Position.TOP_RIGHT);
    notification.setStyleName(style);
    notification.show(Page.getCurrent());
  }
}
