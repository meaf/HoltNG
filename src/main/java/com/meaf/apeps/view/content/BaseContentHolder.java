package com.meaf.apeps.view.content;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class BaseContentHolder extends VerticalLayout {
  private Component component;

  public BaseContentHolder() {
    setSpacing(false);
    setMargin(false);
  }

  public void fill(Component content) {
    removeAllComponents();
    addComponent(content);
  }
}
