package app.service.impl.anomalies;

import app.model.AnomalyResult;
import app.model.DataRecord;
import app.model.TimeSeriesRow;
import app.service.AnomaliesDetectionService;
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
public abstract class BaseAnomaliesDetectionService<T extends TimeSeriesRow> implements AnomaliesDetectionService<T> {

    /**
     * An internal cache mapping each instantiated {@link IsolationForest} engine back to the specific
     * collection list of historical training records used to build and train its baseline context.
     */
    private final Map<IsolationForest, List<T>> trainingDataMap = new HashMap<>();

    /**
     * Fits and builds an Isolation Forest mathematical model configured with an optimized tree ceiling profile
     * based on the size of the incoming collection and the given number of trees. Stores the initialized training context on completion.
     *
     * @param data A {@link List} of historical {@link DataRecord} instances used to establish normal behavior bounds.
     * @param treesNumber The number of trees to build during the anomaly detection process.
     * @return A fully trained, structurally optimized {@link IsolationForest} model instance.
     */
    public IsolationForest trainIsolationForest(List<T> data, int treesNumber) {

        IsolationForest isolationForest = IsolationForest.fit(
                parseData(data),
                treesNumber,
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
    public List<AnomalyResult<T>> searchForAnomaly(IsolationForest isolationForest, List<T> data, double threshold) {

        double[][] parsedData = parseData(data);
        List<AnomalyResult<T>> anomalies = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            double score = isolationForest.score(parsedData[i]);

            anomalies.add(new AnomalyResult<>(
                    data.get(i),
                    score,
                    calculateContributions(isolationForest, parsedData[i], trainingDataMap.get(isolationForest))
            ));
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
    abstract protected Map<String, Double> calculateContributions(IsolationForest isolationForest, double[] record, List<T> trainingData);

    /**
     * Computes the mathematical mean values across all financial data dimensions from
     * a collection of data records to form a centralized operational reference point.
     *
     * @param data The baseline source list of {@link DataRecord} structures.
     * @return An array of 5 indices tracking sequentially the calculated average for Open, High, Low, Close, and Volume.
     */
    abstract protected double[] calculateMeans(List<T> data);

    /**
     * Transforms a generic collection list of financial domain record objects into a primitive 2D matrix structure
     * format compatible with the underlying mathematical library matrices.
     *
     * @param data The working {@link List} containing {@link DataRecord} instances.
     * @return A primitive 2D double matrix tracking rows mapped to Open, High, Low, Close, and Volume values sequentially.
     */
    abstract protected double[][] parseData(List<T> data);
}