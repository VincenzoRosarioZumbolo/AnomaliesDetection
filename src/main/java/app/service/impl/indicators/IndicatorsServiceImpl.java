package app.service.impl.indicators;

import app.dto.AppState;
import app.dto.DataRecord;
import app.dto.FinancialIndicators;
import app.dto.FinancialIndicatorsPeriods;
import app.service.IndicatorsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the {@link IndicatorsService} responsible for processing financial data
 * records and executing mathematical models to compute traditional technical analysis indicators.
 * <p>This service sequences raw price series to populate momentum, trend, volatility, and volume-weighted structures.</p>
 */
public class IndicatorsServiceImpl implements IndicatorsService {

    /**
     * {@inheritDoc}
     * <p>Pulls raw sequential market records directly from the global {@link AppState} and computes
     * the analytical indicator cluster matching the user's specific window configuration.</p>
     */
    @Override
    public List<FinancialIndicators> calculateRSInMACDnATRnCMF(FinancialIndicatorsPeriods periods) {

        List<DataRecord> records = AppState.getInstance().getDataRecords();

        if (records == null || records.isEmpty())
            return new ArrayList<>();

        return calculate(records, periods);
    }

    /**
     * Overloaded calculation route designed to compute technical indicators using an explicitly
     * supplied list of data records and the fallback baseline {@link FinancialIndicatorsPeriods#STANDARD_PERIODS}.
     * <p>Typically utilized during background processing routines like dto training preparation where
     * localized historical contextual sub-lists are processed.</p>
     *
     * @param records The sequential list of raw {@link DataRecord} instances to analyze.
     * @return A populated {@link List} containing compiled indicator entities, or an empty list if input is invalid.
     */
    public List<FinancialIndicators> calculateRSInMACDnATRnCMF(List<DataRecord> records) {

        if (records == null || records.isEmpty())
            return new ArrayList<>();

        return calculate(records, FinancialIndicatorsPeriods.STANDARD_PERIODS);
    }

    /**
     * Internal orchestration routine that instantiates independent tracking engines for each complex
     * indicator, steps linearly through chronological records, and maps values into snapshot results.
     * <p>Calculated outputs for MACD and ATR are dynamically normalized relative to the current
     * closing quote to maintain scale uniformity across pricing variables.</p>
     *
     * @param records The structural timeline series of price records under evaluation.
     * @param periods The explicit period window sizes defining tracking criteria bounds.
     * @return A list mapping chronological points directly to computed technical results.
     */
    private List<FinancialIndicators> calculate(List<DataRecord> records, FinancialIndicatorsPeriods periods) {

        List<FinancialIndicators> results = new ArrayList<>();

        RsiCalculator rsiCalculator = new RsiCalculator(periods.getRSIPeriod());
        MacdCalculator macdCalculator = new MacdCalculator(periods.getMACDPeriod()[0], periods.getMACDPeriod()[1]);
        AtrCalculator atrCalculator = new AtrCalculator(periods.getATRPeriod());
        CmfCalculator cmfCalculator = new CmfCalculator(periods.getCMFPeriod());

        DataRecord previousRecord = null;

        for (DataRecord currentRecord : records) {

            rsiCalculator.update(currentRecord, previousRecord);
            macdCalculator.update(currentRecord);
            atrCalculator.update(currentRecord, previousRecord);
            cmfCalculator.update(currentRecord);

            double rsiVal = rsiCalculator.isReady() ? rsiCalculator.getValue() : 0.0;

            double macdVal = (macdCalculator.isReady() && currentRecord.getClose() != 0.0) ?
                    (macdCalculator.getValue() / currentRecord.getClose()) : 0.0;

            double atrVal = (atrCalculator.isReady() && currentRecord.getClose() != 0.0) ?
                    (atrCalculator.getValue() / currentRecord.getClose()) : 0.0;

            double cmfVal = cmfCalculator.isReady() ? cmfCalculator.getValue() : 0.0;

            results.add(new FinancialIndicators(
                    currentRecord.getTimestamp(),
                    rsiVal,
                    macdVal,
                    atrVal,
                    cmfVal
            ));

            previousRecord = currentRecord;
        }

        return results;
    }

    /**
     * Internal mathematical engine evaluating directional velocity dynamics via Relative Strength Index (RSI) metrics.
     * <p>Utilizes a Simple Moving Average for initial baseline pooling, shifting subsequently into
     * Wilder's smoothed moving average algorithm logic for persistent data feeds.</p>
     */
    static class RsiCalculator {

