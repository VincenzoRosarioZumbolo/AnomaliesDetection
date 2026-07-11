package app.service.impl.anomalies;

import app.dto.DataRecord;
import smile.anomaly.IsolationForest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the {@link BaseAnomaliesDetectionService} specialized for processing
 * raw sequential pricing data mapped to the {@link DataRecord} structure.
 * <p>It maps records across 5 structural features: Open, High, Low, Close, and Volume.</p>
 */
public class BaseDataRecordAnomaliesDetectionService extends BaseAnomaliesDetectionService<DataRecord> {

    /**
     * {@inheritDoc}
     * <p>Parses the 5 base OHLCV attributes (Open, High, Low, Close, Volume) from {@link DataRecord} objects into a matrix.</p>
     */
    @Override
    protected double[][] parseData(List<DataRecord> data) {

        double[][] parsedData = new double[data.size()][5];
        int i = 0;

        for (DataRecord dataRecord : data) {
            parsedData[i][0] = dataRecord.getOpen();
            parsedData[i][1] = dataRecord.getHigh();
            parsedData[i][2] = dataRecord.getLow();
            parsedData[i][3] = dataRecord.getClose();
            parsedData[i++][4] = dataRecord.getVolume();
        }

        return parsedData;
    }

    /**
     * {@inheritDoc}
     * <p>Calculates the absolute mathematical averages for the 5 fundamental OHLCV dimensions.</p>
     */
    @Override
    protected double[] calculateMeans(List<DataRecord> data) {

        if (data == null || data.isEmpty())
            return new double[]{0, 0, 0, 0, 0};

        double[] sums = new double[5];

        for (DataRecord record : data) {
            sums[0] += record.getOpen();
            sums[1] += record.getHigh();
            sums[2] += record.getLow();
            sums[3] += record.getClose();
            sums[4] += record.getVolume();
        }

        double[] means = new double[5];
        for (int i = 0; i < 5; i++) {
            means[i] = sums[i] / data.size();
        }

        return means;
    }

    /**
     * {@inheritDoc}
     * <p>Identifies spatial distance variances relative to the 5 baseline OHLCV variables.</p>
     */
    @Override
    protected Map<String, Double> calculateContributions(IsolationForest isolationForest, double[] record, List<DataRecord> trainingData) {

        double[] means = calculateMeans(trainingData);
        String[] featureNames = {"Open", "High", "Low", "Close", "Volume"};
        Map<String, Double> contributions = new HashMap<>();

        double meansScore = isolationForest.score(means);
        double[] rawImpacts = new double[5];
        double totalImpact = 0.0;

        for (int i = 0; i < 5; i++) {

            double[] testPoint = means.clone();
            testPoint[i] = record[i];

            double newScore = isolationForest.score(testPoint);
            rawImpacts[i] = Math.abs(newScore - meansScore);
            totalImpact += rawImpacts[i];
        }

        for (int i = 0; i < 5; i++) {

            double percentage = 0;

            if (totalImpact > 0)
                percentage = (rawImpacts[i] / totalImpact) * 100;

            contributions.put(featureNames[i], percentage);
        }

        return contributions;
    }
}