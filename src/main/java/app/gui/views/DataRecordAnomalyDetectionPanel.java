package app.gui.views;

import app.controller.Controller;
import app.model.AnomalyResult;
import app.model.AppState;
import app.model.DataRecord;

import java.time.LocalDateTime;
import java.util.List;

public class DataRecordAnomalyDetectionPanel extends BaseAnomalyDetectionPanel<DataRecord> {

    @Override
    protected void performSearch(String implementation, LocalDateTime startDate, String threshold, String treesNumber) throws Exception {
        Controller.getInstance().searchForDataRecordAnomaly(implementation, startDate, threshold, treesNumber);
    }

    @Override
    protected List<AnomalyResult<DataRecord>> getAnomalyResults() {
        return AppState.getInstance().getDataRecordAnomalyResults();
    }

    @Override
    protected String[] getFeatureNames() {
        return new String[]{"Open", "Close", "High", "Low", "Volume"};
    }
}