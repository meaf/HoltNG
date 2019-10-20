package com.meaf.apeps.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "weatherStateData")
public class WeatherStateData extends ABaseEntity {
  @Column
  private Date date;
  @Column
  private Long modelId;
  @Column
  private Integer ghi = 0;
  @Column
  private Integer ebh = 0;
  @Column
  private Integer dni = 0;
  @Column
  private Integer dhi = 0;
  @Column
  private Double cloudOpacity = 0.;
  @Column
  private Double windSpeed = 0.;

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Integer getGhi() {
    return ghi;
  }

  public void setGhi(Integer ghi) {
    this.ghi = ghi;
  }

  public Integer getEbh() {
    return ebh;
  }

  public void setEbh(Integer ebh) {
    this.ebh = ebh;
  }

  public Integer getDni() {
    return dni;
  }

  public void setDni(Integer dni) {
    this.dni = dni;
  }

  public Integer getDhi() {
    return dhi;
  }

  public void setDhi(Integer dhi) {
    this.dhi = dhi;
  }

  public Double getCloudOpacity() {
    return cloudOpacity;
  }

  public void setCloudOpacity(Double cloudOpacity) {
    this.cloudOpacity = cloudOpacity;
  }

  public Double getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(Double windSpeed) {
    this.windSpeed = windSpeed;
  }

  public Date getDate() {
    return date;
  }

  public TimelessDate asTimelessDate() {
    return new TimelessDate(date.getTime());
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
