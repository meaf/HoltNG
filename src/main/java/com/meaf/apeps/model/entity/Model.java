package com.meaf.apeps.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "models")
public class Model extends ABaseEntity {
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String fieldsName;
    @Column
    private Long projectId;
    @Column
    private Long userId;
    @Column(name="alpha", precision = 8, scale = 5)
    private BigDecimal alpha;
    @Column(name="beta", precision = 8, scale = 5)
    private BigDecimal beta;
    @Column(name="gamma", precision = 8, scale = 5)
    private BigDecimal gamma;
    @Column(name="mse", precision = 15, scale = 5)
    private BigDecimal mse;
    @Column
    private Integer period;


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

    public String getFieldsName() {
        return fieldsName;
    }

    public void setFieldsName(String fieldsName) {
        this.fieldsName = fieldsName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public BigDecimal getMse() {
        return mse;
    }

    public void setMse(BigDecimal mse) {
        this.mse = mse;
    }

  public Integer getPeriod() {
    return period;
  }

  public void setPeriod(Integer period) {
    this.period = period;
  }
}
