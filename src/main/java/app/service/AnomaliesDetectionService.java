package app.service;

import app.exception.ValidationException;
import app.dto.AnomalyResult;
import app.dto.TimeSeriesRow;
import smile.anomaly.IsolationForest;
import java.util.List;

/**
 * Service interface defining operations for training anomaly detection models
 * and evaluating datasets to identify abnormal financial records.
 *
 * @param <T> The underlying time-series data row type under evaluation, extending {@link TimeSeriesRow}.
 */
public interface AnomaliesDetectionService<T extends TimeSeriesRow> {

    /**
     * Trains an Isolation Forest dto using the provided baseline context training dataset.
     *
     * @param data A {@link List} of generic historical time-series rows used to establish normal behavior.
     * @param treesNumber The number of trees to build during the anomaly detection process.
     * @return A trained {@link IsolationForest} dto instance.
     */
    IsolationForest trainIsolationForest(List<T> data, int treesNumber);

    /**
     * Evaluates a list of target financial records against a trained Isolation Forest dto
     * to identify data points that exceed the specified abnormality threshold.
     *
     * @param isolationForest The pre-trained {@link IsolationForest} engine to use for evaluation.
     * @param data            The working {@link List} of target generic rows to scan for anomalies.
     * @param threshold       The statistical contamination sensitivity limit mapping anomaly criteria.
     * @return A {@link List} containing the evaluated {@link AnomalyResult} metrics for elements flagged as anomalous.
     */
    List<AnomalyResult<T>> searchForAnomaly(IsolationForest isolationForest, List<T> data, double threshold);

    /**
     * Validates and parses a string representation of the contamination threshold.
     * <p>
     * The threshold must be a valid floating-point number strictly between 0 and 1.
     * </p>
     *
     * @param threshold The string representation of the threshold to validate.
     * @return The parsed {@code double} value of the threshold if valid.
     * @throws ValidationException If the threshold is not a valid number, or if it is outside the range (0, 1).
     */
    static double validateThreshold(String threshold)  throws ValidationException {

        double parsedThreshold;

        try {
            parsedThreshold = Double.parseDouble(threshold);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid threshold value: " + threshold);
        }

        if (parsedThreshold >= 1 || parsedThreshold <= 0)
            throw new ValidationException("Threshold must be a number between 0 and 1");

        return parsedThreshold;
    }

    /**
     * Validates and parses a string representation of the number of trees.
     * <p>
     * The number of trees must be a valid integer strictly greater than 0.
     * </p>
     *
     * @param treesNumber The string representation of the number of trees to validate.
     * @return The parsed {@code int} value of the trees number if valid.
     * @throws ValidationException If the value is not a valid integer, or if it is less than or equal to 0.
     */
    static int validateTreesNumber(String treesNumber)  throws ValidationException {

        int parsedTreesNumber;

        try {
            parsedTreesNumber = Integer.parseInt(treesNumber);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid number of trees: " + treesNumber);
        }

        if (parsedTreesNumber <= 0)
            throw new ValidationException("The number of trees must be greater than 0");

        return parsedTreesNumber;
    }
}