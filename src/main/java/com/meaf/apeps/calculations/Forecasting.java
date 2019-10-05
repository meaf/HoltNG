package com.meaf.apeps.calculations;

public interface Forecasting {
    public double[] forecast(int[] y, double alpha, double beta, double gamma, int period, int m, boolean debug);
}
