package com.meaf.apeps.view.content;

import com.meaf.apeps.view.beans.StateBean;
import com.meaf.apeps.view.content.pages.LoginContent;
import com.meaf.apeps.view.content.pages.ModelContent;
import com.meaf.apeps.view.events.StateChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ViewRouter implements ApplicationListener<StateChangeEvent> {

  @Autowired
  private StateBean stateBean;
  @Autowired
  private ModelContent modelContent;
  @Autowired
  private LoginContent loginContent;
  @Autowired
  private ContentWrapper wrapper;
  private BaseContentHolder holder;


  public void route() {
    holder.fill(getContent());
  }

  private com.vaadin.ui.Component getContent() {
    switch (stateBean.getState()) {
      case Model:
        return modelContent.getContent();
      case Login:
        return loginContent.getContent();
      default:
        throw new IllegalStateException("illegal state");
    }
  }

  @Override
  public void onApplicationEvent(StateChangeEvent stateChangeEvent) {
    this.stateBean.setState(stateChangeEvent.getState());
    route();
  }

  public void setHolder(BaseContentHolder holder) {
    this.holder = holder;
  }
}
