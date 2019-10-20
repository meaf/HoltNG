package com.meaf.apeps.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "weatherStateData")
public class WeatherStateData extends ABaseEntity {
  @Column
  private Integer ghi;
  @Column
  private Integer ebh;
  @Column
  private Integer dni;
  @Column
  private Integer dhi;
  @Column
  private Integer cloudOpacity;
  @Column
  private Integer windSpeed;

}
