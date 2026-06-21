package app.service.impl.indicators;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility calculator layer responsible for calculating the Relative Strength Index (RSI) metric.
 * <p>RSI serves as a momentum oscillator bounded between 0 and 100, tracking the magnitude
 * of recent price gains relative to losses to identify overbought or oversold conditions.</p>
 */
public class RSICalculator {

    /**
     * Calculates the Relative Strength Index value across a specified lookback window.
     *
     * @param period   The number of structural timeframe lines included within the RSI evaluation.
     * @param closures A collection list tracking chronological asset pricing closures.
     * @return The final computed RSI oscillator value (bounded between 0.0 and 100.0).
     */
    public static double calculateRSI(int period, List<Double> closures) {

        List<Double> ups = new ArrayList<>();
        List<Double> downs = new ArrayList<>();

        calculateUpsAndDowns(closures, ups, downs);

        //0 up 1 down
        double[] averages = calculateRecursiveAverages(calculateBaseAverages(period, ups, downs), ups, downs, period);

        if (averages[1] == 0)
            return (averages[0] == 0) ? 50 : 100;

        double rs = averages[0] / (averages[1]);

        return 100 - (100 / (1 + rs));
    }

    /**
     * Iterates through closing price history to calculate sequential absolute gains and losses between intervals.
     *
     * @param closures Master collection charting historical interval closing points.
     * @param ups      Destination collection tracking positive delta changes.
     * @param downs    Destination collection tracking negative delta changes.
     */
    private static void calculateUpsAndDowns(List<Double> closures, List<Double> ups, List<Double> downs) {

        for (int i = 1; i < closures.size(); i++) {
            double delta = closures.get(i) - closures.get(i - 1);

            ups.add(Math.max(delta, 0));
            downs.add(Math.abs(Math.min(delta, 0)));
        }
    }

    /**
     * Generates a base arithmetic simple average baseline using the initial subset blocks matching the window size.
     *
     * @param period Window tracking width parameter.
     * @param ups    Calculated positive delta arrays.
     * @param downs  Calculated negative delta arrays.
     * @return A primitive array containing 2 fields (Index 0: initial upward average, Index 1: initial downward average).
     */
    private static double[] calculateBaseAverages(int period, List<Double> ups, List<Double> downs) {

        double[] averages = {0, 0};

        for (int i = 0; i < Math.min(period, ups.size()); i++) {

            averages[0] += ups.get(i);
            averages[1] += downs.get(i);
        }

        averages[0] /= period;
        averages[1] /= period;

        return averages;
    }

    /**
     * Smooths average gains and losses recursively across the remaining dataset using standard Wilder smoothing techniques.
     *
     * @param baseAverages Calculated base arithmetic averages array.
     * @param ups          Calculated positive delta components.
     * @param downs        Calculated negative delta components.
     * @param period       Lookback range constraint.
     * @return An array containing 2 fields tracking smoothed upward and downward average metrics sequentially.
     */
    private static double[] calculateRecursiveAverages(double[] baseAverages, List<Double> ups, List<Double> downs, int period) {

        double averageUp = baseAverages[0];
        double averageDown = baseAverages[1];

        for (int i = period; i < ups.size(); i++) {
            averageUp = (averageUp * (period - 1) + ups.get(i)) / period;
            averageDown = (averageDown * (period - 1) + downs.get(i)) / period;
        }

        return new double[]{averageUp, averageDown};
    }
}