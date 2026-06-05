package app.service.implementations.baseAnomaliesDetectionImpl;

import app.model.AnomalyResult;
import app.model.DataRecord;
import smile.anomaly.IsolationForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link app.service.AnomaliesDetectionService} that utilizes
 * the Smile library's {@link IsolationForest} algorithm to detect structural deviations
 * and pricing anomalies within financial datasets.
 * * <p>This service tracks the historical training context data used for each initialized model instance,
 * maps multidimensional records into double primitives, evaluates anomaly scores against a percentage
 * sensitivity ceiling, and calculates feature impact contributions to explain detected anomalies.</p>
 */
public class BaseAnomaliesDetectionService implements app.service.AnomaliesDetectionService {

    /**
     * An internal cache mapping each instantiated {@link IsolationForest} engine back to the specific
     * collection list of historical training records used to build and train its baseline context.
     */
    private final Map<IsolationForest, List<DataRecord>> trainingDataMap = new HashMap<>();

    /**
     * Fits and builds an Isolation Forest mathematical model configured with an optimized tree ceiling profile
     * based on the size of the incoming collection. Stores the initialized training context on completion.
     *
     * @param data A {@link List} of historical {@link DataRecord} instances used to establish normal behavior bounds.
     * @return A fully trained, structurally optimized {@link IsolationForest} model instance.
     */
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

    /**
     * Evaluates a collection of active target financial records against the specified pre-trained model
     * to identify elements exceeding the anomaly threshold.
     *
     * @param isolationForest The pre-trained {@link IsolationForest} engine used for scoring.
     * @param data            The working {@link List} of target {@link DataRecord} instances to scan for anomalies.
     * @param threshold       The contamination sensitivity ceiling. Records with scores greater than this value are flagged.
     * @return A {@link List} containing parsed {@link AnomalyResult} items detailing the feature contribution breakages for flagged records.
     */
    public List<AnomalyResult> searchForAnomaly(IsolationForest isolationForest, List<DataRecord> data, double threshold) {

        double[][] parsedData = parseData(data);
        List<AnomalyResult> anomalies = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            double score = isolationForest.score(parsedData[i]);

            if (score > threshold) {
                anomalies.add(new AnomalyResult(
                        data.get(i),
                        score,
                        calculateContributions(isolationForest, parsedData[i], trainingDataMap.get(isolationForest))
                ));
            }
        }

        return anomalies;
    }

    /**
     * Computes the algorithmic feature contribution percentage breakdown explaining how heavily each
     * independent variable (Open, High, Low, Close, Volume) influenced a specific anomaly score.
     * * <p>Calculations evaluate spatial distance impacts by substituting one variable at a time
     * against the aggregate mean markers derived from the associated baseline training collection.</p>
     *
     * @param isolationForest The active {@link IsolationForest} model context evaluating scores.
     * @param record          An array containing the 5 primitive parameters of the specific data row under review.
     * @param trainingData    The collection of historical baseline records matching the active model's state.
     * @return A {@link Map} pairing string feature labels (e.g., "Open", "Volume") to their computed impact contribution weight percentages.
     */
    private Map<String, Double> calculateContributions(IsolationForest isolationForest, double[] record, List<DataRecord> trainingData) {

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

    /**
     * Computes the mathematical mean values across all financial data dimensions from
     * a collection of data records to form a centralized operational reference point.
     *
     * @param data The baseline source list of {@link DataRecord} structures.
     * @return An array of 5 indices tracking sequentially the calculated average for Open, High, Low, Close, and Volume.
     */
    private static double[] calculateMeans(List<DataRecord> data) {

        if (data == null || data.isEmpty())
            return new double[]{0, 0, 0, 0, 0};

        double[] sums = new double[5];

        for (DataRecord dataRecord : data) {
            sums[0] += dataRecord.getOpen();
            sums[1] += dataRecord.getHigh();
            sums[2] += dataRecord.getLow();
            sums[3] += dataRecord.getClose();
            sums[4] += dataRecord.getVolume();
        }

        double[] means = new double[5];
        for (int i = 0; i < 5; i++) {
            means[i] = sums[i] / data.size();
        }

        return means;
    }

    /**
     * Transforms a generic collection list of financial domain record objects into a primitive 2D matrix structure
     * format compatible with the underlying mathematical library matrices.
     *
     * @param data The working {@link List} containing {@link DataRecord} instances.
     * @return A primitive 2D double matrix tracking rows mapped to Open, High, Low, Close, and Volume values sequentially.
     */
    private double[][] parseData(List<DataRecord> data) {

        double[][] parsedData = new double[data.size()][5];

        for (int i = 0; i < data.size(); i++) {
            parsedData[i][0] = data.get(i).getOpen();
            parsedData[i][1] = data.get(i).getHigh();
            parsedData[i][2] = data.get(i).getLow();
            parsedData[i][3] = data.get(i).getClose();
            parsedData[i][4] = data.get(i).getVolume();
        }

        return parsedData;
    }
}