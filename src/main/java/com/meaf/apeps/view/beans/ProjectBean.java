package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.Project;
import com.meaf.apeps.model.repository.ModelRepository;
import com.meaf.apeps.model.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Component
@SessionScope
public class ProjectBean {

  @Autowired
  ProjectRepository projectRepository;
  @Autowired
  ModelRepository modelRepository;

  private Project project;

  public List<Model> getModels(){
    return modelRepository.findModelsByProjectId(this.project.getId());
  };

  public void switchProject(long id) {
    this.project = projectRepository.findById(id).get();
  }
}
