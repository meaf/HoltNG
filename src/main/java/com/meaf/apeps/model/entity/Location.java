package com.meaf.apeps.model.entity;

import com.vaadin.tapio.googlemaps.client.LatLon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location extends ABaseEntity {

  @Column(precision = 8, scale = 5)
  private String name;
  @Column(precision = 8, scale = 5)
  private BigDecimal latitude;
  @Column(precision = 8, scale = 5)
  private BigDecimal longitude;

  @Transient
  private List<LocationKey> keys;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public LatLon toLatLon() {
    return new LatLon(this.getLatitude().doubleValue(), this.getLongitude().doubleValue());
  }

  public List<LocationKey> getKeys() {
    return keys;
  }

  public void setKeys(List<LocationKey> keys) {
    this.keys = keys;
  }
}
