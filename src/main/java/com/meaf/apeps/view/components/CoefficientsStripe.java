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
    return Double.parseDouble(tfAlpha.getValue());
  }

  public Double getBeta() {
    return Double.parseDouble(tfBeta.getValue());
  }

  public Double getGamma() {
    return Double.parseDouble(tfGamma.getValue());
  }

}
