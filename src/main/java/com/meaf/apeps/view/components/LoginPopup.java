package com.meaf.apeps.view.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Window;
import org.vaadin.viritin.layouts.MWindow;

public class LoginPopup extends MWindow {
  public LoginPopup(Button.ClickEvent e, LoginForm.LoginListener loginListener) {
    MWindow window = new MWindow("Enter your credentials")
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(300, Unit.PIXELS)
        .withWidth(280, Unit.PIXELS);

    LoginForm loginForm = new LoginForm();
    loginForm.addLoginListener(loginListener);
    window.setContent(loginForm);
    window.center();

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);
  }
}
