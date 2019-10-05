package com.meaf.apeps.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@MappedSuperclass
public abstract class ABaseEntity implements Serializable{

    /**
     * Дата создания записи
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_Create")
    protected Date dateCreate = new Date();

    /**
     * Дата последней модификации записи
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_Update")
    protected Date lastUpdate = new Date();

    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
