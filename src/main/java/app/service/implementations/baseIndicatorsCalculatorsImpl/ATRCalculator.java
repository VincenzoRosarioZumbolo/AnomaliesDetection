package app.service.implementations.baseIndicatorsCalculatorsImpl;

import java.util.ArrayList;
import java.util.List;

public class ATRCalculator {

    public static double calculateATR(int period, List<Double> highs,  List<Double> lows, List<Double> closures) {

        List<Double> TRs = calculateTRs(highs, lows, closures);

        return calculateATRFromTRs(TRs, period);
    }

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
