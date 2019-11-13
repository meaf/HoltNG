package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.User;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MWindow;

import java.util.function.Consumer;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;

public class UserRegistrationPopup {
  public UserRegistrationPopup(Button.ClickEvent e, Consumer<User> addUser) {
    MWindow window = new MWindow("Registration")
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(300, Sizeable.Unit.PIXELS)
        .withWidth(400, Sizeable.Unit.PIXELS);


    FormLayout layout = new FormLayout();
    layout.setSpacing(true);
    layout.setMargin(true);

    MTextField tfName = new MTextField("Username").withFullWidth();
    tfName.setRequiredIndicatorVisible(true);

    MTextField tfEmail = new MTextField("Email").withFullWidth();
    tfName.setRequiredIndicatorVisible(true);

    PasswordField pass = new PasswordField("Password");
    pass.setWidth(100, Sizeable.Unit.PERCENTAGE);

    PasswordField passConfirm = new PasswordField("Password confirmation");
    passConfirm.setWidth(100, Sizeable.Unit.PERCENTAGE);

    Button btnSubmit = new Button("Create");
    btnSubmit.setClickShortcut(ENTER);
    btnSubmit.addClickListener(ev -> {
      if (tfName.getValue().isEmpty() || tfEmail.isEmpty() || pass.getValue().isEmpty() || passConfirm.getValue().isEmpty()) {
        EToast.ERROR.show("Error", "Fill all the fields");
        return;
      }
      if (!pass.getValue().equals(passConfirm.getValue())) {
        EToast.ERROR.show("Error", "Passwords should match!");
        return;
      }
      User user = new User();
      user.setName(tfName.getValue());
      user.setPassword(pass.getValue());
      user.setEmail(tfEmail.getValue());
      addUser.accept(user);
      window.close();
    });

    layout.addComponents(tfName, tfEmail, pass, passConfirm, btnSubmit);
    window.setContent(layout);

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);
  }
}
