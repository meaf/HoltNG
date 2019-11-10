package com.meaf.apeps.view.beans;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class StateBean {

  private EState state = EState.Project;

  public EState getState() {
    return state;
  }

  public void setState(EState state) {
    this.state = state;
  }

  public enum EState {
    Undefined,
    Project,
    Model,
  }
}
