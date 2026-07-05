package app.model;

import lombok.Data;

import java.util.Map;

/**
 * Represents the structured result of an anomaly detection evaluation on a specific financial data row.
 * <p>This model contains the analyzed generic record (such as raw price records or computed technical indicators),
 * its abnormality score, and an algorithmic mapping explaining the structural feature contributions toward the score.</p>
 *
 * @param <T> The underlying time-series data type under evaluation, implementing {@link TimeSeriesRow}.
 */
@Data
public class AnomalyResult<T> {

    /**
     * The targeted financial data record or indicator snapshot that underwent anomaly evaluation.
     */
    private T dataRecord;

    /**
     * The numerical score indicating the degree of abnormality assigned by the detection algorithm.
     */
    private double score;

    /**
     * A breakdown map detailing how heavily each structural feature parameter contributed to the computed anomaly score.
     */
    private Map<String, Double> contributions;

    /**
     * Constructs a fully initialized AnomalyResult entity.
     *
     * @param dataRecord    The evaluated data row instance.
     * @param score         The computed anomaly degree score.
     * @param contributions The structural parameter feature contribution weights map.
     */
    public AnomalyResult(T dataRecord, double score, Map<String, Double> contributions) {
        this.dataRecord = dataRecord;
        this.score = score;
        this.contributions = contributions;
    }
}