        private final int period;
        private int count = 0;
        private double sumGain = 0.0;
        private double sumLoss = 0.0;
        private double avgGain = 0.0;
        private double avgLoss = 0.0;

        /**
         * Initializes the RSI calculation engine with a specific window constraints threshold.
         *
         * @param period The sliding timeframe window size for tracking price changes.
         */
        RsiCalculator(int period) {
            this.period = period;
        }

        /**
         * Updates sequential parameters tracking relative upward and downward close differences.
         *
         * @param current The active timeline record.
         * @param prev    The chronologically preceding history record, used to determine directional delta bounds.
         */
        void update(DataRecord current, DataRecord prev) {

            if (prev == null) return;

            double diff = current.getClose() - prev.getClose();
            double gain = Math.max(0, diff);
            double loss = Math.max(0, -diff);

            count++;
            if (count <= period) {
                sumGain += gain;
                sumLoss += loss;
                if (count == period) {
                    avgGain = sumGain / period;
                    avgLoss = sumLoss / period;
                }
            } else {
                avgGain = (avgGain * (period - 1) + gain) / period;
                avgLoss = (avgLoss * (period - 1) + loss) / period;
            }
        }

        /**
         * Verifies if enough raw data context has accumulated to generate valid indicator metrics.
         *
         * @return {@code true} if sequential updates meet or exceed window size constraints; {@code false} otherwise.
         */
        boolean isReady() { return count >= period; }

        /**
         * Compiles the relative strength calculation, bounding the final metric output between 0 and 100.
         *
         * @return The calculated RSI technical analysis value.
         */
        double getValue() {
            if (avgLoss == 0.0) return 100.0;
            double rs = avgGain / avgLoss;
            return 100.0 - (100.0 / (1 + rs));
        }
    }

    /**
     * Internal mathematical engine parsing price moving convergence and divergence (MACD) attributes.
     * <p>Tracks momentum and shifts by resolving deviations between two distinct, variable-length
     * Exponential Moving Average engines.</p>
     */
    static class MacdCalculator {

        private final EmaCalculator fastEma;
        private final EmaCalculator slowEma;

        /**
         * Initializes the twin-engine tracking layout for MACD trend mapping calculations.
         *
         * @param fastPeriod The window period parameter for the reactive fast-tracking line.
         * @param slowPeriod The window period parameter for the foundational slow-tracking line.
         */
        MacdCalculator(int fastPeriod, int slowPeriod) {
            this.fastEma = new EmaCalculator(fastPeriod);
            this.slowEma = new EmaCalculator(slowPeriod);
        }

        /**
         * Passes the active pricing quote point down to execute recursive underlying state updates.
         *
         * @param current The active timestamp record containing closing context points.
         */
        void update(DataRecord current) {
            fastEma.update(current.getClose());
            slowEma.update(current.getClose());
        }

        /**
         * Checks if the longer foundational indicator timeline window has gathered sufficient data points.
         *
         * @return {@code true} if the slower baseline engine is ready; {@code false} otherwise.
         */
        boolean isReady() {
            return slowEma.isReady();
        }

        /**
         * Subtracts the baseline slow valuation line from the reactive fast valuation metrics.
         *
         * @return The underlying nominal gap value representing MACD lines convergence/divergence.
         */
        double getValue() {
            return fastEma.getValue() - slowEma.getValue();
        }

        /**
         * Utility class tracking independent Exponential Moving Average calculations using standard smoothing multipliers.
         */
        static class EmaCalculator {
            private final int period;
            private final double k;
            private int count = 0;
            private double sum = 0.0;
            private double ema = 0.0;

            /**
             * Sets up multiplier smoothing factors matching specified periodic sizes.
             *
             * @param period The structural size parameter representing lookback constraints.
             */
            EmaCalculator(int period) {
                this.period = period;
                this.k = 2.0 / (period + 1);
            }

            /**
             * Calculates the initial simple average baseline, scaling later into exponential updates.
             *
             * @param value The raw incoming double coordinate value to factor into the average.
             */
            void update(double value) {
                count++;
                if (count <= period) {
                    sum += value;
                    if (count == period) {
                        ema = sum / period;
                    }
                } else {
                    ema = (value - ema) * k + ema;
                }
            }

