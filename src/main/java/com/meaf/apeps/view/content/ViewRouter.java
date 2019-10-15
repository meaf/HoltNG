package com.meaf.apeps.view.content;

import com.meaf.apeps.view.beans.StateBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewRouter {

  @Autowired
  private StateBean stateBean;
  @Autowired
  private ModelContent modelContent;
  @Autowired
  private ContentWrapper wrapper;


  public com.vaadin.ui.Component route() {
    switch (stateBean.getState()) {
      case Model:
        return wrapper.wrap(modelContent.getModelContent());
      default:
        throw new IllegalStateException("illegal state");
    }
  }

}
