package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.Project;
import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.model.repository.ModelRepository;
import com.meaf.apeps.model.repository.ProjectRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.rmi.NoSuchObjectException;
import java.util.List;

@Component
@SessionScope
public class ProjectBean {


  private final ProjectRepository projectRepository;
  private final SessionBean sessionBean;
  private final ModelRepository modelRepository;

  private Project project;

  @PostConstruct
  void initProject(){
    this.project = getUserAvailableProjects().stream().findAny().orElse(null);
  }

  public ProjectBean(ProjectRepository projectRepository, SessionBean sessionBean, ModelRepository modelRepository) {
    this.projectRepository = projectRepository;
    this.sessionBean = sessionBean;
    this.modelRepository = modelRepository;
  }

  public List<Model> getModels(){
    return modelRepository.findModelsByProjectId(this.project.getId());
  }

  public void switchProject(long projectId) throws NoSuchObjectException {
    this.project = projectRepository.findById(projectId).orElseThrow(() -> new NoSuchObjectException("Cannot find project by id=" + projectId));
  }

  public List<Project> getUserAvailableProjects() {
    User user = sessionBean.getUser();
    Long userId = user == null ? -1L : user.getId();
    return projectRepository.findAvailableProject(userId);
  }

  public Project getProject() {
    return project;
  }
}
