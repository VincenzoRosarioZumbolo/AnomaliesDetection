package app.service.impl.anomalies;

import app.dto.AnomalyResult;
import app.dto.TimeSeriesRow;
import app.service.AnomaliesDetectionService;
import smile.anomaly.IsolationForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link app.service.AnomaliesDetectionService} that utilizes
 * the Smile library's {@link IsolationForest} algorithm to detect structural deviations
 * and anomalies within financial datasets.
 * <p>This service tracks the historical training context data used for each initialized dto instance,
 * maps multidimensional generic records into double primitives, evaluates anomaly scores against a percentage
 * sensitivity ceiling, and calculates feature impact contributions to explain detected anomalies.</p>
 *
 * @param <T> The underlying time-series data row type under evaluation, extending {@link TimeSeriesRow}.
 */
public abstract class BaseAnomaliesDetectionService<T extends TimeSeriesRow> implements AnomaliesDetectionService<T> {

    /**
     * An internal cache mapping each instantiated {@link IsolationForest} engine back to the specific
     * collection list of historical training records used to build and train its baseline context.
     */
    private final Map<IsolationForest, List<T>> trainingDataMap = new HashMap<>();

    /**
     * Fits and builds an Isolation Forest mathematical dto configured with an optimized tree ceiling profile
     * based on the size of the incoming collection and the given number of trees. Stores the initialized training context on completion.
     *
     * @param data A {@link List} of historical generic time-series instances used to establish normal behavior bounds.
     * @param treesNumber The number of trees to build during the anomaly detection process.
     * @return A fully trained, structurally optimized {@link IsolationForest} dto instance.
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
     * Evaluates a collection of active target financial records against the specified pre-trained dto
     * to identify elements exceeding the anomaly threshold.
     *
     * @param isolationForest The pre-trained {@link IsolationForest} engine used for scoring.
     * @param data            The working {@link List} of target generic rows to scan for anomalies.
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
     * independent variable influenced a specific anomaly score.
     * <p>Calculations evaluate spatial distance impacts by substituting one variable at a time
     * against the aggregate mean markers derived from the associated baseline training collection.</p>
     *
     * @param isolationForest The active {@link IsolationForest} dto context evaluating scores.
     * @param record          An array containing the primitive parameters of the specific data row under review.
     * @param trainingData    The collection of historical baseline records matching the active dto's state.
     * @return A {@link Map} pairing string feature labels to their computed impact contribution weight percentages.
     */
    abstract protected Map<String, Double> calculateContributions(IsolationForest isolationForest, double[] record, List<T> trainingData);

    /**
     * Computes the mathematical mean values across all financial data dimensions from
     * a collection of data records to form a centralized operational reference point.
     *
     * @param data The baseline source list of generic data structures.
     * @return An array tracking sequentially the calculated average for the specific mapped dimensions.
     */
    abstract protected double[] calculateMeans(List<T> data);

    /**
     * Transforms a generic collection list of financial domain record objects into a primitive 2D matrix structure
     * format compatible with the underlying mathematical library matrices.
     *
     * @param data The working {@link List} containing generic time-series instances.
     * @return A primitive 2D double matrix tracking dimensional mapped values.
     */
    abstract protected double[][] parseData(List<T> data);
}