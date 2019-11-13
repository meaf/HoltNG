package com.meaf.apeps.model.entity;

import com.meaf.apeps.utils.Formatter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.meaf.apeps.utils.Formatter.format;

@Entity
@Table(name = "models")
public class Model extends ABaseEntity {

  @Column
  private String name;
  @Column
  private String description;
  @Column
  private Long projectId;
  @ManyToOne(fetch = FetchType.EAGER, cascade = {})
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;
  @Column(name = "alpha", precision = 8, scale = 5)
  private BigDecimal alpha;
  @Column(name = "beta", precision = 8, scale = 5)
  private BigDecimal beta;
  @Column(name = "gamma", precision = 8, scale = 5)
  private BigDecimal gamma;
  @Column(name = "mse_wind", precision = 15, scale = 5)
  private BigDecimal mseWind;
  @Column(name = "mse_solar", precision = 15, scale = 5)
  private BigDecimal mseSolar;
  @Column
  private Integer period;
  @Formula("(select avg(d.ghi) from weather_state_data d where d.model_id = id)")
  private Double avgGhi;
  @Formula("(select avg(d.wind_speed) from weather_state_data d where d.model_id = id)")
  private Double avgWindSpeed;
  @Column
  private Double windSpeedForecast;
  @Column
  private Double ghiForecast;
  @Column
  private Double windSpeedLast;
  @Column
  private Double ghiLast;

  @Formula("(select count(*) from weather_state_data d where d.model_id = id)")
  private Integer dataAmount;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public BigDecimal getAlpha() {
    return alpha;
  }

  public void setAlpha(BigDecimal alpha) {
    this.alpha = alpha;
  }

  public BigDecimal getBeta() {
    return beta;
  }

  public void setBeta(BigDecimal beta) {
    this.beta = beta;
  }

  public BigDecimal getGamma() {
    return gamma;
  }

  public void setGamma(BigDecimal gamma) {
    this.gamma = gamma;
  }

  public BigDecimal getMseWind() {
    return mseWind;
  }

  public void setMseWind(BigDecimal mseWind) {
    this.mseWind = mseWind;
  }

  public BigDecimal getMseSolar() {
    return mseSolar;
  }

  public void setMseSolar(BigDecimal mseSolar) {
    this.mseSolar = mseSolar;
  }

  public Integer getPeriod() {
    return period;
  }

  public void setPeriod(Integer period) {
    this.period = period;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Double getAvgGhi() {
    return avgGhi;
  }

  public Double getAvgWindSpeed() {
    return avgWindSpeed;
  }

  public Double getWindSpeedForecast() {
    return windSpeedForecast;
  }

  public void setWindSpeedForecast(Double windSpeedForecast) {
    this.windSpeedForecast = windSpeedForecast;
  }

  public Double getGhiForecast() {
    return ghiForecast;
  }

  public void setGhiForecast(Double ghiForecast) {
    this.ghiForecast = ghiForecast;
  }

  public Integer getDataAmount() {
    return dataAmount;
  }

  public Double getWindSpeedLast() {
    return windSpeedLast;
  }

  public void setWindSpeedLast(Double windSpeedLast) {
    this.windSpeedLast = windSpeedLast;
  }

  public Double getGhiLast() {
    return ghiLast;
  }

  public void setGhiLast(Double ghiLast) {
    this.ghiLast = ghiLast;
  }

  public String getWindForecastCell() {
    return windSpeedForecast == null || windSpeedLast == null
        ? "" : format(windSpeedForecast) + " " + format(windSpeedLast);
  }

  public String getSolarForecastCell() {
    return ghiForecast == null || ghiLast == null
        ? "" : format(ghiForecast) + " " + format(ghiLast);
  }

}
