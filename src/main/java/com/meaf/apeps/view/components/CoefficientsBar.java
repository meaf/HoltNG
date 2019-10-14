package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Model;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;

public class CoefficientsBar extends MHorizontalLayout {
  private TextField tfAlpha;
  private TextField tfBeta;
  private TextField tfGamma;
  private TextField tfPeriod;
  private TextField tfFC;

  private static DecimalFormat df = new DecimalFormat("#.##");

  public CoefficientsBar() {
    super();
  }

  public CoefficientsBar(Model model) {
    super();
    this.tfAlpha = new TextField("Alpha", defEmpty(model.getAlpha()));
    this.tfBeta = new TextField("Beta", defEmpty(model.getBeta()));
    this.tfGamma = new TextField("Gamma", defEmpty(model.getGamma()));
    this.tfPeriod = new TextField("Period", defEmpty(model.getPeriod()));
    this.tfFC = new TextField("Forecast points", "3");
    this.add(tfAlpha, tfBeta, tfGamma, tfPeriod, tfFC);
  }

  public Double getAlpha() {
    return nullableDouble(tfAlpha.getValue());
  }

  public Double getBeta() {
    return nullableDouble(tfBeta.getValue());
  }

  public Double getGamma() {
    return nullableDouble(tfGamma.getValue());
  }

  public Integer getPeriod() {
    return nullableInteger(tfPeriod.getValue());
  }

  public Integer getForecastPoints() {
    return nullableInteger(tfFC.getValue());
  }

  public void setAlpha(@NotNull Double alpha) {
    this.tfAlpha.setValue(df.format(alpha));
  }

  public void setBeta(@NotNull Double beta) {
    this.tfBeta.setValue(df.format(beta));
  }

  public void setGamma(@NotNull Double gamma) {
    this.tfGamma.setValue(df.format(gamma));
  }

  private Double nullableDouble(String value) {
    return (value == null || "".equals(value))
        ? null
        : Double.parseDouble(value);
  }

  private Integer nullableInteger(String value) {
    return (value == null || "".equals(value))
        ? null
        : Integer.parseInt(value);
  }

  private String defEmpty(Number k){
    return k == null ? "" : k.toString();
  }

}
