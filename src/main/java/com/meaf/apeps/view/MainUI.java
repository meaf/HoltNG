package com.meaf.apeps.view;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.model.entity.DataEntry;
import com.meaf.apeps.view.beans.ModelBean;
import com.meaf.apeps.view.beans.ProjectBean;
import com.meaf.apeps.view.beans.SessionBean;
import com.meaf.apeps.view.components.CoefficientsStripe;
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
  private CoefficientsStripe coefficients;

  @Override
  protected void init(VaadinRequest request) {

    //    showLoginPrompt();
    initFakeData();

    DisclosurePanel aboutBox = new DisclosurePanel("Abut model", new RichText("Hello"));

    coefficients = new CoefficientsStripe(modelBean.getModel());

    TextField accuraccy = new TextField("Accuraccy", "0");
    accuraccy.setReadOnly(true);

    Button btnCalculate = new Button("Calculate", e -> accuraccy.setValue(String.valueOf(calculate())));

    MVerticalLayout calculations = new MVerticalLayout(
        coefficients,
        new Label("CAHRTS GONNA BE HERE"),
        accuraccy,
        btnCalculate
    );

    calculations.setWidth(60, Unit.PERCENTAGE);

    MHorizontalLayout data = new MHorizontalLayout(
        entriesGrid,
        calculations
    );


    MVerticalLayout content = new MVerticalLayout(
        aboutBox,
        data
    ).expand(entriesGrid);

    setContent(content);
    listData();
  }

  private double calculate() {
    double[] inputData = modelBean.getEntries().stream()
        .flatMapToDouble(e -> DoubleStream.of(e.getValue().doubleValue()))
        .toArray();

    double accuracy = new HoltWinters().forecast(inputData,
        coefficients.getAlpha(),
        coefficients.getBeta(),
        coefficients.getGamma(),
        12,
        2);

    return accuracy;

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