            /**
             * Verifies if lookback baseline data context requirements have been reached.
             *
             * @return {@code true} if ready to output data; {@code false} otherwise.
             */
            boolean isReady() { return count >= period; }

            /**
             * Returns the calculated active exponential value.
             *
             * @return The active EMA tracking point metric.
             */
            double getValue() { return ema; }
        }
    }

    /**
     * Internal mathematical engine modeling market volatility via Average True Range (ATR) metrics.
     * <p>Tracks continuous volatility characteristics by calculating the absolute maximum value across
     * high-to-low intervals, high-to-previous-close gaps, and low-to-previous-close ranges.</p>
     */
    static class AtrCalculator {
        private final int period;
        private int count = 0;
        private double sumTr = 0.0;
        private double atr = 0.0;

        /**
         * Initializes the volatility processing framework with window constraint definitions.
         *
         * @param period The sliding timeframe lookback width for calculating volatility ranges.
         */
        AtrCalculator(int period) {
            this.period = period;
        }

        /**
         * Captures historical pricing rows to resolve the relative peak and boundary gaps.
         *
         * @param current The active structural timeline row under analysis.
         * @param prev    The immediate history tracking element used to calculate previous close deltas.
         */
        void update(DataRecord current, DataRecord prev) {
            double high = current.getHigh();
            double low = current.getLow();

            double closePrev = (prev == null) ? current.getOpen() : prev.getClose();

            double tr1 = high - low;
            double tr2 = Math.abs(high - closePrev);
            double tr3 = Math.abs(low - closePrev);
            double trueRange = Math.max(tr1, Math.max(tr2, tr3));

            count++;
            if (count <= period) {
                sumTr += trueRange;
                if (count == period) {
                    atr = sumTr / period;
                }
            } else {
                atr = (atr * (period - 1) + trueRange) / period;
            }
        }

        /**
         * Checks if the required timeframe window length has been fulfilled.
         *
         * @return {@code true} if sequential ticks meet or exceed initialization ranges; {@code false} otherwise.
         */
        boolean isReady() { return count >= period; }

        /**
         * Returns the calculated active average value.
         *
         * @return The computed ATR volatility metric.
         */
        double getValue() { return atr; }
    }

    /**
     * Internal mathematical engine implementing Chaikin Money Flow (CMF) metrics.
     * <p>Integrates volume weighting with underlying structural location indicators to map
     * accumulation and distribution flow weights via a sliding buffer architecture.</p>
     */
    static class CmfCalculator {
        private final int period;
        private final double[] mfvBuffer;
        private final long[] volBuffer;
        private int index = 0;
        private int count = 0;
        private double sumMfv = 0.0;
        private long sumVol = 0;

        /**
         * Prepares structural buffer memory rings sized precisely to periodic requirements.
         *
         * @param period The timeframe window context required for money flow summation.
         */
        CmfCalculator(int period) {
            this.period = period;
            this.mfvBuffer = new double[period];
            this.volBuffer = new long[period];
        }

        /**
         * Computes active Money Flow Multiplier metrics, scaling values against structural volume sizes,
         * and rolls buffer ring configurations forward.
         *
         * @param current The active transactional data dto snapshot containing price and volume attributes.
         */
        void update(DataRecord current) {
            double high = current.getHigh();
            double low = current.getLow();
            double close = current.getClose();
            long volume = current.getVolume();

            double mfm = (high == low) ? 0.0 : ((close - low) - (high - close)) / (high - low);
            double mfv = mfm * volume;

            if (count == period) {
                sumMfv -= mfvBuffer[index];
                sumVol -= volBuffer[index];
            } else {
                count++;
            }

            mfvBuffer[index] = mfv;
            volBuffer[index] = volume;
            sumMfv += mfv;
            sumVol += volume;

            index = (index + 1) % period;
        }

        /**
         * Verifies if the sliding buffer architecture is completely filled.
         *
         * @return {@code true} if data ticks matching the period length have been cached; {@code false} otherwise.
         */
        boolean isReady() { return count == period; }

        /**
         * Divides cached money flow values against the cumulative aggregate volume pool.
         *
         * @return The final parsed Chaikin Money Flow indicator valuation.
         */
        double getValue() {
            return sumVol == 0 ? 0.0 : sumMfv / sumVol;
        }
    }
}