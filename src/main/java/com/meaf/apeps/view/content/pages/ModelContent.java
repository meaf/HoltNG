package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.calculations.Result;
import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.csv.CSVFileReceiver;
import com.meaf.apeps.input.http.RequestHttpUpdate;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import com.meaf.apeps.utils.ETargetValues;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.ProjectBean;
import com.meaf.apeps.view.components.CoefficientsBar;
import com.meaf.apeps.view.components.ModelChart;
import com.meaf.apeps.view.components.UploadInfoWindow;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
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
import java.rmi.NoSuchObjectException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public
class ModelContent extends ABaseContent {

  private static DecimalFormat df = new DecimalFormat("#.####");
  private final ModelBean modelBean;
  private final ProjectBean projectBean;
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

  public ModelContent(ModelBean modelBean, ProjectBean projectBean, RequestHttpUpdate requestHttpUpdate) {
    this.modelBean = modelBean;
    this.projectBean = projectBean;
    this.requestHttpUpdate = requestHttpUpdate;
  }

  @Override
  public com.vaadin.ui.Component getContent() {
    try {
      initFakeData();
    } catch (NoSuchObjectException e) {
      e.printStackTrace();
    }

    Button btnCalculate = createCalculateButton();
    Button btnSaveResults = createSaveButton();
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

    DisclosurePanel aboutBox = new DisclosurePanel(modelBean.getModel().getName(), new RichText(modelBean.getModel().getDescription()));
    MVerticalLayout content = new MVerticalLayout(
        aboutBox,
        data
    );
    updateDataGrid();
    try {
      calculate();
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    }
    return content;
  }

  private Button createClearButton() {
    Button btnClear = new Button("Clear");
    btnClear.addClickListener(e -> lhCoefficients.reset());
    return btnClear;
  }

  private Button createUpdateButton() {
    Button updButton = new Button("Update");
    updButton.addClickListener(e -> {
      List<WeatherStateData> weatherStateDataList = returnOnlyMissingValues(requestHttpUpdate.requestUpdate());
      if (weatherStateDataList == null) {
        showErrorNotification("Http update error", "could not communicate to data sever");
        return;
      }
      if (weatherStateDataList.isEmpty())
        showSuccessNotification("Everything is up-to-date", "nothing to update");

      createNewDataWindow(updButton, weatherStateDataList, false);
    });

    return updButton;
  }

  private List<WeatherStateData> returnOnlyMissingValues(List<WeatherStateData> weatherStateDataList) {
    List<WeatherStateData> entries = modelBean.getEntries();
    return weatherStateDataList.stream().filter(e -> isDataMissing(entries, e)).collect(Collectors.toList());
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
      uploadInfoWindow.setShowResultsAction(e -> createNewDataWindow(uploadInfoWindow, uploadInfoWindow.getResults(), true));
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
      modelBean.getModel().setAlpha(new BigDecimal(lhCoefficients.getAlpha()));
      modelBean.getModel().setBeta(new BigDecimal(lhCoefficients.getBeta()));
      modelBean.getModel().setGamma(new BigDecimal(lhCoefficients.getGamma()));
      modelBean.getModel().setMse(new BigDecimal(tfMSE.getValue()).setScale(5, BigDecimal.ROUND_HALF_UP));
      modelBean.saveModel();

      showSuccessNotification("Success", "Model params are saved");
    });
  }

  private void showSuccessNotification(String title, String descr) {
    Notification notification = new Notification(title, descr);
    notification.setPosition(Position.TOP_RIGHT);
    notification.setStyleName("");
    notification.show(Page.getCurrent());
  }

  private void showErrorNotification(String title, String descr) {
    Notification notification = new Notification(title, descr);
    notification.setPosition(Position.TOP_RIGHT);
    notification.setStyleName("error");
    notification.show(Page.getCurrent());
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
      showSuccessNotification("Merged", data.size() + " entries were added to model");
      updateDataGrid();
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

    if (!lhCoefficients.check()) {
      throw new IllegalArgumentException();
    }

    List<WeatherStateData> dataList = cxGroupMonthly.getValue()
        ? WeatherAggregator.dailyToMonthy(modelBean.getEntries())
        : modelBean.getEntries();

    method = new HoltWinters();
    method.setTargetType(cxSolarStats.getValue() ? ETargetValues.SOLAR : ETargetValues.WIND);
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

  private void initFakeData() throws NoSuchObjectException {
    projectBean.switchProject(1L);
    modelBean.switchModel(1L);
  }


  private void updateDataGrid() {
    entriesGrid.setRows(modelBean.getEntries().stream().peek(
        e -> e.setWindSpeed(Double.parseDouble(df.format(e.getWindSpeed())))
    ).collect(Collectors.toList()));
  }

  private void redrawChart() {
    this.chartWrapper.removeAllComponents();
    ModelChart components = new ModelChart(method);
    components.setSizeFull();
    this.chartWrapper.add(components).setSizeFull();
  }


}
