package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.Project;
import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.view.beans.*;
import com.meaf.apeps.view.components.*;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.meaf.apeps.utils.Formatter.format;
import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;

@Component
public class ProjectContent extends ABaseContent {

  private ProjectBean projectBean;
  private PropertiesBean propertiesBean;
  private LocationBean locationBean;
  private ModelBean modelBean;
  private SessionBean sessionBean;

  private GoogleMap googleMap;
  private ComboBox<Project> cbProjects;
  private MGrid<Model> modelGrid;
  private Button btnProceed;
  private List<Project> projects;
  private List<Model> models;


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
    Button sessionButton = sessionBean.isUserLoggedIn()
        ? createLogoutBtn()
        : createLoginBtn();
    btnProceed = createProceedBtn();
    Button btnNewProject = createNewProjectBtn();
    Button btnNewModel = createNewModelBtn();
    Button btnNewLocation = createNewLocationBtn();

    MHorizontalLayout buttons = new MHorizontalLayout();
    buttons.add(sessionButton, btnProceed, btnNewProject, btnNewModel, btnNewLocation);

    layout.add(cbProjects);
    layout.add(googleMap);
    layout.add(modelGrid);
    layout.add(buttons);

    fill();

    cbProjects.setSizeFull();
    layout.setExpandRatio(cbProjects, 0);
    layout.setExpandRatio(googleMap, 3);
    layout.setExpandRatio(modelGrid, 4);


