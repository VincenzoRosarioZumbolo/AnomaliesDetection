package app.service.implementations.baseIndicatorsCalculatorsImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility calculator layer responsible for calculating the Moving Average Convergence Divergence (MACD) metric.
 * <p>MACD serves as a trend-following momentum indicator, visualizing the relationship between two separate
 * Exponential Moving Averages (EMA) of asset prices alongside a third signal line smoothing element.</p>
 */
public class MACDCalculator {

    /**
     * Calculates the definitive MACD value, derived as the difference between the main MACD Line and its corresponding Signal Line.
     *
     * @param periods  A primitive array containing lookback configuration coordinates (Index 0: Fast period, Index 1: Slow period, Index 2: Signal line period).
     * @param closures The complete listing sequence tracking chronological asset closing parameters.
     * @return The final resulting value representation tracking MACD momentum parameters.
     */
    public static double calculateMACD(int[] periods, List<Double> closures) {

        int fastPeriod = periods[0];
        int slowPeriod = periods[1];
        int signalLinePeriod = periods[2];

        double EMAFast = calculateEMA(fastPeriod, closures);
        double EMASlow = calculateEMA(slowPeriod, closures);

        double MACDLine = EMAFast - EMASlow;

        List<Double> EMAFasts = new ArrayList<>();
        List<Double> EMASlows = new ArrayList<>();

        for (int i = slowPeriod; i < closures.size(); i++) {

            EMAFasts.add(calculateEMA(fastPeriod, closures.subList(0, i)));
            EMASlows.add(calculateEMA(slowPeriod, closures.subList(0, i)));
        }

        List<Double> MACDs = new ArrayList<>();

        for (int i = fastPeriod; i < EMAFasts.size(); i++)
            MACDs.add(EMAFasts.get(i) - EMASlows.get(i));

        double signalLine = calculateEMA(signalLinePeriod, MACDs);

        return MACDLine - signalLine;
    }

    /**
     * Determines the Exponential Moving Average (EMA) parameter for a given list of numeric values.
     * <p>Applies a mathematical multiplier to heavily weight recent closing prices over older observations.</p>
     *
     * @param period The mathematical evaluation period context tracking bounds.
     * @param values The active subset array listing tracking targets.
     * @return The derived current numerical Exponential Moving Average marker point.
     */
    private static double calculateEMA(int period, List<Double> values) {

        double k = (double)2 / (period + 1);

        double ema = 0;
        for (int i = 0; i < Math.min(period, values.size()); i++)
            ema += values.get(i);

        ema /= period;

        for (int i = period; i < values.size(); i++)
            ema = values.get(i) * k + ema * (1 - k);

        return ema;
    }
}