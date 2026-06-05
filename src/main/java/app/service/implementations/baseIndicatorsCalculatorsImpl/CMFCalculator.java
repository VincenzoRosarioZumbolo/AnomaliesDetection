package app.service.implementations.baseIndicatorsCalculatorsImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CMFCalculator {

    public static double calculateCMF(int period, List<Double> highs, List<Double> lows, List<Double> closures, List<Long> volumes) {

        List<Double> MFVs = calculateMFVs(highs, lows, closures, volumes, period);

        return calculateCMFFromMFVs(MFVs, volumes, period);
    }

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

    private static double calculateCMFFromMFVs(List<Double> MFVs, List<Long> volumes, int period) {

        double MFVSum = 0;
        long volumesSum = 0;

        for (int i = Math.max(volumes.size() - period, 0); i < volumes.size(); i++)
            volumesSum += volumes.get(i);

        for (Double mfv : MFVs) MFVSum += mfv;

        return MFVSum / volumesSum;
    }

}
