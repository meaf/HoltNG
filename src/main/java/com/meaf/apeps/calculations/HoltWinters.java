package com.meaf.apeps.calculations;

import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DatedValue;
import com.meaf.apeps.utils.ETargetValues;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class HoltWinters {

  private ETargetValues targetType;
  private Result optimalResult;
  private List<DatedValue> inputData,
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
  private EDateInterval dateInterval;

  public ETargetValues getTargetType() {
    return targetType;
  }

  public void setTargetType(ETargetValues targetType) {
    this.targetType = targetType;
  }

  public void calculate(List<WeatherStateData> stats, Double alpha, Double betta, Double gamma, int period, int forecastLen) {
    inputData = stats.stream().map(s -> new DatedValue(s.getDate(), targetType.mapper.apply(s))).collect(Collectors.toList());
    if (inputData.isEmpty())
      return;
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
          forecast(a, b, c, period, forecastLen);
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

  public double[] extractData(List<WeatherStateData> stats) {
    return stats.stream().map(this.targetType.mapper)
        .flatMapToDouble(value -> DoubleStream.of(value.doubleValue()))
        .toArray();
  }

  public Result getOptimalResult() {
    return optimalResult;
  }

  public List<DatedValue> getInputData() {
    return inputData;
  }

  public List<DatedValue> getSmoothedData() {
    return Lt;
  }

  public List<DatedValue> getFcData() {
    List<DatedValue> result = new ArrayList<>();
    result.addAll(FcEstimated);
    result.addAll(Fc);
    return result;
  }

  public DatedValue getNearestForecast() {
    return Fc.iterator().next();
  }

  private void forecast(double alpha,
                        double beta,
                        double gamma,
                        int period,
                        int forecastLen) {

    S = inputData;
    initValues(forecastLen);

    for (int i = 1; i < S.size(); i++)
      calculateStep(alpha, beta, gamma, period, i);

    for (int i = S.size(); i < S.size() + forecastLen; i++)
      calculateForecastStep(period, i);

    calculateError();
  }

  private void initValues(int forecastLen) {
    int resultedLen = S.size() + forecastLen;
    Fc = new ArrayList<>(forecastLen);

    Lt = new ArrayList<>(S.size());
    Tt = new ArrayList<>(S.size());
    Sts = new ArrayList<>(S.size());
    Err = new ArrayList<>(S.size());
    FcEstimated = new ArrayList<>(S.size());
    ErrorPerc = new ArrayList<>(S.size());
    ErrorMSE = new ArrayList<>(S.size());
    ErrorMAE = new ArrayList<>(S.size());


    Lt.add(0, S.get(0));
    Tt.add(0, new DatedValue(S.get(0).getDate(), 0));
    Sts.add(0, new DatedValue(S.get(0).getDate(), 1));
//    Fc.add(0, new DatedValue(S.get(0).getDate(), 0));
    Err.add(0, new DatedValue(S.get(0).getDate(), 0));
    FcEstimated.add(0, Lt.get(0));
    ErrorPerc.add(0, new DatedValue(S.get(0).getDate(), 0));
    ErrorMAE.add(0, new DatedValue(S.get(0).getDate(), 0));
    ErrorMSE.add(0, new DatedValue(S.get(0).getDate(), 0));
  }

  private void calculateStep(double alpha, double beta, double gamma, int period, int i) {
    Date date = S.get(i).getDate();

    Lt.add(i, new DatedValue(date, alpha * S.get(i).asDouble() / getSeasonalK(i, period) + ((1 - alpha) * (Lt.get(i - 1).asDouble() + Tt.get(i - 1).asDouble()))));
    Tt.add(i, new DatedValue(date, beta * (Lt.get(i).asDouble() - Lt.get(i - 1).asDouble()) + ((1 - beta) * Tt.get(i - 1).asDouble())));
    Sts.add(i, new DatedValue(date, i <= period
        ? 1
        : gamma * S.get(i).asDouble() / Lt.get(i).asDouble() + ((1 - gamma) * getSeasonalK(i, period))));

    FcEstimated.add(i, new DatedValue(date, (Lt.get(i - 1).asDouble() + Tt.get(i - 1).asDouble()) * getSeasonalK(i, period)));
    Err.add(i, new DatedValue(date, S.get(i).asDouble() - FcEstimated.get(i).asDouble()));
    ErrorMAE.add(i, new DatedValue(date, Math.abs(Err.get(i).asDouble())));
    ErrorMSE.add(i, new DatedValue(date, Err.get(i).asDouble() * Err.get(i).asDouble()));
    ErrorPerc.add(i, new DatedValue(date, ErrorMSE.get(i).asDouble() / (S.get(i).asDouble() * S.get(i).asDouble())));
  }

  private void calculateForecastStep(int period, int i) {
    int lastSequenceValueInd = S.size() - 1;
    int fcPointNumber = i - S.size() + 1;
    Date lastDate = S.get(lastSequenceValueInd).getDate();
    LocalDate now = new Date(lastDate.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(fcPointNumber);

    Fc.add(fcPointNumber - 1, new DatedValue(now, (Lt.get(lastSequenceValueInd).asDouble() + Tt.get(lastSequenceValueInd).asDouble() * fcPointNumber) * Sts.get(i - period).asDouble()));
  }

  private double getSeasonalK(int i, int period) {
    return i < period ? 1 : Sts.get(i - period).asDouble();
  }

  private void calculateError() {
    this.maeAvg = ErrorMAE.stream().map(DatedValue::asDouble).reduce(Double::sum).orElse(-1d) / S.size();
    this.mseAvg = ErrorMSE.stream().map(DatedValue::asDouble).reduce(Double::sum).orElse(-1d) / S.size();
    this.msePerc = ErrorPerc.stream().map(DatedValue::asDouble).reduce(Double::sum).orElse(-1d) / S.size();
  }

  public EDateInterval getDateInterval() {
    return dateInterval;
  }

  public void setDateInterval(EDateInterval dateInterval) {
    this.dateInterval = dateInterval;
  }

  public Double getLastActualData() {
    return S.get(S.size() - 1).asDouble();
  }

  public enum EDateInterval {
    MONTHLY, DAILY;
  }
}
