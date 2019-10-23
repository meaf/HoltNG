package com.meaf.apeps.calculations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HoltWinters implements Forecasting {

  private Result optimalResult;
  private double[]
      S,  // заданные значения
      Lt, //экспоненциально сглаженный ряд
      Tt, //значение тренда
      Sts, // коэффициент сезонности предыдущего периода;
      Fc, // Прогноз по методу Хольта
      FcEstimated, // Проноз для оценки модели
      ErrorPerc, // Ошибка модели
      ErrorMSE, // среднеквадратическая ошибка
      ErrorMAE, // абсолютная ошибка
      Err; //Отклонение ошибки модели от прогнозной модели

  private double msePerc = -1; //среднеквадратическая ошибка модели(%)
  private double mseAvg = -1;  //среднеквадратическая ошибка модели
  private double maeAvg = -1;  //средняя абсолютная ошибка модели

  public void calculate(double[] stats, Double alpha, Double betta, Double gamma, int period, int forecastLen){
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
    double optimalMAE = Double.MAX_VALUE;
    double optimalMSEPerc = Double.MAX_VALUE;

    for (double a = aLowerLimit; a <= aUpperLimit; a += step) {
      for (double b = bLowerLimit; b <= bUpperLimit; b += step) {
        for (double c = cLowerLimit; c <= cUpperLimit; c += step) {
          forecast(stats, a, b, c, period, forecastLen);
          double newMSE = mseAvg;
          if (optimalMSE > newMSE) {
            optimalA = a;
            optimalB = b;
            optimalC = c;
            optimalMAE = maeAvg;
            optimalMSE = mseAvg;
            optimalMSEPerc = msePerc;
          }
        }
      }
    }
    optimalResult = new Result(optimalA, optimalB, optimalC, optimalMAE, optimalMSE, optimalMSEPerc);
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
      calculateStep(alpha, beta, gamma, period, i);

    for (int i = S.length; i < S.length + forecastLen; i++)
      calculateForecastStep(period, i);

    calculateError();
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
    ErrorMSE = new double[S.length];
    ErrorMAE = new double[S.length];

    Lt[0] = S[0];
    Tt[0] = 0;
    Sts[0] = 1;
    Fc[0] = 0;
    Err[0] = 0;
    FcEstimated[0] = Lt[0];
    ErrorPerc[0] = 0;
  }

  private void calculateStep(double alpha, double beta, double gamma, int period, int i) {
    Lt[i] = alpha * S[i] / getSeasonalK(i, period) + ((1 - alpha) * (Lt[i - 1] + Tt[i - 1]));
    Tt[i] = beta * (Lt[i] - Lt[i - 1]) + ((1 - beta) * Tt[i - 1]);
    Sts[i] = i <= period
        ? 1
        : gamma * S[i] / Lt[i] + ((1 - gamma) * getSeasonalK(i, period));

    FcEstimated[i] = (Lt[i - 1] + Tt[i - 1]) * getSeasonalK(i, period);
    Err[i] = S[i] - FcEstimated[i];
    ErrorMAE[i] = Math.abs(Err[i]);
    ErrorMSE[i] = (Err[i] * Err[i]);
    ErrorPerc[i] = ErrorMSE[i] / (S[i] * S[i]);
  }

  private void calculateForecastStep(int period, int i) {
    int p = i - S.length + 1;
    int lastSequenceValueInd = S.length - 1;
    Fc[p - 1] = (Lt[lastSequenceValueInd] + Tt[lastSequenceValueInd] * p) * Sts[i - period];
  }

  private double getSeasonalK(int i, int period) {
    return i < period ? 1 : Sts[i - period];
  }

  private void calculateError(){
    this.maeAvg = Arrays.stream(ErrorMAE).reduce(Double::sum).orElse(-1) / S.length;
    this.mseAvg = Arrays.stream(ErrorMSE).reduce(Double::sum).orElse(-1) / S.length;
    this.msePerc = Arrays.stream(ErrorPerc).reduce(Double::sum).orElse(-1) / S.length;
  }

}
