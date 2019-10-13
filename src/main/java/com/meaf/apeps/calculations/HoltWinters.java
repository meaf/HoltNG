package com.meaf.apeps.calculations;

import java.util.Arrays;

public class HoltWinters {

  double[]
      S,
      Lt, //экспоненциально сглаженный ряд
      Tt, //значение тренда
      Sts, // коэффициент сезонности предыдущего периода;
      Fc, // Прогноз по методу Хольта
      FcEstimated, // Проноз для оценки модели
      ErrorPerc, // Ошибка модели
      Err; //Отклонение ошибки модели от прогнозной модели


  public static void main(String[] args) {

    double[] stats = new double[]{
        17986229, 23571965, 25537589, 24630951, 24429696, 26116377, 27931501, 25914893, 24904130,
        22360354, 23825299, 22241744, 21149853, 23770186, 29608386, 28588548, 29712036, 31191793,
        28311730, 27438262, 26166319, 25916207, 23168086, 27707909, 25379305, 27823570, 28518039,
        33971886, 31577081, 29328611, 34312920, 31364478, 29046432, 27244171, 24353446, 25447525,
        24255101, 22391876, 27902911, 24102028, 24939643, 25401741, 22817314, 23554471, 21219769,
        21144903, 19185427, 20507490, 16116508, 20363081
    };

    class Result implements Comparable {
      public Result(double a, double b, double c, double accuracy) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.accuracy = accuracy;
      }

      double a, b, c;
      double accuracy;

      @Override
      public int compareTo(Object o) {
        if (o instanceof Result) {
          if (accuracy > ((Result) o).accuracy) {
            return 1;
          }
        }
        if (accuracy < ((Result) o).accuracy) {
          return -1;
        }
        return 0;
      }
    }

    Result result = new Result(-1, -1, -1, 100);

    for (double a = 0; a <= 1; a += 0.05) {
      for (double b = 0; b <= 1; b += 0.05) {
        for (double c = 0; c <= 1; c += 0.05) {
          double newAcc = new HoltWinters().forecast(stats, a, b, c, 12, 10);
          if (result.accuracy > newAcc) {
            result.accuracy = newAcc;
            result.a = a;
            result.b = b;
            result.c = c;
          }
        }
      }
    }

    System.out.println("done");

  }

  public double forecast(double[] y,
                         double alpha,
                         double beta,
                         double gamma,
                         int period,
                         int forecastLen) {

    S = y;
    initValues(forecastLen);

    for (int i = 1; i < S.length; i++) {
      calculateStep(alpha, beta, gamma, forecastLen, i);
    }

    for (int i = S.length; i < S.length + forecastLen; i++) {
      calculateForecastStep(period, i);
    }
    return Arrays.stream(ErrorPerc).reduce(Double::sum).orElse(-1) / S.length;

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
    Fc[p - 1] = (Lt[i - 1] + Tt[i - 1] * p) * Sts[i - period];
  }

  private double getSeasonalK(int i, int forecastLen) {
    return i < forecastLen ? 1 : Sts[i - forecastLen];
  }

  private void initValues(int forecastLen) {
    int resultedLen = S.length + forecastLen;
    Fc = new double[forecastLen];

    Lt = new double[resultedLen];
    Tt = new double[resultedLen];
    Sts = new double[resultedLen];
    Err = new double[resultedLen];
    FcEstimated = new double[resultedLen];
    ErrorPerc = new double[resultedLen];

    Lt[0] = S[0];
    Tt[0] = 0;
    Sts[0] = 1;
    Fc[0] = 0;
    Err[0] = 0;
    FcEstimated[0] = Lt[0];
    ErrorPerc[0] = 0;

  }

}
