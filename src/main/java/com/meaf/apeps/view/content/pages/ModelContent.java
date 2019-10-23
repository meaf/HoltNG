package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.calculations.Forecasting;
import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.calculations.Result;
import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.csv.CSVFileReciever;
import com.meaf.apeps.input.http.RequestHttpUpdate;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.DoubleStream;

@Component
public
class ModelContent extends ABaseContent {

  private final ModelBean modelBean;
  private final ProjectBean projectBean;
  private final RequestHttpUpdate requestHttpUpdate;

  private Forecasting method;

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
  private MCheckBox cxSolarStats = new MCheckBox("Solar energy");
  private MCheckBox cxGroupMonthly = new MCheckBox("Group monthly");

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

    lhCoefficients = new CoefficientsBar(modelBean.getModel());
    lhCoefficients.add(btnCalculate);
    lhCoefficients.add(btnSaveResults);
    lhCoefficients.setComponentAlignment(btnCalculate, Alignment.BOTTOM_LEFT);
    lhCoefficients.setComponentAlignment(btnSaveResults, Alignment.BOTTOM_LEFT);

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

    data.setExpandRatio(dataTableOptions, 1);
    data.setExpandRatio(calculations, 4);

    DisclosurePanel aboutBox = new DisclosurePanel(modelBean.getModel().getName(), new RichText(modelBean.getModel().getDescription()));
    MVerticalLayout content = new MVerticalLayout(
        aboutBox,
        data
    );
    updateDataGrid();
    return content;
  }

  private Button createUpdateButton() {
    Button updButton = new Button("Update");
    updButton.addClickListener(e -> {
      List<WeatherStateData> weatherStateDataList = requestHttpUpdate.requestUpdate();
      if (weatherStateDataList == null) {
        showErrorNotification("Http update error", "could not communicate to data sever");
        return;
      }
      createNewDataWindow(updButton, weatherStateDataList, false);
    });

    return updButton;
  }

  private Upload createUploadButton() {
    CSVFileReciever fileReciever = new CSVFileReciever();

    fileReciever.setDateScope(DateUtils.asSqlDate(new Date(107, Calendar.JANUARY, 2))); //TODO

    Upload uploadBtn = new Upload(null, fileReciever);
    uploadBtn.setImmediateMode(false);
    uploadBtn.setButtonCaption("Upload File");

    UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(uploadBtn, fileReciever);

    uploadBtn.addStartedListener(event -> {
      if("".equals(event.getFilename()) || event.getContentLength() == 0)
        return;
      fileReciever.reset();
      if (uploadInfoWindow.getParent() == null) {
        UI.getCurrent().addWindow(uploadInfoWindow);
      }
      uploadInfoWindow.setClosable(false);
    });
    uploadBtn.addFinishedListener(event -> {
      uploadInfoWindow.setClosable(true);
      uploadInfoWindow.setShowResultsAction(e -> createNewDataWindow(uploadInfoWindow, uploadInfoWindow.getResults(), false));
    });
    return uploadBtn;
  }

  private Button createCalculateButton() {
    return new Button("Calculate", e -> {
//      changeState(StateBean.EState.Login);
      Result result = calculate();

      tfMSEPerc.setValue(String.valueOf(result.getMsePerc()));
      tfRMSE.setValue(String.valueOf(result.getRmse()));
      tfMSE.setValue(String.valueOf(result.getMse()));
      tfMAE.setValue(String.valueOf(result.getMae()));

      lhCoefficients.setAlpha(result.getAlpha());
      lhCoefficients.setBeta(result.getBeta());
      lhCoefficients.setGamma(result.getGamma());
    });
  }

  private Button createSaveButton() {
    return new Button("Save results", e -> {
      Result result = calculate();

      tfMSE.setValue(String.valueOf(result.getMse()));
      modelBean.getModel().setAlpha(new BigDecimal(lhCoefficients.getAlpha()));
      modelBean.getModel().setBeta(new BigDecimal(lhCoefficients.getBeta()));
      modelBean.getModel().setGamma(new BigDecimal(lhCoefficients.getGamma()));
      modelBean.getModel().setMse(new BigDecimal(result.getMse()));
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

    if(!displayWindColumn)
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

  private Result calculate() {
    double[] inputData =
        (cxGroupMonthly.getValue()
            ? WeatherAggregator.dailyToMonthy(modelBean.getEntries())
            : modelBean.getEntries())
        .stream().map(getDataTypeMapper())
        .flatMapToDouble(value -> DoubleStream.of(value.doubleValue()))
//        .limit(50)
        .toArray();

    method = new HoltWinters();
    method.calculate(inputData,
        lhCoefficients.getAlpha(),
        lhCoefficients.getBeta(),
        lhCoefficients.getGamma(),
        lhCoefficients.getPeriod(),
        lhCoefficients.getForecastPoints());

    redrawChart();
    return method.getOptimalResult();
  }

  private Function<WeatherStateData, Number> getDataTypeMapper() {
    return cxSolarStats.getValue()
        ? WeatherStateData::getGhi
        : WeatherStateData::getWindSpeed;
  }

  private void initFakeData() throws NoSuchObjectException {
    projectBean.switchProject(1L);
    modelBean.switchModel(1L);
  }


  private void updateDataGrid() {
    entriesGrid.setRows(modelBean.getEntries());
  }

  private void redrawChart() {
    this.chartWrapper.removeAllComponents();
    ModelChart components = new ModelChart(method);
    components.setSizeFull();
    this.chartWrapper.add(components).setSizeFull();
  }


}
