package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.calculations.Result;
import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.csv.CSVFileReceiver;
import com.meaf.apeps.input.http.RequestHttpUpdate;
import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import com.meaf.apeps.utils.ETargetValues;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.PropertiesBean;
import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.beans.StateBean;
import com.meaf.apeps.view.components.*;
import com.vaadin.data.HasValue;
import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.meaf.apeps.utils.Formatter.format;
import static com.vaadin.event.ShortcutAction.KeyCode.*;

@Component
public class ModelContent extends ABaseContent {

  private final ModelBean modelBean;
  private final PropertiesBean propertiesBean;
  private final SessionBean sessionBean;

  private final RequestHttpUpdate requestHttpUpdate;
  private HoltWinters method;
  private MGrid<WeatherStateData> entriesGrid = new MGrid<>(WeatherStateData.class)
      .withProperties("date", "ghi", "windSpeed")
      .withColumnHeaders("date", "ghi", "windSpeed")
      .withWidth(100, Sizeable.Unit.PERCENTAGE)
      .withHeight(100, Sizeable.Unit.PERCENTAGE);
  private CoefficientsBar lhCoefficients;
  private TextField tfMSEPerc = new TextField();
  private TextField tfRMSE = new TextField();
  private TextField tfMSE = new TextField();
  private TextField tfMAE = new TextField();
  private MHorizontalLayout chartWrapper = new MHorizontalLayout();
  private MCheckBox cxSolarStats = new MCheckBox("Solar energy", true);
  private MCheckBox cxGroupMonthly = new MCheckBox("Group monthly", true);
  private Button btnCalculate;
  private Button btnSaveResults;
  private List<WeatherStateData> rowsData;
  private ModelChart modelChart;
  public ModelContent(ModelBean modelBean, PropertiesBean propertiesBean, SessionBean sessionBean, RequestHttpUpdate requestHttpUpdate) {
    this.modelBean = modelBean;
    this.propertiesBean = propertiesBean;
    this.sessionBean = sessionBean;
    this.requestHttpUpdate = requestHttpUpdate;
  }

