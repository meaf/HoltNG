package com.meaf.apeps.view.events;

import com.meaf.apeps.view.beans.StateBean;
import org.springframework.context.ApplicationEvent;

public class StateChangeEvent extends ApplicationEvent {
  private StateBean.EState state;

  public StateChangeEvent(Object source, StateBean.EState state) {
    super(source);
    this.state = state;
  }

  public StateBean.EState getState() {
    return state;
  }
}
