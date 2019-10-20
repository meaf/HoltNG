package com.meaf.apeps.view.beans;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class StateBean {

  public enum EState{
    Login,
    Undefined,
    Project,
    Model,
  }

  private EState state = EState.Model;

  public EState getState() {
    return state;
  }

  public void setState(EState state) {
    this.state = state;
  }
}
