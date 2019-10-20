package com.meaf.apeps.view.content;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class BaseContentHolder extends VerticalLayout {
  public BaseContentHolder() {
    setSpacing(false);
    setMargin(false);
  }

  void fill(Component content) {
    removeAllComponents();
    addComponent(content);
  }
}
