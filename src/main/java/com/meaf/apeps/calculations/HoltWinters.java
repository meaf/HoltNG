package com.meaf.apeps.calculations;

import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DatedValue;
import com.meaf.apeps.utils.ETargetValues;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
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
  private Date filterDate;

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

    forecast(optimalA, optimalB, optimalC, period, forecastLen);
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

  public List<DatedValue> getFcTail() {
    return Fc;
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
    int sliceSize = (int)inputData.stream().map(DatedValue::getDate).filter(filterDate::after).count();

    initValues(forecastLen, sliceSize);

    for (int i = 1; i < sliceSize; i++)
      calculateStep(alpha, beta, gamma, period, i, sliceSize);

    for (int i = sliceSize; i < sliceSize + forecastLen; i++)
      calculateForecastStep(period, i, sliceSize);

    calculateError(sliceSize);
  }

  private void initValues(int forecastLen, int sliceSize) {
    int resultedLen = sliceSize + forecastLen;
    Fc = new ArrayList<>(forecastLen);

    Lt = new ArrayList<>(sliceSize);
    Tt = new ArrayList<>(sliceSize);
    Sts = new ArrayList<>(sliceSize);
    Err = new ArrayList<>(sliceSize);
    FcEstimated = new ArrayList<>(sliceSize);
    ErrorPerc = new ArrayList<>(sliceSize);
    ErrorMSE = new ArrayList<>(sliceSize);
    ErrorMAE = new ArrayList<>(sliceSize);


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

  private void calculateStep(double alpha, double beta, double gamma, int period, int i, int sliceSize) {
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

  private void calculateForecastStep(int period, int i, int sliceSize) {
    int lastSequenceValueInd = sliceSize - 1;
    int fcPointNumber = i - sliceSize + 1;
    Date lastDate = S.get(lastSequenceValueInd).getDate();
    LocalDate now = new Date(lastDate.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(fcPointNumber);

    Fc.add(fcPointNumber - 1, new DatedValue(now, (Lt.get(lastSequenceValueInd).asDouble() + Tt.get(lastSequenceValueInd).asDouble() * fcPointNumber) * Sts.get(i - period).asDouble()));
  }

  private double getSeasonalK(int i, int period) {
    return i < period ? 1 : Sts.get(i - period).asDouble();
  }

  private void calculateError(int sliceSize) {
    this.maeAvg = ErrorMAE.stream().map(DatedValue::asDouble).reduce(Double::sum).orElse(-1d) / sliceSize;
    this.mseAvg = ErrorMSE.stream().map(DatedValue::asDouble).reduce(Double::sum).orElse(-1d) / sliceSize;
    this.msePerc = ErrorPerc.stream().map(DatedValue::asDouble).reduce(Double::sum).orElse(-1d) / sliceSize;
  }

  public EDateInterval getDateInterval() {
    return dateInterval;
  }

  public void setDateInterval(EDateInterval dateInterval) {
    this.dateInterval = dateInterval;
  }

  public DatedValue getLastActualData() {
    return S.stream()
        .filter(s -> s.getDate().before(filterDate))
        .max(Comparator.comparing(DatedValue::getDate))
        .orElse(null);
  }

  public void setFilterDate(Date filterDate) {
    this.filterDate = filterDate;
  }

  public Date getFilterDate() {
    return filterDate;
  }

  public enum EDateInterval {
    MONTHLY, DAILY;
  }
}