  @Override
  public com.vaadin.ui.Component getContent() {

    btnCalculate = createCalculateButton();
    btnSaveResults = createSaveButton();
    btnSaveResults.setVisible(modelBean.isUserManager(modelBean.getModel()));
    Button btnClear = createClearButton();

    lhCoefficients = new CoefficientsBar(modelBean.getModel(), cxSolarStats.getValue());
    lhCoefficients.addButtons(btnCalculate, btnSaveResults, btnClear);

    tfMSE.setCaption("MSE");
    tfMSE.setEnabled(false);

    tfRMSE.setCaption("RMSE");
    tfRMSE.setEnabled(false);

    tfMAE.setPlaceholder("Press calculate");
    tfMAE.setCaption("MAE");
    tfMAE.setEnabled(false);

    tfMSEPerc.setCaption("MSE, %");
    tfMSEPerc.setEnabled(false);

    Button btnReturnToProject = new Button("Return");
    btnReturnToProject.setClickShortcut(ESCAPE);
    btnReturnToProject.addClickListener(this::toProjects);

    cxSolarStats.addValueChangeListener(this::toggleModelType);

    MHorizontalLayout hlBottomBar = new MHorizontalLayout(tfMAE, tfMSEPerc, tfMSE, tfRMSE, new MVerticalLayout(cxSolarStats, cxGroupMonthly));
    hlBottomBar.setCaption("Average node MSE");

    ModelChart lineChart = new ModelChart(method);
    chartWrapper.removeAllComponents();
    chartWrapper.add(lineChart);

    MVerticalLayout calculations = new MVerticalLayout(
        lhCoefficients,
        chartWrapper,
        hlBottomBar
    ).withFullWidth();

    Button btnDisplayFullGrid = createGridPropertiesButton();


    MVerticalLayout dataTableOptions = new MVerticalLayout(
        entriesGrid,
        btnDisplayFullGrid
    ).withExpand(entriesGrid, 10)
        .withExpand(btnDisplayFullGrid, 1)
        .withFullHeight();

    dataTableOptions.setSizeFull();

    MHorizontalLayout data = new MHorizontalLayout(
        dataTableOptions,
        calculations
    ).withFullWidth();

    data.setExpandRatio(dataTableOptions, 2);
    data.setExpandRatio(calculations, 7);

    DisclosurePanel aboutBox = new DisclosurePanel(modelBean.getModel().getName(), new RichText(modelBean.getModel().getDescription()), getMap());
    MHorizontalLayout sessionBarAdditional = new MHorizontalLayout(btnReturnToProject, aboutBox);
    sessionBarAdditional.setDefaultComponentAlignment(Alignment.TOP_LEFT);
    sessionBarAdditional.expand(aboutBox);


    MVerticalLayout content = new MVerticalLayout(
        new SessionInfoBar(sessionBean, sessionBarAdditional),
        data
    );
    updateDataGrid(entriesGrid);

    if (lhCoefficients.isFilled()) {
      try {
        calculate();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return content;
  }

  private void toggleModelType(HasValue.ValueChangeEvent<Boolean> e) {
    boolean isSolar = e.getValue();
    Model model = modelBean.getModel();

    if (isSolar) {
      if (model.getAlpha_s() != null)
        lhCoefficients.setAlpha(model.getAlpha_s().doubleValue());
      else lhCoefficients.resetAlpha();
      if (model.getBeta_s() != null)
        lhCoefficients.setBeta(model.getBeta_s().doubleValue());
      else lhCoefficients.resetBeta();
      if (model.getGamma_s() != null)
        lhCoefficients.setGamma(model.getGamma_s().doubleValue());
      else lhCoefficients.resetGamma();
    } else {
      if (model.getAlpha_w() != null)
        lhCoefficients.setAlpha(model.getAlpha_w().doubleValue());
      else lhCoefficients.resetAlpha();
      if (model.getBeta_w() != null)
        lhCoefficients.setBeta(model.getBeta_w().doubleValue());
      else lhCoefficients.resetBeta();
      if (model.getGamma_w() != null)
        lhCoefficients.setGamma(model.getGamma_w().doubleValue());
      else lhCoefficients.resetGamma();
    }

    if(lhCoefficients.isFilled() && lhCoefficients.check()) {
      calculate();
    } else {
      clearChart();
    }
  }

  private Button createGridPropertiesButton() {
    Button button = new Button("Data management");
    button.setVisible(modelBean.isUserManager(modelBean.getModel()));
    button.addClickListener(this::createPropertiesWindow);
    return button;
  }

  private void createPropertiesWindow(Button.ClickEvent clickEvent) {
    clickEvent.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    MWindow window = new MWindow("Model Data")
        .withCenter()
        .withModal(true)
        .withClosable(true)
        .withHeight(60, Sizeable.Unit.PERCENTAGE)
        .withWidth(60, Sizeable.Unit.PERCENTAGE);

    MGrid<WeatherStateData> grid = getDetailedGridBase();

    grid.sort("date");
    grid.setRows(rowsData);

    MHorizontalLayout buttons = new MHorizontalLayout();

    buttons.add(createUploadButton(grid), createUpdateButton(window, grid));
    buttons.add(createDeleteButton(grid), createDeleteAllButton(grid));

    MVerticalLayout layout = new MVerticalLayout(
        grid,
        buttons)
        .withExpand(grid, 1)
        .withFullSize()
        .withAlign(grid, Alignment.MIDDLE_CENTER)
        .withAlign(buttons, Alignment.MIDDLE_CENTER);

    window.setContent(layout);

    clickEvent.getButton().getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    clickEvent.getButton().getUI().addWindow(window);
  }

  private Button createDeleteButton(MGrid<WeatherStateData> grid) {
    Button button = new Button("Delete");
    button.setClickShortcut(DELETE);
    button.addClickListener(e -> new ConfirmationPopup(e, "Delete selected rows?", d -> {
      if(grid.getSelectedItems().size() == 0){
        EToast.ERROR.show("", "Select an item");
      }

      deleteRows(grid.getSelectedItems());
      updateDataGrid(entriesGrid);
      updateDataGrid(grid);
    }));
    return button;
  }

  private Button createDeleteAllButton(MGrid<WeatherStateData> grid) {
    Button button = new Button("Delete All");
    button.setStyleName("danger");
    button.addClickListener(e -> new ConfirmationPopup(e, "Delete all rows?", d -> {
      deleteRows(rowsData);
      updateDataGrid(entriesGrid);
      updateDataGrid(grid);
    }));
    return button;
  }

  private void deleteRows(Iterable<WeatherStateData> rows) {
    modelBean.removeItems(rows);
    rowsData = modelBean.getEntries();
  }

  private MGrid<WeatherStateData> getDetailedGridBase() {
    return new MGrid<>(WeatherStateData.class)
        .withProperties("date", "ghi", "ebh", "dni", "dhi", "cloudOpacityFormatted", "windSpeed")
        .withColumnHeaders("date", "ghi", "ebh", "dni", "dhi", "cloudOpacity", "windSpeed")
        .withWidth(90, Sizeable.Unit.PERCENTAGE)
        .withHeight(90, Sizeable.Unit.PERCENTAGE);
  }

  private GoogleMap getMap() {
    Location location = modelBean.getModel().getLocation();
    LatLon latLon = location.toLatLon();
    GoogleMapMarker googleMapMarker = new GoogleMapMarker(location.getName(), latLon, false, null);

    GoogleMap googleMap = new GoogleMapPreset(propertiesBean.getMapsKey(), null, "english", Collections.singletonList(googleMapMarker));
    googleMap.setCenter(latLon);
    return googleMap;
  }

  private Button createClearButton() {
    Button btnClear = new Button("Clear");
    btnClear.addClickListener(e -> lhCoefficients.reset());
    return btnClear;
  }

  private Button createUpdateButton(MWindow parentWindow, MGrid<WeatherStateData> grid) {
    Button updButton = new Button("Update");
    updButton.addClickListener(e -> {
      List<WeatherStateData> weatherStateDataList = returnOnlyMissingValues(requestHttpUpdate.requestUpdate(modelBean.getModel().getLocation().getId()));
      if (weatherStateDataList == null) {
        EToast.ERROR.show("Http update error", "could not communicate to data sever \nCheck if API keys are correct");
        return;
      }
      createNewDataWindow(grid, parentWindow, weatherStateDataList, false);

    });

    return updButton;
  }

  private List<WeatherStateData> returnOnlyMissingValues(List<WeatherStateData> weatherStateDataList) {
    if (weatherStateDataList == null)
      return null;
    return weatherStateDataList.stream().filter(e -> isDataMissing(rowsData, e)).collect(Collectors.toList());
  }

  private boolean isDataMissing(List<WeatherStateData> entries, WeatherStateData stateData) {
    return entries.stream().noneMatch(e -> e.asDayUnit().equals(stateData.asDayUnit()));
  }

  private Upload createUploadButton(MGrid<WeatherStateData> grid) {
    CSVFileReceiver fileReceiver = new CSVFileReceiver();

    fileReceiver.setDateScope(DateUtils.asSqlDate(new Date(107, Calendar.JANUARY, 2))); //TODO

    Upload uploadBtn = new Upload(null, fileReceiver);
    uploadBtn.setImmediateMode(false);
    uploadBtn.setButtonCaption("Upload File");

    UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(uploadBtn, fileReceiver);

    uploadBtn.addStartedListener(event -> {
      if ("".equals(event.getFilename()) || event.getContentLength() == 0) {
        EToast.ERROR.show("", "Select a file first");
        return;
      }
      uploadBtn.setComponentError(null);

      fileReceiver.reset();
      if (uploadInfoWindow.getParent() == null) {
        UI.getCurrent().addWindow(uploadInfoWindow);
      }
      uploadInfoWindow.setClosable(false);
    });
    uploadBtn.addFinishedListener(event -> {
      uploadInfoWindow.setClosable(true);
      uploadInfoWindow.setShowResultsAction(e -> createNewDataWindow(grid, uploadInfoWindow, returnOnlyMissingValues(uploadInfoWindow.getResults()), true));
    });
    return uploadBtn;
  }

  private Button createCalculateButton() {
    Button button = new Button("Calculate");
    button.setClickShortcut(ENTER);
    button.setStyleName("primary");
    button.addClickListener(e -> {
      try {
        calculate();
      } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
      }
    });
    return button;
  }

  private Button createSaveButton() {

    return new Button("Save results", e -> {
      if (method.getDateInterval() != HoltWinters.EDateInterval.MONTHLY) {
        EToast.ERROR.show("Error", "Can save only monthly model");
        return;
      }
      new ConfirmationPopup(e, "Apply parameters to model?", c -> {
        try {
          calculate();
          saveModel();
        } catch (IllegalArgumentException ex) {
          ex.printStackTrace();
        }
      });
    });
  }

  private void saveModel() {
    if (method.getDateInterval() != HoltWinters.EDateInterval.MONTHLY) {
      EToast.ERROR.show("Error", "Can save only monthly model");
      return;
    }

    if (method.getTargetType() == ETargetValues.SOLAR) {
      modelBean.getModel().setGhiForecast(method.getNearestForecast().asDouble());
      modelBean.getModel().setMseSolar(new BigDecimal(tfMSE.getValue()).setScale(5, BigDecimal.ROUND_HALF_UP));
      modelBean.getModel().setGhiLast(method.getLastActualData());
      modelBean.getModel().setAlpha_s(new BigDecimal(lhCoefficients.getAlpha()));
      modelBean.getModel().setBeta_s(new BigDecimal(lhCoefficients.getBeta()));
      modelBean.getModel().setGamma_s(new BigDecimal(lhCoefficients.getGamma()));
    } else {
      modelBean.getModel().setWindSpeedForecast(method.getNearestForecast().asDouble());
      modelBean.getModel().setMseWind(new BigDecimal(tfMSE.getValue()).setScale(5, BigDecimal.ROUND_HALF_UP));
      modelBean.getModel().setWindSpeedLast(method.getLastActualData());
      modelBean.getModel().setAlpha_w(new BigDecimal(lhCoefficients.getAlpha()));
      modelBean.getModel().setBeta_w(new BigDecimal(lhCoefficients.getBeta()));
      modelBean.getModel().setGamma_w(new BigDecimal(lhCoefficients.getGamma()));
    }
    modelBean.saveCurrentModel();

    EToast.SUCCESS.show("Success", "Model params are saved");
  }

  private void createNewDataWindow(MGrid<WeatherStateData> mgmtGrid, com.vaadin.ui.Component src, List<WeatherStateData> data, boolean displayWindColumn) {
    if(src.getUI().getWindows().stream().anyMatch(w -> "New Data".equals(w.getCaption())))
      return;
    if(data.isEmpty()) {
      EToast.SUCCESS.show("Everything is up-to-date", "nothing to update");
      return;
    }
    MWindow window = new MWindow("New Data")
        .withCenter()
        .withModal(true)
        .withClosable(true)
        .withHeight(60, Sizeable.Unit.PERCENTAGE)
        .withWidth(60, Sizeable.Unit.PERCENTAGE);


    MGrid<WeatherStateData> grid = getDetailedGridBase();

    if (!displayWindColumn)
      grid.removeColumn("windSpeed");

    grid.sort("date");
    grid.setRows(data);
    Button mergeDataBtn = new Button("Save to DB");
    mergeDataBtn.setVisible(true);
    mergeDataBtn.addClickListener(e -> {
      Long id = modelBean.getModel().getId();
      data.forEach(d -> d.setModelId(id));
      modelBean.mergeEntries(data);
      EToast.SUCCESS.show("Merged", data.size() + " entries were added to model");
      updateDataGrid(entriesGrid);
      updateDataGrid(mgmtGrid);
      window.close();
    });

    MVerticalLayout layout = new MVerticalLayout(
        grid,
        mergeDataBtn)
        .withExpand(grid, 1)
        .withFullSize()
        .withAlign(grid, Alignment.MIDDLE_CENTER)
        .withAlign(mergeDataBtn, Alignment.MIDDLE_CENTER);

    window.setContent(layout);
    src.getUI().addWindow(window);
  }

  private void calculate() {
    if (!lhCoefficients.check())
      throw new IllegalArgumentException();

    if (rowsData.isEmpty()) {
      throw new IllegalArgumentException();
    }
    List<WeatherStateData> dataList = cxGroupMonthly.getValue()
        ? WeatherAggregator.dailyToMonthy(rowsData)
        : rowsData;

    method = new HoltWinters();
    method.setTargetType(cxSolarStats.getValue() ? ETargetValues.SOLAR : ETargetValues.WIND);
    method.setDateInterval(cxGroupMonthly.getValue() ? HoltWinters.EDateInterval.MONTHLY : HoltWinters.EDateInterval.DAILY);

    if (dataList.isEmpty() || dataList.size() < lhCoefficients.getPeriod()) {
      EToast.ERROR.show("Error", "not enough data to build the model, consider uploading new stats or select smaller period");
      throw new IllegalArgumentException();
    }
    method.calculate(dataList,
        lhCoefficients.getAlpha(),
        lhCoefficients.getBeta(),
        lhCoefficients.getGamma(),
        lhCoefficients.getPeriod(),
        lhCoefficients.getForecastPoints());

    Result result = method.getOptimalResult();

    tfMSEPerc.setValue(format(result.getMsePerc()));
    tfRMSE.setValue(format(result.getRmse()));
    tfMSE.setValue(format(result.getMse()));
    tfMAE.setValue(format(result.getMae()));

    lhCoefficients.setAlpha(result.getAlpha());
    lhCoefficients.setBeta(result.getBeta());
    lhCoefficients.setGamma(result.getGamma());

    redrawChart();
  }

  private void updateDataGrid(MGrid<WeatherStateData> grid) {
    List<WeatherStateData> dataList = modelBean.getEntries().stream().peek(
        e -> e.setWindSpeed(Double.parseDouble(format(e.getWindSpeed())))
    ).collect(Collectors.toList());

    this.rowsData = dataList;
    grid.setRows(dataList);
    enableModelButtons(!dataList.isEmpty());
  }

  private void enableModelButtons(boolean enabled) {
    btnCalculate.setEnabled(enabled);
    btnSaveResults.setEnabled(enabled);
  }

  private void redrawChart() {
    this.chartWrapper.removeAllComponents();
    modelChart = new ModelChart(method);
    modelChart.setSizeFull();
    this.chartWrapper.add(modelChart).setSizeFull();
  }

  private void clearChart() {
    this.chartWrapper.removeAllComponents();
    modelChart = new ModelChart(null);
    modelChart.setSizeFull();
    this.chartWrapper.add(modelChart).setSizeFull();
  }

  private void toProjects(Button.ClickEvent e) {
    method = null;
    tfMSE.clear();
    tfMSEPerc.clear();
    tfMAE.clear();
    tfRMSE.clear();
    changeState(StateBean.EState.Project);
  }

}
