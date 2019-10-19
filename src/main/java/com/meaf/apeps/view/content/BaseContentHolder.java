package com.meaf.apeps.view.content;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class BaseContentHolder extends VerticalLayout {
  private Component component;

  public void setComponent(Component component){
    this.component = component;
  }


}
