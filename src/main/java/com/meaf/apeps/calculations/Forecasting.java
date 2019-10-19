package com.meaf.apeps.calculations;

import java.util.List;

public interface Forecasting {
    void calculate(double[] stats, Double alpha, Double betta, Double gamma, int period, int forecastLen);
    Result getOptimalResult();
    List<Double> getInputData();
    List<Double> getSmoothedData();
    List<Double> getFcData();
}
