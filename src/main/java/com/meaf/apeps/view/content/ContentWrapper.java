package com.meaf.apeps.view.content;

import org.springframework.stereotype.Component;
import org.vaadin.viritin.layouts.MVerticalLayout;

@Component
public class ContentWrapper {
  com.vaadin.ui.Component wrap(MVerticalLayout modelContent) {
    return modelContent;
  }
}
