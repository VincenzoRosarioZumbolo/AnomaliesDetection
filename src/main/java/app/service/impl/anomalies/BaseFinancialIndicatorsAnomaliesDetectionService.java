package app.service.impl.anomalies;

import app.model.FinancialIndicators;
import smile.anomaly.IsolationForest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseFinancialIndicatorsAnomaliesDetectionService extends BaseAnomaliesDetectionService<FinancialIndicators> {

    @Override
    protected double[][] parseData(List<FinancialIndicators> data) {

        double[][] parsedData = new double[data.size()][4];
        int i = 0;

        for (FinancialIndicators financialIndicators : data) {
            parsedData[i][0] = financialIndicators.getRSI();
            parsedData[i][1] = financialIndicators.getMACD();
            parsedData[i][2] = financialIndicators.getATR();
            parsedData[i++][3] = financialIndicators.getCMF();
        }

        return parsedData;
    }

    @Override
    protected double[] calculateMeans(List<FinancialIndicators> data) {

        if (data == null || data.isEmpty())
            return new double[]{0, 0, 0, 0};

        double[] sums = new double[4];

        for (FinancialIndicators record : data) {
            sums[0] += record.getRSI();
            sums[1] += record.getMACD();
            sums[2] += record.getATR();
            sums[3] += record.getCMF();
        }

        double[] means = new double[4];
        for (int i = 0; i < 4; i++) {
            means[i] = sums[i] / data.size();
        }

        return means;
    }

    @Override
    protected Map<String, Double> calculateContributions(IsolationForest isolationForest, double[] record, List<FinancialIndicators> trainingData) {

        double[] means = calculateMeans(trainingData);
        String[] featureNames = {"RSI", "MACD", "ATR", "CMF"};
        Map<String, Double> contributions = new HashMap<>();

        double meansScore = isolationForest.score(means);
        double[] rawImpacts = new double[4];
        double totalImpact = 0.0;

        for (int i = 0; i < 4; i++) {

            double[] testPoint = means.clone();
            testPoint[i] = record[i];

            double newScore = isolationForest.score(testPoint);
            rawImpacts[i] = Math.abs(newScore - meansScore);
            totalImpact += rawImpacts[i];
        }

        for (int i = 0; i < 4; i++) {

            double percentage = 0;

            if (totalImpact > 0)
                percentage = (rawImpacts[i] / totalImpact) * 100;

            contributions.put(featureNames[i], percentage);
        }

        return contributions;
    }
}
