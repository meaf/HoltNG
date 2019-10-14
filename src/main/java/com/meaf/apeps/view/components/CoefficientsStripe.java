package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Model;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.math.BigDecimal;

public class CoefficientsStripe extends MHorizontalLayout {
  private TextField tfAlpha;
  private TextField tfBeta;
  private TextField tfGamma;

  public CoefficientsStripe() {
    super();
  }

  public CoefficientsStripe(Model model) {
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

  public void setAlpha(Double alpha) {
    this.tfAlpha.setValue(alpha.toString());
  }

  public void setBeta(Double beta) {
    this.tfBeta.setValue(beta.toString());
  }

  public void setGamma(Double gamma) {
    this.tfGamma.setValue(gamma.toString());
  }
}
