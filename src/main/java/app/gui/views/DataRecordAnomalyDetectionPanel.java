package app.gui.views;

import app.controller.Controller;
import app.dto.AnomalyResult;
import app.dto.AppState;
import app.dto.DataRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Concrete specialization of {@link BaseAnomalyDetectionPanel} aimed at configuring
 * and visually inspecting anomalies found in raw pricing records (OHLCV).
 */
public class DataRecordAnomalyDetectionPanel extends BaseAnomalyDetectionPanel<DataRecord> {

    /**
     * {@inheritDoc}
     * <p>
     * Channels the execution to the controller method dedicated to searching for anomalies
     * in historical raw market data.
     * </p>
     */
    @Override
    protected void performSearch(String implementation, LocalDateTime startDate, String threshold, String treesNumber) throws Exception {
        Controller.getInstance().searchForDataRecordAnomaly(implementation, startDate, threshold, treesNumber);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the calculated result vectors for {@link DataRecord} structures from the centralized application state.
     * </p>
     */
    @Override
    protected List<AnomalyResult<DataRecord>> getAnomalyResults() {
        return AppState.getInstance().getDataRecordAnomalyResults();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Defines the 5 structural dimensions associated with stock quotes: Open, Close, High, Low, and Volume.
     * </p>
     */
    @Override
    protected String[] getFeatureNames() {
        return new String[]{"Open", "Close", "High", "Low", "Volume"};
    }
}