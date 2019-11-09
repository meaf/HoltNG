package com.meaf.apeps.view.content;

import com.meaf.apeps.view.beans.StateBean;
import com.meaf.apeps.view.content.pages.ModelContent;
import com.meaf.apeps.view.content.pages.ProjectContent;
import com.meaf.apeps.view.events.StateChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ViewRouter implements ApplicationListener<StateChangeEvent> {

  private final StateBean stateBean;
  private final ModelContent modelContent;
  private final ProjectContent projectContent;

  private BaseContentHolder holder;

  public ViewRouter(StateBean stateBean, ModelContent modelContent, ProjectContent projectContent) {
    this.stateBean = stateBean;
    this.modelContent = modelContent;
    this.projectContent = projectContent;
  }

  public void route() {
    holder.fill(getContent());
  }

  private com.vaadin.ui.Component getContent() {
    switch (stateBean.getState()) {
      case Model:
        return modelContent.getContent();
      case Project:
        return projectContent.getContent();
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
