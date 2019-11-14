package com.meaf.apeps.view.components;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.util.function.Consumer;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;
import static com.vaadin.event.ShortcutAction.KeyCode.ESCAPE;

public class ConfirmationPopup {
  public ConfirmationPopup(Button.ClickEvent e, String title, Consumer<Void> c) {
    MWindow window = new MWindow()
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(100, Sizeable.Unit.PIXELS)
        .withWidth(300, Sizeable.Unit.PIXELS);

    Button btnOk = createOkButton(c, window);
    Button btnCancel = creteCancelButton(c, window);
    MHorizontalLayout horizontalLayout = new MHorizontalLayout(btnOk, btnCancel);
    horizontalLayout.setExpandRatio(btnOk, 1);
    horizontalLayout.setExpandRatio(btnCancel, 1);
    horizontalLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
    Label label = new Label(title);
    MVerticalLayout verticalLayout = new MVerticalLayout(label, horizontalLayout);
    verticalLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

    window.setContent(verticalLayout);

    e.getButton().getUI().addWindow(window);
  }

  private Button createOkButton(Consumer<Void> c, MWindow window) {
    Button btnOk = new Button("Ok");
    btnOk.setClickShortcut(ENTER);
    btnOk.setStyleName("primary");
    btnOk.setWidth(100, Sizeable.Unit.PERCENTAGE);
    btnOk.addClickListener(ev -> {
      c.accept(null);
      window.close();
    });
    return btnOk;
  }

  private Button creteCancelButton(Consumer<Void> c, MWindow window) {
    Button btnCancel = new Button("Cancel");
    btnCancel.setClickShortcut(ESCAPE);
//    btnCancel.setStyleName("danger");
    btnCancel.setWidth(100, Sizeable.Unit.PERCENTAGE);
    btnCancel.addClickListener(ev -> {
      window.close();
    });
    return btnCancel;
  }
}
