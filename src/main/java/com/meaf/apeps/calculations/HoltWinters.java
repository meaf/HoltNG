package com.meaf.apeps.calculations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HoltWinters {

  private Result optimalResult;
  private double[]
      S,  // заданные значения
      Lt, //экспоненциально сглаженный ряд
      Tt, //значение тренда
      Sts, // коэффициент сезонности предыдущего периода;
      Fc, // Прогноз по методу Хольта
      FcEstimated, // Проноз для оценки модели
      ErrorPerc, // Ошибка модели
      Err; //Отклонение ошибки модели от прогнозной модели

  private double mse = 100;  //среднеквадратическая ошибка модели

  public void calculate(double[] stats, Double alpha, Double betta, Double gamma, double period, double forecastLen){
    double aLowerLimit = alpha == null ? 0 : alpha;
    double aUpperLimit = alpha == null ? 1 : alpha;

    double bLowerLimit = betta == null ? 0 : betta;
    double bUpperLimit = betta == null ? 1 : betta;

    double cLowerLimit = gamma == null ? 0 : gamma;
    double cUpperLimit = gamma == null ? 1 : gamma;

    double step = 0.1;

    double optimalA = 0;
    double optimalB = 0;
    double optimalC = 0;
    double optimalMSE = Double.MAX_VALUE;

    for (double a = aLowerLimit; a <= aUpperLimit; a += step) {
      for (double b = bLowerLimit; b <= bUpperLimit; b += step) {
        for (double c = cLowerLimit; c <= cUpperLimit; c += step) {
          forecast(stats, a, b, c, 12, 10);
          double newMSE = mse;
          if (optimalMSE > newMSE) {
            optimalA = a;
            optimalB = b;
            optimalC = c;
            optimalMSE = newMSE;
          }
        }
      }
    }
    optimalResult = new Result(optimalA, optimalB, optimalC, optimalMSE);
  }

  public Result getOptimalResult() {
    return optimalResult;
  }

  public List<Double> getInputData() {
    return Arrays.stream(S).boxed().collect(Collectors.toList());
  }

  public List<Double> getSmoothedData() {
    return Arrays.stream(Lt).boxed().collect(Collectors.toList());
  }

  public List<Double> getFcData() {
    List<Double> result = Arrays.stream(FcEstimated).boxed().collect(Collectors.toList());
    result.addAll(Arrays.stream(Fc).boxed().collect(Collectors.toList()));
    return result;
  }

  private void forecast(double[] inputArray,
                        double alpha,
                        double beta,
                        double gamma,
                        int period,
                        int forecastLen) {

    S = inputArray;
    initValues(forecastLen);

    for (int i = 1; i < S.length; i++)
      calculateStep(alpha, beta, gamma, forecastLen, i);

    for (int i = S.length; i < S.length + forecastLen; i++)
      calculateForecastStep(period, i);

    calculateMSE();
  }

  private void initValues(int forecastLen) {
    int resultedLen = S.length + forecastLen;
    Fc = new double[forecastLen];

    Lt = new double[S.length];
    Tt = new double[S.length];
    Sts = new double[S.length];
    Err = new double[S.length];
    FcEstimated = new double[S.length];
    ErrorPerc = new double[S.length];

    Lt[0] = S[0];
    Tt[0] = 0;
    Sts[0] = 1;
    Fc[0] = 0;
    Err[0] = 0;
    FcEstimated[0] = Lt[0];
    ErrorPerc[0] = 0;
  }

  private void calculateStep(double alpha, double beta, double gamma, int forecastLen, int i) {
    Lt[i] = (alpha * S[i] / getSeasonalK(i, forecastLen)) + (1 - alpha) * (Lt[i - 1] + Tt[i - 1]);
    Tt[i] = beta * (Lt[i] - Lt[i - 1]) + (1 - beta) * Tt[i - 1];
    Sts[i] =
        i < forecastLen ? 1 : gamma * (S[i] / Lt[i]) + (1 - gamma) * getSeasonalK(i, forecastLen);

    FcEstimated[i] = (Lt[i - 1] + Tt[i - 1]) * getSeasonalK(i, forecastLen);
    Err[i] = S[i] - FcEstimated[i];
    ErrorPerc[i] = (Err[i] * Err[i]) / (S[i] * S[i]);
  }

  private void calculateForecastStep(int period, int i) {
    int p = i - S.length + 1;
    int lastSequenceValueInd = S.length - 1;
    Fc[p - 1] = (Lt[lastSequenceValueInd] + Tt[lastSequenceValueInd] * p) * Sts[i - period];
  }

  private double getSeasonalK(int i, int forecastLen) {
    return i < forecastLen ? 1 : Sts[i - forecastLen];
  }

  private void calculateMSE(){
    this.mse = Arrays.stream(ErrorPerc).reduce(Double::sum).orElse(-1) / S.length;
  }

  public static class Result {
    public Result(double alpha, double beta, double gamma, double error) {
      this.alpha = alpha; //коэффициент сглаживания ряда
      this.beta = beta; //коэффициент сглаживания тренда
      this.gamma = gamma; //коэффициент сглаживания сезонности
      this.mse = error;
    }

    final double alpha, beta, gamma;
    final double mse;

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
  }

}
