package com.meaf.apeps.view.components;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Window;
import org.vaadin.viritin.layouts.MWindow;

public class LoginPopup extends MWindow {
  public LoginPopup(Button.ClickEvent e, LoginForm.LoginListener loginListener) {
    MWindow window = new MWindow("Enter your credentials")
        .withCenter()
        .withClosable(true)
        .withHeight(20, Sizeable.Unit.PERCENTAGE)
        .withWidth(30, Sizeable.Unit.PERCENTAGE);

    LoginForm loginForm = new LoginForm();

    loginForm.addLoginListener(loginListener);

    window.setContent(loginForm);

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);
  }
}
