package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.entity.LocationKey;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import org.vaadin.viritin.layouts.MWindow;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Consumer;

public class LocationCreationPopup {
  public LocationCreationPopup(Button.ClickEvent e, Consumer<Location> addLocation) {

    MWindow window = new MWindow("Create new location")
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(345, Sizeable.Unit.PIXELS)
        .withWidth(375, Sizeable.Unit.PIXELS);


    FormLayout layout = new FormLayout();
    layout.setSpacing(true);
    layout.setMargin(true);

    TextField tfName = new TextField("Location name");
    tfName.setRequiredIndicatorVisible(true);
    tfName.setWidth(100, Sizeable.Unit.PERCENTAGE);

    TextField tfLon = new TextField("Longitude");
    tfLon.setRequiredIndicatorVisible(true);
    tfLon.setWidth(100, Sizeable.Unit.PERCENTAGE);

    TextField tfLat = new TextField("Latitude");
    tfLat.setRequiredIndicatorVisible(true);
    tfLat.setWidth(100, Sizeable.Unit.PERCENTAGE);

    TextField tfSiteId = new TextField("Solcast location id");
    tfSiteId.setWidth(100, Sizeable.Unit.PERCENTAGE);
    TextField tfKey = new TextField("Solcast API key");
    tfKey.setWidth(100, Sizeable.Unit.PERCENTAGE);

    Button btnSubmit = new Button("Create");
    btnSubmit.addClickListener(ev -> {
      if (tfName.getValue().isEmpty() || tfLon.getValue().isEmpty() || tfLat.getValue() == null) {
        EToast.ERROR.show("Error", "Fill all the fields");
        return;
      }
      Location location = new Location();
      location.setName(tfName.getValue());
      try {
        location.setLatitude(BigDecimal.valueOf(Double.parseDouble(tfLat.getValue())));
        location.setLongitude(BigDecimal.valueOf(Double.parseDouble(tfLon.getValue())));
      } catch (NumberFormatException ex) {
        EToast.ERROR.show("Error", "Illegal location position");
        return;
      }
      if(!tfSiteId.getValue().isEmpty() && !tfKey.getValue().isEmpty()) {
        LocationKey key = new LocationKey();
        key.setSiteId(tfSiteId.getValue());
        key.setApiKey(tfKey.getValue());
        location.setKeys(Arrays.asList(key));
      }

      addLocation.accept(location);
      window.close();
    });

    layout.addComponents(tfName, tfLat, tfLon, tfSiteId, tfKey, btnSubmit);
    window.setContent(layout);

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);


  }
}
