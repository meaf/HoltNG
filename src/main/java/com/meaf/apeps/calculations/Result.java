package com.meaf.apeps.calculations;

public class Result {
  private final double alpha, beta, gamma;
  private final double mae, mse, rmse, msePerc;
  private final double optimalFitMAE;

  Result(double alpha, double beta, double gamma, double mae, double mse, double msePerc, double optimalFitMAE) {
    this.alpha = alpha; //коэффициент сглаживания ряда
    this.beta = beta; //коэффициент сглаживания тренда
    this.gamma = gamma; //коэффициент сглаживания сезонности
    this.mae = mae;
    this.mse = mse;
    this.msePerc = msePerc;
    this.rmse = Math.sqrt(mse);
    double fitMAE = (1 - optimalFitMAE) * 100;
    this.optimalFitMAE = fitMAE > 0 ? fitMAE : 0;
  }

  public double getAlpha() {
    return alpha;
  }

  public double getBeta() {
    return beta;
  }

  public double getGamma() {
    return gamma;
  }

  public double getMse() {
    return mse;
  }

  public double getMae() {
    return mae;
  }

  public double getRmse() {
    return rmse;
  }

  public double getMsePerc() {
    return msePerc;
  }

  public double getOptimalFitMAE() {
    return optimalFitMAE;
  }
}

