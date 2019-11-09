package com.meaf.apeps.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends ABaseEntity {

  @Column
  private Boolean privateProject;
  @Column
  private String name;
  @Column
  private String description;


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

  public Boolean getPrivateProject() {
    return privateProject;
  }

  public void setPrivateProject(Boolean privateProject) {
    this.privateProject = privateProject;
  }
}
