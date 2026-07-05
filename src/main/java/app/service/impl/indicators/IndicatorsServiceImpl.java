package app.service.impl.indicators;

import app.model.AppState;
import app.model.DataRecord;
import app.model.FinancialIndicators;
import app.model.FinancialIndicatorsPeriods;
import app.service.IndicatorsService;

import java.util.ArrayList;
import java.util.List;

public class IndicatorsServiceImpl implements IndicatorsService {

    @Override
    public List<FinancialIndicators> calculateRSInMACDnATRnCMF(FinancialIndicatorsPeriods periods) {

        List<DataRecord> records = AppState.getInstance().getDataRecords();

        if (records == null || records.isEmpty())
            return new ArrayList<>();

        return calculate(records, periods);
    }
    
    public List<FinancialIndicators> calculateRSInMACDnATRnCMF(List<DataRecord> records) {

        if (records == null || records.isEmpty())
            return new ArrayList<>();

        return calculate(records, FinancialIndicatorsPeriods.STANDARD_PERIODS);
    }

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

    static class RsiCalculator {

        private final int period;
        private int count = 0;
        private double sumGain = 0.0;
        private double sumLoss = 0.0;
        private double avgGain = 0.0;
        private double avgLoss = 0.0;

        RsiCalculator(int period) {
            this.period = period;
        }

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

        boolean isReady() { return count >= period; }

        double getValue() {
            if (avgLoss == 0.0) return 100.0;
            double rs = avgGain / avgLoss;
            return 100.0 - (100.0 / (1 + rs));
        }
    }

    static class MacdCalculator {

        private final EmaCalculator fastEma;
        private final EmaCalculator slowEma;

        MacdCalculator(int fastPeriod, int slowPeriod) {
            this.fastEma = new EmaCalculator(fastPeriod);
            this.slowEma = new EmaCalculator(slowPeriod);
        }

        void update(DataRecord current) {
            fastEma.update(current.getClose());
            slowEma.update(current.getClose());
        }

        boolean isReady() {
            return slowEma.isReady();
        }

        double getValue() {
            return fastEma.getValue() - slowEma.getValue();
        }

        static class EmaCalculator {
            private final int period;
            private final double k;
            private int count = 0;
            private double sum = 0.0;
            private double ema = 0.0;

            EmaCalculator(int period) {
                this.period = period;
                this.k = 2.0 / (period + 1);
            }

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

            boolean isReady() { return count >= period; }
            double getValue() { return ema; }
        }
    }

    static class AtrCalculator {
        private final int period;
        private int count = 0;
        private double sumTr = 0.0;
        private double atr = 0.0;

        AtrCalculator(int period) {
            this.period = period;
        }

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

        boolean isReady() { return count >= period; }
        double getValue() { return atr; }
    }

    static class CmfCalculator {
        private final int period;
        private final double[] mfvBuffer;
        private final long[] volBuffer;
        private int index = 0;
        private int count = 0;
        private double sumMfv = 0.0;
        private long sumVol = 0;

        CmfCalculator(int period) {
            this.period = period;
            this.mfvBuffer = new double[period];
            this.volBuffer = new long[period];
        }

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

        boolean isReady() { return count == period; }

        double getValue() {
            return sumVol == 0 ? 0.0 : sumMfv / sumVol;
        }
    }
}