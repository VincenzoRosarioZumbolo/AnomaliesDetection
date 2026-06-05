package app.service.implementations.baseIndicatorsCalculatorsImpl;

import java.util.ArrayList;
import java.util.List;

public class RSICalculator {

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

    private static void calculateUpsAndDowns(List<Double> closures, List<Double> ups, List<Double> downs) {

        for (int i = 1; i < closures.size(); i++) {
            double delta = closures.get(i) - closures.get(i - 1);

            ups.add(Math.max(delta, 0));
            downs.add(Math.abs(Math.min(delta, 0)));
        }
    }

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

    private static double[] calculateRecursiveAverages(double[] averages, List<Double> ups, List<Double> downs, int period) {

        for (int i = period; i < ups.size(); i++) {

            averages[0] = averages[0] * (period - 1) / period + ups.get(i) / period;
            averages[1] = averages[1] * (period - 1) / period + downs.get(i) / period;
        }

        return averages;
    }
}
