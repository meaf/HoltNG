package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.Project;
import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.model.entity.UserProjectRelation;
import com.meaf.apeps.model.repository.ModelRepository;
import com.meaf.apeps.model.repository.ProjectRepository;
import com.meaf.apeps.model.repository.UserProjectRelationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@SessionScope
public class ProjectBean {


  private final ProjectRepository projectRepository;
  private final SessionBean sessionBean;
  private final ModelRepository modelRepository;
  private final UserProjectRelationRepository userProjectRelationRepository;

  private Project project;

  public ProjectBean(ProjectRepository projectRepository, SessionBean sessionBean, ModelRepository modelRepository, UserProjectRelationRepository userProjectRelationRepository) {
    this.projectRepository = projectRepository;
    this.sessionBean = sessionBean;
    this.modelRepository = modelRepository;
    this.userProjectRelationRepository = userProjectRelationRepository;
  }

  @PostConstruct
  void initProject() {
    this.project = getUserAvailableProjects().stream().findAny().orElse(null);
  }

  public List<Model> getModels() {
    return modelRepository.findModelsByProjectId(this.project.getId());
  }

  public void switchProject(Long projectId) {
    this.project = projectRepository.findById(projectId).orElse(null);
  }

  public List<Project> getUserAvailableProjects() {
    User user = sessionBean.getLoggedInUser();
    Long userId = user == null ? -1L : user.getId();
    Boolean admin = user == null ? false : user.getAdmin();
    return projectRepository.findAvailableProject(userId, admin);
  }

  public Project getProject() {
    return project;
  }

  public Project save(Project p, User user) {

    Project saved = projectRepository.save(p);
    if (saved.getPrivateProject()) {
      UserProjectRelation relation = new UserProjectRelation();
      relation.setUserId(user.getId());
      relation.setProjectId(p.getId());
    }

    return saved;
  }

}
