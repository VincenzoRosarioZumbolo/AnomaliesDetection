package app.gui.views;

import app.controller.Controller;
import app.dto.AnomalyResult;
import app.dto.AppState;
import app.dto.FinancialIndicators;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Concrete specialization of {@link BaseAnomalyDetectionPanel} aimed at configuring and visually
 * inspecting structural anomalies detected on mathematical technical analysis indicators.
 */
public class FinancialIndicatorsAnomalyDetectionPanel extends BaseAnomalyDetectionPanel<FinancialIndicators> {

    /**
     * {@inheritDoc}
     * <p>
     * Channels the execution to the controller method dedicated to searching for anomalies in derived algorithmic indicator vectors.
     * </p>
     */
    @Override
    protected void performSearch(String implementation, LocalDateTime startDate, String threshold, String treesNumber) throws Exception {
        Controller.getInstance().searchForFinancialIndicatorsAnomaly(implementation, startDate, threshold, treesNumber);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the computed output data collections for the {@link FinancialIndicators} dto from the application state.
     * </p>
     */
    @Override
    protected List<AnomalyResult<FinancialIndicators>> getAnomalyResults() {
        return AppState.getInstance().getFinancialIndicatorsAnomalyResults();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Maps the 4 technical indicators calculated by the analysis engine: RSI, MACD, ATR, and CMF.
     * </p>
     */
    @Override
    protected String[] getFeatureNames() {
        return new String[]{"RSI", "MACD", "ATR", "CMF"};
    }
}