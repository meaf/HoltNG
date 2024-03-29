package com.meaf.apeps.view.events.state;

import com.meaf.apeps.view.beans.StateBean;
import org.springframework.context.ApplicationEvent;

public class StateChangeEvent extends ApplicationEvent {
  private StateBean.EState state;

  StateChangeEvent(Object source, StateBean.EState state) {
    super(source);
    this.state = state;
  }

  public StateBean.EState getState() {
    return state;
  }
}
