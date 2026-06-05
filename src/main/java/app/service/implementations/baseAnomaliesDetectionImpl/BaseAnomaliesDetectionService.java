package app.service.implementations.baseAnomaliesDetectionImpl;

import app.model.AnomalyResult;
import app.model.DataRecord;
import smile.anomaly.IsolationForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAnomaliesDetectionService implements app.service.AnomaliesDetectionService {

    private final Map<IsolationForest, List<DataRecord>> trainingDataMap = new HashMap<>();

    public IsolationForest trainIsolationForest(List<DataRecord> data) {

        IsolationForest isolationForest = IsolationForest.fit(
                parseData(data),
                500,
                (int) Math.ceil(Math.log(data.size() * Math.min(0.9, 256.0 / data.size())) / Math.log(2)),
                Math.min(0.9, 256.0 / data.size()),
                1);

        trainingDataMap.put(isolationForest, data);

        return isolationForest;
    }

    public List<AnomalyResult> searchForAnomaly(IsolationForest isolationForest, List<DataRecord> data, double threshold) {

        double[][] parsedData = parseData(data);
        List<AnomalyResult> anomalies = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            double score = isolationForest.score(parsedData[i]);

            if (score >= threshold)
                anomalies.add(new AnomalyResult(data.get(i), score, getAnomalyContribution(isolationForest, parsedData[i])));
            else
                anomalies.add(new AnomalyResult(data.get(i), score, new HashMap<>()));
        }

        return anomalies;
    }

    private static double[][] parseData(List<DataRecord> data) {

        double[][] forestData = new double[data.size()][5];

        for (int i = 0; i < data.size(); i++) {

            forestData[i][0] = data.get(i).getOpen();
            forestData[i][1] = data.get(i).getHigh();
            forestData[i][2] = data.get(i).getLow();
            forestData[i][3] = data.get(i).getClose();
            forestData[i][4] = Math.log(data.get(i).getVolume());
        }

        return forestData;
    }

    public Map<String, Double> getAnomalyContribution(IsolationForest isolationForest, double[] record) {

        double[] means = calculateMeans(trainingDataMap.get(isolationForest));
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

    private static double[] calculateMeans(List<DataRecord> data) {

        if (data == null || data.isEmpty())
            return new double[]{0, 0, 0, 0, 0};

        double[] sums = new double[5];

        for (DataRecord dataRecord : data) {
            sums[0] += dataRecord.getOpen();
            sums[1] += dataRecord.getHigh();
            sums[2] += dataRecord.getLow();
            sums[3] += dataRecord.getClose();
            sums[4] += Math.log(dataRecord.getVolume());
        }

        double[] means = new double[5];

        for (int i = 0; i < 5; i++)
            means[i] = sums[i] / data.size();

        return means;
    }
}
