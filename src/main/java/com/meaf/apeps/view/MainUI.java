package com.meaf.apeps.view;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.model.entity.DataEntry;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.ProjectBean;
import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.components.BasicLine;
import com.meaf.apeps.view.components.CoefficientsBar;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

@Title("HoltNG")
@Theme("valo")
@SpringUI
public class MainUI extends UI {

  private static final long serialVersionUID = 1L;

  @Autowired
  private SessionBean sessionBean;
  @Autowired
  private ModelBean modelBean;
  @Autowired
  private ProjectBean projectBean;
  private HoltWinters method;

  private MGrid<DataEntry> entriesGrid = new MGrid<>(DataEntry.class)
      .withProperties("date", "value")
      .withColumnHeaders("date", "value")
      .withWidth(100, Unit.PERCENTAGE)
      .withHeight(100, Unit.PERCENTAGE);
  private CoefficientsBar lhCoefficients;
  private TextField tfMSE = new TextField();
  MHorizontalLayout chartWrapper = new MHorizontalLayout();

  @Override
  protected void init(VaadinRequest request) {

    //    showLoginPrompt();
    initFakeData();

    lhCoefficients = new CoefficientsBar(modelBean.getModel());
    tfMSE.setPlaceholder("Press calculate");
    tfMSE.setEnabled(false);

    Button btnCalculate = createCalculateButton();
    Button btnSaveResults = createSaveButton();
    MHorizontalLayout lhErrorBar = new MHorizontalLayout(btnCalculate, tfMSE, btnSaveResults);
    lhErrorBar.setCaption("Average node MSE");

    BasicLine lineChart = new BasicLine(method);
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

    setContent(content);
    listData();
  }

  private Button createCalculateButton() {
    return new Button("Calculate", e -> {
      HoltWinters.Result result = calculate();

      tfMSE.setValue(String.valueOf(result.getMse()));
      lhCoefficients.setAlpha(result.getAlpha());
      lhCoefficients.setBeta(result.getBeta());
      lhCoefficients.setGamma(result.getGamma());
    });
  }

  private Button createSaveButton() {
    return new Button("Save results", e -> {
      HoltWinters.Result result = calculate();

      tfMSE.setValue(String.valueOf(result.getMse()));
      modelBean.getModel().setAlpha(new BigDecimal(lhCoefficients.getAlpha()));
      modelBean.getModel().setBeta(new BigDecimal(lhCoefficients.getBeta()));
      modelBean.getModel().setGamma(new BigDecimal(lhCoefficients.getGamma()));
      modelBean.getModel().setMse(new BigDecimal(result.getMse()));
      modelBean.saveModel();
    });
  }

  private HoltWinters.Result calculate() {
    double[] inputData = modelBean.getEntries().stream()
        .flatMapToDouble(e -> DoubleStream.of(e.getValue().doubleValue()))
        .toArray();

    method = new HoltWinters();
    method.calculate(inputData,
        lhCoefficients.getAlpha(),
        lhCoefficients.getBeta(),
        lhCoefficients.getGamma(),
        12,
        2);

    redrawChart();
    return method.getOptimalResult();
  }

  private void initFakeData() {
    projectBean.switchProject(1L);
    modelBean.switchModel(1L);
  }

  private void showLoginPrompt() {
    if (sessionBean.isUserLoggedIn()) {
      LoginForm component = new LoginForm();
      component.addLoginListener(e -> {
        boolean isAuthenticated = sessionBean.authenticate(e);
        if (!isAuthenticated) {
//                component.set`(true);
        }
      });
    }
  }

  private void listData() {
    entriesGrid.setRows(modelBean.getEntries());
  }

  private void redrawChart(){
    this.chartWrapper.removeAllComponents();
    BasicLine components = new BasicLine(method);
    components.setSizeFull();
    this.chartWrapper.add(components).setSizeFull();
  }
}
