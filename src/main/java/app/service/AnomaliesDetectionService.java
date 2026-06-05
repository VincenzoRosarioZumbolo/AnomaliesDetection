package app.service;

import app.model.AnomalyResult;
import app.model.DataRecord;
import smile.anomaly.IsolationForest;
import java.util.List;

public interface AnomaliesDetectionService {

    IsolationForest trainIsolationForest(List<DataRecord> data);

    List<AnomalyResult> searchForAnomaly(IsolationForest isolationForest, List<DataRecord> data, double threshold);
}
