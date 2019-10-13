package com.meaf.apeps.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "dataEntries")
public class DataEntry extends ABaseEntity {

  @Column(name = "value")
  private BigDecimal value;

  @Column(name = "date")
  private Date date;

  @Column(name = "modelId")
  private Long modelId;

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }
}
