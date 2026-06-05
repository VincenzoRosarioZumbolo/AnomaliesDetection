package app.service.implementations.quantumAnomaliesDetectionImpl;

import app.model.AnomalyResult;
import app.model.DataRecord;
import app.service.AnomaliesDetectionService;
import smile.anomaly.IsolationForest;

import java.util.List;

public class QuantumAnomaliesDetectionService implements AnomaliesDetectionService {

    @Override
    public IsolationForest trainIsolationForest(List<DataRecord> data) {return null;}

    @Override
    public List<AnomalyResult> searchForAnomaly(IsolationForest isolationForest, List<DataRecord> data, double threshold) {return null;}
}
