package app.service.implementations.baseIndicatorsCalculatorsImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility mathematical model calculator responsible for computing the Average True Range (ATR)
 * financial indicator metric.
 * <p>ATR measures market volatility by decomposing the entire range of an asset's historical asset movement
 * across sequential intervals using True Range formulas.</p>
 */
public class ATRCalculator {

    /**
     * Entry-point method that coordinates the calculation of the Average True Range value across a specified lookback window.
     *
     * @param period   The numerical rolling lookback window interval size.
     * @param highs    A collection list tracking sequential historical period high pricing boundaries.
     * @param lows     A collection list tracking sequential historical period low pricing boundaries.
     * @param closures A collection list tracking sequential historical period closing pricing values.
     * @return The computed modern numeric Average True Range volatility value.
     */
    public static double calculateATR(int period, List<Double> highs,  List<Double> lows, List<Double> closures) {

        List<Double> TRs = calculateTRs(highs, lows, closures);

        return calculateATRFromTRs(TRs, period);
    }

    /**
     * Determines individual structural True Range values across the length of the dataset series.
     * <p>Computes the maximum absolute distance comparing the active period high/low range,
     * high versus previous close, and low versus previous close.</p>
     *
     * @param highs    Historical data series tracking interval highs.
     * @param lows     Historical data series tracking interval lows.
     * @param closures Historical data series tracking interval closures.
     * @return A list containing calculated continuous True Range values.
     */
    private static List<Double> calculateTRs(List<Double> highs, List<Double> lows, List<Double> closures) {

        List<Double> TRs = new ArrayList<>();

        TRs.add(highs.getFirst() -  lows.getFirst());

        for (int i = 1; i < highs.size(); i++)
            TRs.add(Math.max(
                    Math.max(highs.get(i) - lows.get(i),
                            Math.abs(highs.get(i) - closures.get(i-1))),
                    Math.abs(lows.get(i) - closures.get(i-1))));

        return TRs;
    }

    /**
     * Smooths and builds the standard rolling average from calculated True Range increments.
     * <p>Applies Wilder's smoothing technique recursively after deriving a base simple arithmetic average.</p>
     *
     * @param TRs    The list of all extracted primitive True Range values.
     * @param period The mathematical evaluation period context.
     * @return The final smoothed absolute average metrics boundary.
     */
    private static double calculateATRFromTRs(List<Double> TRs, int period) {

        double ATR = 0;

        for (int i = 0; i < Math.min(period, TRs.size()); i++)
            ATR += TRs.get(i);

        ATR /= period;

        for (int i = period; i < TRs.size(); i++)
            ATR = (ATR * (period - 1) + TRs.get(i)) / period;

        return ATR;
    }
}