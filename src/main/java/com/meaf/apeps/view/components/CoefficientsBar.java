package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Model;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CoefficientsBar extends MHorizontalLayout {
  private TextField tfAlpha;
  private TextField tfBeta;
  private TextField tfGamma;

  private static DecimalFormat df = new DecimalFormat("#.##");

  public CoefficientsBar() {
    super();
  }

  public CoefficientsBar(Model model) {
    super();
    this.tfAlpha = new TextField("alpha", defEmpty(model.getAlpha()));
    this.tfBeta = new TextField("beta", defEmpty(model.getBeta()));
    this.tfGamma = new TextField("gamma", defEmpty(model.getGamma()));
    this.add(tfAlpha, tfBeta, tfGamma);
  }

  private String defEmpty(BigDecimal k){
    return k == null ? "" : k.toString();
  }

  public Double getAlpha() {
    return nullableDouble(tfAlpha.getValue());
  }

  private Double nullableDouble(String value) {
    return (value == null || "".equals(value))
        ? null
        : Double.parseDouble(value);
  }

  public Double getBeta() {
    return nullableDouble(tfBeta.getValue());
  }

  public Double getGamma() {
    return nullableDouble(tfGamma.getValue());
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
}