    return layout;
  }

  private void fill() {
    if (fillProjects()) {
      fillMap();
      fillModels(true);
    }
  }

  private Button createLoginBtn() {
    Button btnLogin = new Button("Login");
    btnLogin.addClickListener(e -> new LoginPopup(e, sessionBean::authenticate, u -> register(e, u)));
    return btnLogin;
  }

  private void register(Button.ClickEvent e, User user) {
    if(sessionBean.checkUsernameAvailable(user.getName())){
      sessionBean.register(user);
      new LoginPopup(e, sessionBean::authenticate, u -> register(e, u));
    } else EToast.ERROR.show("Error", "this username is already taken");
  }

  private Button createLogoutBtn() {
    Button btnLogin = new Button("Logout");
    btnLogin.addClickListener(e -> sessionBean.logout(e));

    return btnLogin;
  }

  private Button createNewProjectBtn() {
    Button button = new Button("New project");
    Consumer<Project> fillProjects = this::addProject;
    button.addClickListener(e -> new ProjectCreationPopup(e, projectBean, sessionBean.getLoggedInUser(), fillProjects));
    button.setVisible(sessionBean.isUserLoggedIn());
    return button;
  }
  private Button createNewModelBtn() {
    Button button = new Button("New model");
    button.addClickListener(e -> new ModelCreationPopup(e, locationBean.listLocations(), this::addModel));
    button.setVisible(sessionBean.isUserLoggedIn());
    return button;
  }

  private Button createNewLocationBtn() {
    Button button = new Button("New location");
    button.addClickListener(e -> new LocationCreationPopup(e, this::addLocation));
    button.setVisible(sessionBean.isUserLoggedIn());
    return button;
  }

  private void addProject(Project project) {
    projects.add(project);
    cbProjects.setDataProvider(new ListDataProvider<>(projects));
    EToast.SUCCESS.show("Saved", "Project saved successfully");
  }

  private void addModel(Model model) {
    model.setProjectId(projectBean.getProject().getId());
    modelBean.save(model);
    models.add(model);
    fillModels(false);
    fillMap();
    EToast.SUCCESS.show("Saved", "Model saved successfully");
  }

  private void addLocation(Location location) {
    String descr;
    Location saved = locationBean.save(location);
    if(location.getKeys() != null && !location.getKeys().isEmpty()) {
      location.getKeys().forEach(k -> k.setLocationId(saved.getId()));
      propertiesBean.saveLocationKey(location.getKeys().iterator().next());
      descr = "Location with Solcast key is saved successfully";
    } else {
      descr = "Location is saved without key successfully";
    }

    EToast.SUCCESS.show("Saved", descr);
  }

  private Button createProceedBtn() {
    Button button = new Button("Open model");
    button.setEnabled(false);
    button.setClickShortcut(ENTER);
    button.addClickListener(e -> openModel());
    return button;
  }

  private void openModel() {
    Set<Model> selectedItems = this.modelGrid.getSelectedItems();
    modelBean.switchModel(selectedItems.iterator().next().getId());
    changeState(StateBean.EState.Model);
  }

  private MGrid<Model> createModelGrid() {
    MGrid<Model> grid = new MGrid<>();
    grid.setSizeFull();
    grid.addColumn(Model::getName).setCaption("Name").setExpandRatio(5);

    grid.addColumn(m -> m.getLocation().getLatitude() + " / " + m.getLocation().getLongitude()).setCaption("Lat/Lon")
        .setExpandRatio(10);

    grid.addColumn(Model::getDataAmount).setCaption("Data amount").setExpandRatio(5);
    grid.addColumn(m -> format(m.getSolarForecastCell()), new DynamicsRenderer()).setCaption("Forecast(GHI)").setExpandRatio(10);
    grid.addColumn(m -> format(m.getAvgGhi())).setCaption("Avg GHI").setExpandRatio(5);
    grid.addColumn(m -> format(m.getMseWind())).setCaption("MSE(GHI)").setExpandRatio(5);
    grid.addColumn(m -> format(m.getWindForecastCell()), new DynamicsRenderer()).setCaption("Forecast(Wind)").setExpandRatio(10);
    grid.addColumn(m -> format(m.getAvgWindSpeed())).setCaption("Avg wind speed").setExpandRatio(5);
    grid.addColumn(m -> format(m.getMseSolar())).setCaption("MSE(Wind)").setExpandRatio(10);
    if(sessionBean.isUserLoggedIn())
      grid.addColumn(this::isManageable, new ComponentRenderer()).setCaption("Manageable").setExpandRatio(5);

    grid.addSelectionListener(this::selectModel);

    return grid;
  }

  private com.vaadin.ui.Component isManageable(Model model) {
    CheckBox cb = new CheckBox();
    cb.setValue(modelBean.isUserManager(model));
    cb.setReadOnly(true);
    return cb;
  }

  private void selectModel(SelectionEvent<Model> e) {
    Model model = e.getFirstSelectedItem().orElse(null);
    if (model == null)
      return;

    btnProceed.setEnabled(true);
    googleMap.setCenter(model.getLocation().toLatLon());
  }

  private void fillModels(boolean loadFromDB) {
    if(loadFromDB)
      models = projectBean.getModels();
    modelGrid.setRows(models);
  }

  private GoogleMap createMap() {
    return new GoogleMapPreset(propertiesBean.getMapsKey(), null, "english", null);
  }

  private void fillMap() {
    List<Location> locations = locationBean.listProjectLocations(projectBean.getProject());
    googleMap.clearMarkers();
    for (Location location : locations) {
      LatLon latLon = location.toLatLon();
      GoogleMapMarker marker = new GoogleMapMarker(location.getName(), latLon, false, null);
      googleMap.addMarker(marker);
      googleMap.setCenter(latLon);
    }
  }

  private ComboBox<Project> createProjectsComboBox() {
    ComboBox<Project> cbProjects = new ComboBox<>();
    cbProjects.setCaption("Select a project");
    cbProjects.setScrollToSelectedItem(true);
    cbProjects.setItemCaptionGenerator(Project::getName);
    cbProjects.setTextInputAllowed(false);
    cbProjects.addValueChangeListener(this::trySwitchProject);
    cbProjects.setEmptySelectionAllowed(false);

    return cbProjects;
  }

  private void trySwitchProject(HasValue.ValueChangeEvent<Project> e) {
    if (!e.isUserOriginated())
      return;
    Long id = e.getSource().getValue().getId();
    if (id == null)
      return;
    projectBean.switchProject(id);
    fill();
  }

  private boolean fillProjects() {
    projects = projectBean.getUserAvailableProjects();
    googleMap.setVisible(!projects.isEmpty());
    modelGrid.setVisible(!projects.isEmpty());
    if (projects.isEmpty())
      return false;
    cbProjects.setDataProvider(new ListDataProvider<>(projects));
    cbProjects.setSelectedItem(projectBean.getProject());
    return true;
  }

}
