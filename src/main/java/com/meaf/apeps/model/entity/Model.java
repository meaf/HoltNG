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
  @Column(name = "alpha_s", precision = 8, scale = 5)
  private BigDecimal alpha_s;
  @Column(name = "beta_s", precision = 8, scale = 5)
  private BigDecimal beta_s;
  @Column(name = "gamma_s", precision = 8, scale = 5)
  private BigDecimal gamma_s;
  @Column(name = "alpha_w", precision = 8, scale = 5)
  private BigDecimal alpha_w;
  @Column(name = "beta_w", precision = 8, scale = 5)
  private BigDecimal beta_w;
  @Column(name = "gamma_w", precision = 8, scale = 5)
  private BigDecimal gamma_w;
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

  public BigDecimal getAlpha_s() {
    return alpha_s;
  }

  public void setAlpha_s(BigDecimal alpha_s) {
    this.alpha_s = alpha_s;
  }

  public BigDecimal getBeta_s() {
    return beta_s;
  }

  public void setBeta_s(BigDecimal beta_s) {
    this.beta_s = beta_s;
  }

  public BigDecimal getGamma_s() {
    return gamma_s;
  }

  public void setGamma_s(BigDecimal gamma_s) {
    this.gamma_s = gamma_s;
  }

  public BigDecimal getAlpha_w() {
    return alpha_w;
  }

  public void setAlpha_w(BigDecimal alpha_w) {
    this.alpha_w = alpha_w;
  }

  public BigDecimal getBeta_w() {
    return beta_w;
  }

  public void setBeta_w(BigDecimal beta_w) {
    this.beta_w = beta_w;
  }

  public BigDecimal getGamma_w() {
    return gamma_w;
  }

  public void setGamma_w(BigDecimal gamma_w) {
    this.gamma_w = gamma_w;
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
