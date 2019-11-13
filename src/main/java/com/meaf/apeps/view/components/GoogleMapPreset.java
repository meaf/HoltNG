package com.meaf.apeps.view.components;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
    setMaxZoom(13);
  }

  @Override
  public void addMarker(GoogleMapMarker marker) {
    addAccuracyRing(marker);
    super.addMarker(marker);
  }

  private void addAccuracyRing(GoogleMapMarker marker) {
    GoogleMapPolygon polygon = new GoogleMapPolygon();
    polygon.setCoordinates(createCircle(marker.getPosition()));
    polygon.setFillOpacity(0.3);
    polygon.setFillColor("red");
    polygon.setStrokeOpacity(0);
    polygon.setGeodesic(false);
    addPolygonOverlay(polygon);
  }

  private List<LatLon> createCircle(LatLon position) {
    List<LatLon> pts = new ArrayList<>();
    int ptsNumber = 360;

    double rad = 0.01;

    for(int i=0; i<ptsNumber; i++){
      double theta = i*(2*Math.PI/ptsNumber);
      double x = position.getLat() + rad*sin(theta);
      double y = position.getLon() + rad*cos(theta);
      pts.add(new LatLon(x, y));
    }
    return pts;
  }
}
