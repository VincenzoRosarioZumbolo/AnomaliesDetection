package app.service.impl.indicators;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility calculator layer responsible for calculating the Chaikin Money Flow (CMF) technical indicator value.
 * <p>CMF measures the institutional accumulation and distribution strength of an asset over a set period,
 * factoring in both pricing positions relative to ranges and corresponding volumes.</p>
 */
public class CMFCalculator {

    /**
     * Orchestrates the multi-stage matrix calculation logic required to derive the Chaikin Money Flow index.
     *
     * @param period   The number of recent timeline bars included inside the tracking window calculation.
     * @param highs    A data list containing asset pricing high constraints.
     * @param lows     A data list containing asset pricing low constraints.
     * @param closures A data list containing asset pricing close benchmarks.
     * @param volumes  A data list tracking total historical trading volume magnitudes.
     * @return The calculated CMF percentage index value, typically ranging between -1.0 and +1.0.
     */
    public static double calculateCMF(int period, List<Double> highs, List<Double> lows, List<Double> closures, List<Long> volumes) {

        List<Double> MFVs = calculateMFVs(highs, lows, closures, volumes, period);

        return calculateCMFFromMFVs(MFVs, volumes, period);
    }

    /**
     * Determines individual Money Flow Volume (MFV) metrics for each point within the targeted lookback timeframe.
     * <p>Calculates a multiplier based on where the close price falls within the high-low range, then scales it by volume.</p>
     *
     * @param highs    Asset period ceiling arrays.
     * @param lows     Asset period floor arrays.
     * @param closures Asset period terminal endpoints.
     * @param volumes  Asset transaction quantities tracking vectors.
     * @param period   The contextual historical depth range size.
     * @return A list detailing calculated Money Flow Volume values over the lookback window.
     */
    private static List<Double> calculateMFVs(List<Double> highs, List<Double> lows, List<Double> closures, List<Long> volumes, int period) {

        List<Double> MFVs = new ArrayList<>();

        for (int i = Math.max(volumes.size() - period, 0); i < volumes.size(); i++)
            if (Objects.equals(highs.get(i), lows.get(i)))
                MFVs.add(0.0);
            else
                MFVs.add((((closures.get(i) - lows.get(i)) - (highs.get(i) - closures.get(i))) /
                        (highs.get(i) - lows.get(i))) *
                        (double)volumes.get(i));

        return MFVs;
    }

    /**
     * Aggregates the computed Money Flow Volume sums and divides them by total volume to calculate the final CMF ratio.
     *
     * @param MFVs    The calculated lookback array containing Money Flow Volume values.
     * @param volumes The complete collection of transactional volume histories.
     * @param period  The scope boundaries tracking index.
     * @return The final aggregated Chaikin Money Flow metric.
     */
    private static double calculateCMFFromMFVs(List<Double> MFVs, List<Long> volumes, int period) {

        double MFVsum = 0;
        long VolSum = 0;

        for (double mfv : MFVs)
            MFVsum += mfv;

        for (int i = Math.max(volumes.size() - period, 0); i < volumes.size(); i++)
            VolSum += volumes.get(i);

        if (VolSum == 0) return 0;

        return MFVsum / VolSum;
    }
}