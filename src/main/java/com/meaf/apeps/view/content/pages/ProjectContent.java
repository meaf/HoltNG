package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.Project;
import com.meaf.apeps.view.beans.*;
import com.meaf.apeps.view.components.LoginPopup;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.*;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;

@Component
public class ProjectContent extends ABaseContent{

  private ProjectBean projectBean;
  private PropertiesBean propertiesBean;
  private LocationBean locationBean;
  private ModelBean modelBean;
  private SessionBean sessionBean;

  GoogleMap googleMap;
  ComboBox<Project> cbProjects;
  Grid<Model> modelGrid;



  public ProjectContent(ProjectBean projectBean, PropertiesBean propertiesBean, LocationBean locationBean, ModelBean modelBean, SessionBean sessionBean) {
    this.projectBean = projectBean;
    this.propertiesBean = propertiesBean;
    this.locationBean = locationBean;
    this.modelBean = modelBean;
    this.sessionBean = sessionBean;
  }

  @Override
  public com.vaadin.ui.Component getContent() {
    MVerticalLayout layout = new MVerticalLayout();

    cbProjects = createProjectsComboBox();
    googleMap = createMap();
    modelGrid = createModelGrid();
    Button btnLogin = createLoginBtn();

    layout.add(cbProjects);
    layout.add(googleMap);
    layout.add(modelGrid);
    layout.add(btnLogin);

    fill();

    btnLogin.setVisible(!sessionBean.isUserLoggedIn());

    return layout;
  }

  private void fill() {
    if(fillProjects()) {
      fillMap();
      fillModels();
    }
  }

  private Button createLoginBtn() {
    Button btnLogin = new Button("Login");
    btnLogin.addClickListener(e -> new LoginPopup(e, sessionBean::authenticate));
    return btnLogin;
  }

  private Grid<Model> createModelGrid() {
    Grid<Model> grid = new Grid<>();
    grid.setSizeFull();
    grid.addColumn(Model::getName).setCaption("Name").setExpandRatio(1);

    grid.addColumn(m -> m.getLocation().getLatitude() + "/" + m.getLocation().getLongitude()).setCaption("Lat/Lon")
        .setExpandRatio(2);

    grid.addColumn(Model::getDataAmount).setCaption("Data amount");
    grid.addColumn(Model::getAvgGhi).setCaption("Avg GHI");
    grid.addColumn(Model::getAvgWindSpeed).setCaption("Avg wind speed");
    grid.addColumn(Model::getMse).setCaption("MSE");
    grid.addColumn(Model::getGhiForecastForecast).setCaption("Forecast(GHI)");
    grid.addColumn(Model::getWindSpeedForecast).setCaption("Forecast(WS)");

    return grid;
  }

  private void fillModels() {
    List<Model> models = projectBean.getModels();
    modelGrid.setData(models);
  }

  private GoogleMap createMap() {
    GoogleMap googleMap = new GoogleMap(propertiesBean.getMapsKey(), null, "english");
    googleMap.setSizeFull();
    googleMap.setMinZoom(4);
    googleMap.setMaxZoom(16);
    return googleMap;
  }

  private void fillMap() {
    List<Location> locations = locationBean.listProjectLocations(projectBean.getProject());
    for(Location location : locations) {
      LatLon latLon = new LatLon(location.getLatitude().doubleValue(), location.getLongitude().doubleValue());
      googleMap.addMarker(location.getName(), latLon, false, null);
      googleMap.setCenter(latLon);
    }
  }

  private ComboBox<Project> createProjectsComboBox() {
    ComboBox<Project> cbProjects = new ComboBox<>();
    cbProjects.setCaption("Select a project");
    return cbProjects;
  }

  private boolean fillProjects() {
    List<Project> projects = projectBean.getUserAvailableProjects();
    googleMap.setVisible(!projects.isEmpty());
    modelGrid.setVisible(!projects.isEmpty());
    if(projects.isEmpty())
      return false;
    cbProjects.setDataProvider(new ListDataProvider<>(projects));
    return true;
  }

}
