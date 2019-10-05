package com.meaf.apeps.calculations;

public class HoltWinters implements Forecasting{

    double[] Lt, Tt, Stt, Fc, ForecastForErr, Err, ErrPerc;

    public static void main(String[] args) {
        int[] stats = new int[]{
                17986229, 23571965, 25537589, 24630951, 24429696, 26116377, 27931501, 25914893, 24904130, 22360354, 23825299, 22241744, 21149853, 23770186, 29608386, 28588548, 29712036, 31191793, 28311730, 27438262, 26166319, 25916207, 23168086, 27707909, 25379305, 27823570, 28518039, 33971886, 31577081, 29328611, 34312920, 31364478, 29046432, 27244171, 24353446, 25447525, 24255101, 22391876, 27902911, 24102028, 24939643, 25401741, 22817314, 23554471, 21219769, 21144903, 19185427, 20507490, 16116508, 20363081
        };

        new HoltWinters().forecast(stats, 0.5, 0.5, 0.5, 12, 10, true);

    }

    public double[] forecast(int[] y, double alpha, double beta,
                             double gamma, int period, int m, boolean debug) {

        if (y == null) {
            return null;
        }

        int seasons = y.length / period;
        double a0 = calculateInitialLevel(y, period);
        double b0 = calculateInitialTrend(y, period);
        double[] initialSeasonalIndices = calculateSeasonalIndices(y, period, seasons);

        double[] forecast = calculateHoltWinters(y, a0, b0, alpha, beta, gamma,
                initialSeasonalIndices, period, m, debug);

        if (debug) {
            printArray("Forecast", forecast);
        }

        return forecast;
    }
    private static double[] calculateHoltWinters(int[] y, double a0, double b0, double alpha,
                                                 double beta, double gamma, double[] initialSeasonalIndices, int period, int m, boolean debug) {

        double[] St = new double[y.length];
        double[] Bt = new double[y.length];
        double[] It = new double[y.length];
        double[] Ft = new double[y.length + m];


        return Ft;
    }

    private static double calculateInitialLevel(int[] y, int period) {

        return y[0];
    }

    private static double calculateInitialTrend(int[] y, int period){

        double sum = 0;

        for (int i = 0; i < period; i++) {
            sum += (y[period + i] - y[i]);
        }

        return sum / (period * period);
    }

    private static double[] calculateSeasonalIndices(int[] y, int period, int seasons){

        double[] seasonalAverage = new double[seasons];
        double[] seasonalIndices = new double[period];

        double[] averagedObservations = new double[y.length];


        return seasonalIndices;
    }

    private static void printArray(String description, double[] data){

    }
}
