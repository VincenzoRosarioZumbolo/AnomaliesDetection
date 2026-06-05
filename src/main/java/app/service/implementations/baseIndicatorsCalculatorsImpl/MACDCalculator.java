package app.service.implementations.baseIndicatorsCalculatorsImpl;

import java.util.ArrayList;
import java.util.List;

public class MACDCalculator {

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

    private static double calculateEMA(int period, List<Double> values) {

        double k = (double)2 / (period + 1);

        double EMA = 0;

        for (int i = 0; i < Math.min(period, values.size()); i++)
            EMA += values.get(i);

        EMA /= period;

        for (int i = period; i < values.size(); i++)
            EMA = values.get(i) * k + EMA * (1 - k);

        return EMA;
    }
}
