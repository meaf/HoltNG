package com.meaf.apeps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "model")
public class Model extends ABaseEntity {

    @Column(name="name")
    private String name;

    @Column(name="projectId")
    private Long projectId;

    @Column(name="userId")
    private Long userId;

    @Column(name="alpha")
    private BigDecimal alpha;

    @Column(name="beta")
    private BigDecimal beta;

    @Column(name="gamma")
    private BigDecimal gamma;

    @Column(name="accuracy")
    private BigDecimal accuracy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }
}
