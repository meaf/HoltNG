package com.meaf.apeps.view.events;

import com.meaf.apeps.view.beans.StateBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class StateChangePublisher {
  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  public void publishStateChange(final StateBean.EState state) {
    StateChangeEvent customSpringEvent = new StateChangeEvent(this, state);
    applicationEventPublisher.publishEvent(customSpringEvent);
  }
}