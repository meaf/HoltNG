package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.calculations.Forecasting;
import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.calculations.Result;
import com.meaf.apeps.input.csv.CSVFileReciever;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.ProjectBean;
import com.meaf.apeps.view.beans.StateBean;
import com.meaf.apeps.view.components.CoefficientsBar;
import com.meaf.apeps.view.components.ModelChart;
import com.meaf.apeps.view.components.UploadInfoWindow;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.math.BigDecimal;
import java.rmi.NoSuchObjectException;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.DoubleStream;

@Component
public
class ModelContent extends ABaseContent {

  private final ModelBean modelBean;
  private final ProjectBean projectBean;
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
  private boolean solarStats;

  public ModelContent(ModelBean modelBean, ProjectBean projectBean) {
    this.modelBean = modelBean;
    this.projectBean = projectBean;
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

    MHorizontalLayout lhErrorBar = new MHorizontalLayout(tfMAE, tfMSEPerc, tfMSE, tfRMSE);
    lhErrorBar.setCaption("Average node MSE");

    ModelChart lineChart = new ModelChart(method);
    chartWrapper.removeAllComponents();
    chartWrapper.add(lineChart);

    MVerticalLayout calculations = new MVerticalLayout(
        lhCoefficients,
        chartWrapper,
        lhErrorBar
    ).withFullWidth();

    MHorizontalLayout data = new MHorizontalLayout(
        entriesGrid,
        calculations
    ).withFullWidth();

    data.setExpandRatio(entriesGrid, 1);
    data.setExpandRatio(calculations, 4);

    DisclosurePanel aboutBox = new DisclosurePanel(modelBean.getModel().getName(), new RichText(modelBean.getModel().getDescription()));
    MVerticalLayout content = new MVerticalLayout(
        aboutBox,
        data
    );
    addUploadBtn(content);
    listData();
    return content;
  }

  private void addUploadBtn(MVerticalLayout content) {
    CSVFileReciever fileReciever= new CSVFileReciever();

    fileReciever.setDateScope(DateUtils.asSqlDate(new Date(107, Calendar.JANUARY, 2))); //TODO

    Upload uploadBtn = new Upload(null, fileReciever);
    uploadBtn.setImmediateMode(false);
    uploadBtn.setButtonCaption("Upload File");

    UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(uploadBtn, fileReciever);

    uploadBtn.addStartedListener(event -> {
      fileReciever.reset();
      if (uploadInfoWindow.getParent() == null) {
        UI.getCurrent().addWindow(uploadInfoWindow);
      }
      uploadInfoWindow.setClosable(false);
    });
    uploadBtn.addFinishedListener(event -> uploadInfoWindow.setClosable(true));
    content.add(uploadBtn);
  }



  private Button createCalculateButton() {
    return new Button("Calculate", e -> {
      changeState(StateBean.EState.Login);
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

      Notification notification=new Notification("Success",
          "Model params are saved");
      notification.setPosition(Position.TOP_RIGHT);
      notification.setStyleName("");
      notification.show(Page.getCurrent());
    });
  }

  private Result calculate() {
    double[] inputData = modelBean.getEntries().stream().map(getDataTypeMapper())
        .flatMapToDouble(value -> DoubleStream.of(value.doubleValue()))
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
    return solarStats
        ? WeatherStateData::getGhi
        : WeatherStateData::getWindSpeed;
  }

  private void initFakeData() throws NoSuchObjectException {
    projectBean.switchProject(1L);
    modelBean.switchModel(1L);
  }


  private void listData() {
    entriesGrid.setRows(modelBean.getEntries());
  }

  private void redrawChart() {
    this.chartWrapper.removeAllComponents();
    ModelChart components = new ModelChart(method);
    components.setSizeFull();
    this.chartWrapper.add(components).setSizeFull();
  }


}
