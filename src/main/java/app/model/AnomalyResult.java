package app.model;

import lombok.Data;

import java.util.Map;

@Data
public class AnomalyResult {

    private DataRecord dataRecord;
    private double score;
    private Map<String, Double> contributions;

    public AnomalyResult(DataRecord dataRecord, double score, Map<String, Double> contributions) {
        this.dataRecord = dataRecord;
        this.score = score;
        this.contributions = contributions;
    }
}
