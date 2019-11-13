package com.meaf.apeps.view.components;

import com.meaf.apeps.model.entity.Model;
import com.vaadin.data.HasValue;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.validation.constraints.NotNull;

import static com.meaf.apeps.utils.Formatter.format;

public class CoefficientsBar extends MHorizontalLayout {
  private TextField tfAlpha;
  private TextField tfBeta;
  private TextField tfGamma;
  private TextField tfPeriod;
  private TextField tfFC;

  public CoefficientsBar(Model model, Boolean isSolar) {
    super();
    this.tfAlpha = new TextField("α, level smoothing", defEmpty(isSolar ? model.getAlpha_s() : model.getAlpha_w()));
    this.tfBeta = new TextField("β, trend smoothing", defEmpty(isSolar ? model.getBeta_s() : model.getBeta_w()));
    this.tfGamma = new TextField("γ, seasonal smoothing", defEmpty(isSolar ? model.getGamma_s() : model.getGamma_w()));
    this.tfPeriod = new TextField("period", defEmpty(model.getPeriod()));
    this.tfFC = new TextField("forecast points", "3");
    this.add(tfAlpha, tfBeta, tfGamma, tfPeriod, tfFC);
    addValidators();
  }

  private void addValidators() {
    tfAlpha.addValueChangeListener(this::addCoefficientRestrictions);
    tfBeta.addValueChangeListener(this::addCoefficientRestrictions);
    tfGamma.addValueChangeListener(this::addCoefficientRestrictions);
    tfPeriod.addValueChangeListener(this::addPeriodRestriction);
    tfFC.addValueChangeListener(this::addPeriodRestriction);
  }

  public Double getAlpha() {
    return nullableDouble(tfAlpha.getValue());
  }

  public void setAlpha(@NotNull Double alpha) {
    this.tfAlpha.setValue(format(alpha));
  }

  public Double getBeta() {
    return nullableDouble(tfBeta.getValue());
  }

  public void setBeta(@NotNull Double beta) {
    this.tfBeta.setValue(format(beta));
  }

  public Double getGamma() {
    return nullableDouble(tfGamma.getValue());
  }

  public void setGamma(@NotNull Double gamma) {
    this.tfGamma.setValue(format(gamma));
  }

  public Integer getPeriod() {
    return nullableInteger(tfPeriod.getValue());
  }

  public Integer getForecastPoints() {
    return nullableInteger(tfFC.getValue());
  }

  public boolean isFilled() {
    return !(tfAlpha.getValue().isEmpty() || tfGamma.getValue().isEmpty() || tfGamma.getValue().isEmpty());
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

  private String defEmpty(Number k) {
    return k == null ? "" : k.toString();
  }

  public void reset() {
    this.tfAlpha.clear();
    this.tfBeta.clear();
    this.tfGamma.clear();
  }

  public void addButtons(Button... buttons) {
    for (Button btn : buttons) {
      this.add(btn);
      this.setComponentAlignment(btn, Alignment.BOTTOM_LEFT);
    }
  }

  private void addCoefficientRestrictions(HasValue.ValueChangeEvent<String> event) {
    String newValue = event.getValue();
    TextField textField = (TextField) event.getComponent();
    boolean isInvalid;

    try {
      isInvalid = !newValue.isEmpty() && (parse(newValue) < 0 || parse(newValue) > 1);
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      isInvalid = true;
    }

    textField.setComponentError(isInvalid
        ? new UserError("Value should be in range [0..1]")
        : null
    );
  }

  private void addPeriodRestriction(HasValue.ValueChangeEvent<String> event) {
    TextField tf = (TextField) event.getComponent();
    boolean isInvalidFCAmount;
    boolean isPeriodNotPositive;
    boolean isFCNotPositive;

    try {
      isPeriodNotPositive = tfPeriod.getValue().isEmpty() || parse(tfPeriod) < 1;
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      isPeriodNotPositive = true;
    }
    tf.setComponentError(isPeriodNotPositive
        ? new UserError("Value should be greater than zero")
        : null
    );

    try {
      isInvalidFCAmount = tfFC.getValue().isEmpty() || parse(tfFC) > parse(tfPeriod);
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      isInvalidFCAmount = true;
    }

    try {
      isFCNotPositive = tfFC.getValue().isEmpty() || parse(tfFC) < 1;
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      isFCNotPositive = true;
    }

    tfFC.setComponentError(isInvalidFCAmount
        ? new UserError("Forecast can be only build for less than period duration points")
        : isFCNotPositive
        ? new UserError("Value should be greater than zero")
        : null
    );
  }


  private double parse(TextField field) {
    return parse(field.getValue());
  }

  private double parse(String value) {
    return Double.parseDouble(value);
  }

  public boolean check() {
    return tfFC.getErrorMessage() == null
        && tfAlpha.getErrorMessage() == null
        && tfBeta.getErrorMessage() == null
        && tfGamma.getErrorMessage() == null;
  }
}
