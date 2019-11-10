package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.calculations.Result;
import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.csv.CSVFileReceiver;
import com.meaf.apeps.input.http.RequestHttpUpdate;
import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import com.meaf.apeps.utils.ETargetValues;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.PropertiesBean;
import com.meaf.apeps.view.beans.StateBean;
import com.meaf.apeps.view.components.*;
import com.vaadin.server.Sizeable;
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
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelContent extends ABaseContent {

  private static DecimalFormat df = new DecimalFormat("#.####");
  private final ModelBean modelBean;
  private final PropertiesBean propertiesBean;

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

  public ModelContent(ModelBean modelBean, PropertiesBean propertiesBean, RequestHttpUpdate requestHttpUpdate) {
    this.modelBean = modelBean;
    this.propertiesBean = propertiesBean;
    this.requestHttpUpdate = requestHttpUpdate;
  }

  @Override
  public com.vaadin.ui.Component getContent() {

    btnCalculate = createCalculateButton();
    btnSaveResults = createSaveButton();
    btnSaveResults.setVisible(modelBean.isUserManager());
    Button btnClear = createClearButton();

    lhCoefficients = new CoefficientsBar(modelBean.getModel());
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
    btnReturnToProject.addClickListener(e -> changeState(StateBean.EState.Project));

    MHorizontalLayout hlBottomBar = new MHorizontalLayout(tfMAE, tfMSEPerc, tfMSE, tfRMSE, new MVerticalLayout(cxSolarStats, cxGroupMonthly), btnReturnToProject);
    hlBottomBar.setCaption("Average node MSE");

    ModelChart lineChart = new ModelChart(method);
    chartWrapper.removeAllComponents();
    chartWrapper.add(lineChart);

    MVerticalLayout calculations = new MVerticalLayout(
        lhCoefficients,
        chartWrapper,
        hlBottomBar
    ).withFullWidth();

    Upload uploadButton = createUploadButton();
    Button updateButton = createUpdateButton();
    MVerticalLayout dataTableOptions = new MVerticalLayout(
        entriesGrid,
        uploadButton,
        updateButton
    ).withExpand(entriesGrid, 10)
        .withExpand(updateButton, 1)
        .withExpand(uploadButton, 1)
        .withFullHeight();

    dataTableOptions.setSizeFull();

    MHorizontalLayout data = new MHorizontalLayout(
        dataTableOptions,
        calculations
    ).withFullWidth();

    data.setExpandRatio(dataTableOptions, 2);
    data.setExpandRatio(calculations, 7);

    DisclosurePanel aboutBox = new DisclosurePanel(modelBean.getModel().getName(), new RichText(modelBean.getModel().getDescription()), getMap());
    MVerticalLayout content = new MVerticalLayout(
        aboutBox,
        data
    );
    updateDataGrid();

    if (lhCoefficients.isFilled()) {
      try {
        calculate();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return content;
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

  private Button createUpdateButton() {
    Button updButton = new Button("Update");
    updButton.addClickListener(e -> {
      List<WeatherStateData> weatherStateDataList = returnOnlyMissingValues(requestHttpUpdate.requestUpdate(modelBean.getModel().getLocation().getId()));
      if (weatherStateDataList == null) {
        EToast.ERROR.show("Http update error", "could not communicate to data sever");
        return;
      }
      if (weatherStateDataList.isEmpty())
        EToast.SUCCESS.show("Everything is up-to-date", "nothing to update");

      createNewDataWindow(updButton, weatherStateDataList, false);
    });

    return updButton;
  }

  private List<WeatherStateData> returnOnlyMissingValues(List<WeatherStateData> weatherStateDataList) {
    return weatherStateDataList.stream().filter(e -> isDataMissing(rowsData, e)).collect(Collectors.toList());
  }

  private boolean isDataMissing(List<WeatherStateData> entries, WeatherStateData stateData) {
    return entries.stream().noneMatch(e -> e.asDayUnit().equals(stateData.asDayUnit()));
  }

  private Upload createUploadButton() {
    CSVFileReceiver fileReceiver = new CSVFileReceiver();

    fileReceiver.setDateScope(DateUtils.asSqlDate(new Date(107, Calendar.JANUARY, 2))); //TODO

    Upload uploadBtn = new Upload(null, fileReceiver);
    uploadBtn.setImmediateMode(false);
    uploadBtn.setButtonCaption("Upload File");

    UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(uploadBtn, fileReceiver);

    uploadBtn.addStartedListener(event -> {
      if ("".equals(event.getFilename()) || event.getContentLength() == 0)
        return;
      fileReceiver.reset();
      if (uploadInfoWindow.getParent() == null) {
        UI.getCurrent().addWindow(uploadInfoWindow);
      }
      uploadInfoWindow.setClosable(false);
    });
    uploadBtn.addFinishedListener(event -> {
      uploadInfoWindow.setClosable(true);
      uploadInfoWindow.setShowResultsAction(e -> createNewDataWindow(uploadInfoWindow, returnOnlyMissingValues(uploadInfoWindow.getResults()), true));
    });
    return uploadBtn;
  }

  private Button createCalculateButton() {
    return new Button("Calculate", e -> {
      try {
        calculate();
      } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
      }
    });
  }

  private Button createSaveButton() {
    return new Button("Save results", e -> {
      try {
        calculate();
      } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
        return;
      }
      saveModel();
    });
  }

  private void saveModel() {
    if (method.getTargetType() == ETargetValues.SOLAR) {
      modelBean.getModel().setGhiForecast(method.getNearestForecast().asDouble());
      modelBean.getModel().setMseSolar(new BigDecimal(tfMSE.getValue()).setScale(5, BigDecimal.ROUND_HALF_UP));
    } else {
      modelBean.getModel().setWindSpeedForecast(method.getNearestForecast().asDouble());
      modelBean.getModel().setMseWind(new BigDecimal(tfMSE.getValue()).setScale(5, BigDecimal.ROUND_HALF_UP));
    }
    modelBean.getModel().setAlpha(new BigDecimal(lhCoefficients.getAlpha()));
    modelBean.getModel().setBeta(new BigDecimal(lhCoefficients.getBeta()));
    modelBean.getModel().setGamma(new BigDecimal(lhCoefficients.getGamma()));
    modelBean.saveModel();

    EToast.SUCCESS.show("Success", "Model params are saved");
  }

  private void createNewDataWindow(com.vaadin.ui.Component src, List<WeatherStateData> data, boolean displayWindColumn) {
    src.getUI().getWindows().stream().filter(w -> w instanceof MWindow).forEach(Window::close);
    MWindow window = new MWindow("New Data")
        .withCenter()
        .withClosable(true)
        .withHeight(60, Sizeable.Unit.PERCENTAGE)
        .withWidth(60, Sizeable.Unit.PERCENTAGE);

    MGrid<WeatherStateData> grid = new MGrid<>(WeatherStateData.class)
        .withProperties("date", "ghi", "ebh", "dni", "dhi", "cloudOpacity", "windSpeed")
        .withColumnHeaders("date", "ghi", "ebh", "dni", "dhi", "cloudOpacity", "windSpeed")
        .withWidth(90, Sizeable.Unit.PERCENTAGE)
        .withHeight(90, Sizeable.Unit.PERCENTAGE);

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
      updateDataGrid();
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

    tfMSEPerc.setValue(df.format(result.getMsePerc()));
    tfRMSE.setValue(df.format(result.getRmse()));
    tfMSE.setValue(df.format(result.getMse()));
    tfMAE.setValue(df.format(result.getMae()));

    lhCoefficients.setAlpha(result.getAlpha());
    lhCoefficients.setBeta(result.getBeta());
    lhCoefficients.setGamma(result.getGamma());

    redrawChart();
  }

  private void updateDataGrid() {
    List<WeatherStateData> dataList = modelBean.getEntries().stream().peek(
        e -> e.setWindSpeed(Double.parseDouble(df.format(e.getWindSpeed())))
    ).collect(Collectors.toList());

    this.rowsData = dataList;
    entriesGrid.setRows(dataList);
    enableModelButtons(!dataList.isEmpty());
  }

  private void enableModelButtons(boolean enabled) {
    btnCalculate.setEnabled(enabled);
    btnSaveResults.setEnabled(enabled);
  }

  private void redrawChart() {
    this.chartWrapper.removeAllComponents();
    ModelChart components = new ModelChart(method);
    components.setSizeFull();
    this.chartWrapper.add(components).setSizeFull();
  }


}
