package com.meaf.apeps.view.components;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

import java.util.List;

public class GoogleMapPreset extends GoogleMap {
  private GoogleMapPreset(String apiKey, String clientId, String language) {
    super(apiKey, clientId, language);
  }

  public GoogleMapPreset(String apiKey, String clientId, String language, List<GoogleMapMarker> markers) {
    this(apiKey, clientId, language);
    if (markers != null) {
      markers.forEach(m -> {
        addMarker(m);
        setCenter(m.getPosition());
      });
    }

    setSizeFull();
    setMinZoom(3);
    setMaxZoom(10);
  }
}
