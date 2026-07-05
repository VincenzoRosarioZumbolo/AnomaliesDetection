package app.gui.views;

import app.controller.Controller;
import app.model.AnomalyResult;
import app.model.AppState;
import app.model.FinancialIndicators;

import java.time.LocalDateTime;
import java.util.List;

public class FinancialIndicatorsAnomalyDetectionPanel extends BaseAnomalyDetectionPanel<FinancialIndicators> {

    @Override
    protected void performSearch(String implementation, LocalDateTime startDate, String threshold, String treesNumber) throws Exception {

        Controller.getInstance().searchForFinancialIndicatorsAnomaly(implementation, startDate, threshold, treesNumber);
    }

    @Override
    protected List<AnomalyResult<FinancialIndicators>> getAnomalyResults() {
        return AppState.getInstance().getFinancialIndicatorsAnomalyResults();
    }

    @Override
    protected String[] getFeatureNames() {
        return new String[]{"RSI", "MACD", "ATR", "CMF"};
    }
}