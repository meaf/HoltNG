package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.entity.Model;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MWindow;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ModelCreationPopup {
  public ModelCreationPopup(Button.ClickEvent e, List<Location> locations, Consumer<Model> addModel) {
    MWindow window = new MWindow("Create new model")
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(350, Sizeable.Unit.PIXELS)
        .withWidth(500, Sizeable.Unit.PIXELS);


    FormLayout layout = new FormLayout();
    layout.setSpacing(true);
    layout.setMargin(true);

    MTextField tfName = new MTextField("Model name").withFullWidth();
    tfName.setRequiredIndicatorVisible(true);

    TextArea tfDescr = new TextArea("Model description");
    tfDescr.setWidth(100, Sizeable.Unit.PERCENTAGE);

    ComboBox<Location> cbLocations = new ComboBox<>("Location");
    cbLocations.setItemCaptionGenerator(i -> String.format("%s (lat:%s, lon:%s)", i.getName(), i.getLatitude(), i.getLongitude()));
    cbLocations.setEmptySelectionAllowed(false);
    cbLocations.setTextInputAllowed(false);
    cbLocations.setDataProvider(new ListDataProvider<>(locations));
    cbLocations.setRequiredIndicatorVisible(true);
    cbLocations.setWidth(100, Sizeable.Unit.PERCENTAGE);

    Button btnSubmit = new Button("Create");
    btnSubmit.addClickListener(ev -> {
      if (tfDescr.getValue().isEmpty() || tfDescr.getValue().isEmpty() || cbLocations.getValue() == null) {
        EToast.ERROR.show("Error", "Fill all the fields");
        return;
      }
      Model model = new Model();
      model.setName(tfName.getValue());
      model.setDescription(tfDescr.getValue());
      model.setLocation(cbLocations.getSelectedItem().orElse(null));
      addModel.accept(model);
      window.close();
    });

    layout.addComponents(tfName, tfDescr, cbLocations, btnSubmit);
    window.setContent(layout);

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);
  }
}
