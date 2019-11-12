package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.User;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.util.function.Consumer;

public class LoginPopup extends MWindow {
  public LoginPopup(Button.ClickEvent e, LoginForm.LoginListener loginListener, Consumer<User> addUserEvent) {
    MWindow window = new MWindow("Enter your credentials")
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(380, Unit.PIXELS)
        .withWidth(280, Unit.PIXELS);

    LoginForm loginForm = new LoginForm();
    loginForm.addLoginListener(loginListener);
    Button btnRegister = new Button();
    btnRegister.setStyleName(ValoTheme.BUTTON_LINK);
    btnRegister.setCaption("Register new user");
    btnRegister.addClickListener(regEv -> new UserRegistrationPopup(e, addUserEvent));
    MVerticalLayout layout = new MVerticalLayout(loginForm, btnRegister);
    layout.setComponentAlignment(btnRegister, Alignment.MIDDLE_CENTER);
    window.setContent(layout);
    window.center();

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);
  }
}
