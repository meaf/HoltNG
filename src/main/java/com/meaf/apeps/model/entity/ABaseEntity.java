package com.meaf.apeps.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@MappedSuperclass
public abstract class ABaseEntity implements Serializable {

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dateCreate")
  protected Date dateCreate = new Date();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "lastUpdate")
  protected Date lastUpdate = new Date();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  protected Long id;

  public Date getDateCreate() {
    return dateCreate;
  }

  public void setDateCreate(Date dateCreate) {
    this.dateCreate = dateCreate;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
