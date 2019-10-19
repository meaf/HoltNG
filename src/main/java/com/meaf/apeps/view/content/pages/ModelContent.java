package com.meaf.apeps.view.content.pages;

import com.meaf.apeps.calculations.Forecasting;
import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.calculations.Result;
import com.meaf.apeps.model.entity.DataEntry;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.ProjectBean;
import com.meaf.apeps.view.components.CoefficientsBar;
import com.meaf.apeps.view.components.ModelChart;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

@Component
public
class ModelContent extends ABaseContent {

  @Autowired
  private ModelBean modelBean;
  @Autowired
  private ProjectBean projectBean;
  private Forecasting method;

  private MGrid<DataEntry> entriesGrid = new MGrid<>(DataEntry.class)
      .withProperties("date", "value")
      .withColumnHeaders("date", "value")
      .withWidth(100, Sizeable.Unit.PERCENTAGE)
      .withHeight(100, Sizeable.Unit.PERCENTAGE);
  private CoefficientsBar lhCoefficients;
  private TextField tfMSEPerc = new TextField();
  private TextField tfRMSE = new TextField();
  private TextField tfMSE = new TextField();
  private TextField tfMAE = new TextField();
  MHorizontalLayout chartWrapper = new MHorizontalLayout();

  @Override
  public com.vaadin.ui.Component getContent() {
    initFakeData();

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
    listData();
    return content;
  }


  private Button createCalculateButton() {
    return new Button("Calculate", e -> {
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
    double[] inputData = modelBean.getEntries().stream()
        .flatMapToDouble(e -> DoubleStream.of(e.getValue().doubleValue()))
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

  private void initFakeData() {
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
