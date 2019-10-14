package com.meaf.apeps.view;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.model.entity.DataEntry;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.ProjectBean;
import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.components.CoefficientsBar;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
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
  SessionBean sessionBean;
  @Autowired
  ModelBean modelBean;
  @Autowired
  ProjectBean projectBean;

  private MGrid<DataEntry> entriesGrid = new MGrid<>(DataEntry.class)
      .withProperties("date", "value")
      .withColumnHeaders("date", "value")
      .withWidth(20, Unit.PERCENTAGE);
  private CoefficientsBar lhCoefficients;
  TextField tfMSE = new TextField();

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


    MVerticalLayout calculations = new MVerticalLayout(
        lhCoefficients,
        getCharts(),
        lhErrorBar
    );

    MHorizontalLayout data = new MHorizontalLayout(
        entriesGrid,
        calculations
    );

    DisclosurePanel aboutBox = new DisclosurePanel(modelBean.getModel().getName(), new RichText(modelBean.getModel().getDescription()));
    MVerticalLayout content = new MVerticalLayout(
        aboutBox,
        data
    );

    setContent(content);
    listData();
  }

  private Component getCharts() {




    return new Label("CAHRTS GONNA BE HERE");
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

    HoltWinters method = new HoltWinters();
    method.calculate(inputData,
        lhCoefficients.getAlpha(),
        lhCoefficients.getBeta(),
        lhCoefficients.getGamma(),
        12,
        2);

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

}
