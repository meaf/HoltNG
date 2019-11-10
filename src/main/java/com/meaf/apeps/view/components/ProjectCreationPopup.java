package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Project;
import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.view.beans.ProjectBean;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.viritin.layouts.MWindow;

import java.util.function.Consumer;

public class ProjectCreationPopup extends MWindow {
  public ProjectCreationPopup(Button.ClickEvent e, ProjectBean projectBean, User user, Consumer<Project> updater) {
    MWindow window = new MWindow("Create new project")
        .withCenter()
        .withModal(true)
        .withDraggable(false)
        .withResizable(false)
        .withClosable(true)
        .withHeight(30, Sizeable.Unit.PERCENTAGE)
        .withWidth(20, Sizeable.Unit.PERCENTAGE);


    FormLayout layout = new FormLayout();
    layout.setSpacing(true);
    layout.setMargin(true);

    TextField tfName = new TextField("Project name");
    TextField tfDescr = new TextField("Project description");
    CheckBox cbPrivate = new CheckBox("Is private?");

    Button btnSubmit = new Button("Create");
    btnSubmit.addClickListener(ev -> {
      Project project = new Project();
      project.setName(tfName.getValue());
      project.setDescription(tfDescr.getValue());
      project.setPrivateProject(cbPrivate.getValue());
      projectBean.save(project, user);
      EToast.SUCCESS.show("Success", "Project \"" + project.getName() + "\" created");
      updater.accept(project);
      window.close();
    });

    layout.addComponents(tfName, tfDescr, cbPrivate, btnSubmit);

    window.setContent(layout);

    e.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    e.getButton().getUI().addWindow(window);

  }
}
