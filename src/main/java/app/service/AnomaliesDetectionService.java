package app.service;

import app.model.AnomalyResult;
import app.model.DataRecord;
import smile.anomaly.IsolationForest;
import java.util.List;

/**
 * Service interface defining operations for training anomaly detection models
 * and evaluating datasets to identify abnormal financial records.
 */
public interface AnomaliesDetectionService {

    /**
     * Trains an Isolation Forest model using the provided baseline context training dataset.
     *
     * @param data A {@link List} of historical {@link DataRecord} instances used to establish normal behavior.
     * @return A trained {@link IsolationForest} model instance.
     */
    IsolationForest trainIsolationForest(List<DataRecord> data);

    /**
     * Evaluates a list of target financial records against a trained Isolation Forest model
     * to identify data points that exceed the specified abnormality threshold.
     *
     * @param isolationForest The pre-trained {@link IsolationForest} engine to use for evaluation.
     * @param data            The working {@link List} of target {@link DataRecord} instances to scan for anomalies.
     * @param threshold       The statistical contamination sensitivity limit mapping anomaly criteria.
     * @return A {@link List} containing the evaluated {@link AnomalyResult} metrics for elements flagged as anomalous.
     */
    List<AnomalyResult> searchForAnomaly(IsolationForest isolationForest, List<DataRecord> data, double threshold);
}