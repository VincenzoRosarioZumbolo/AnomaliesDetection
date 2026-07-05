package app.service.impl.anomalies;

import app.model.AnomalyResult;
import app.model.FinancialIndicators;
import smile.anomaly.IsolationForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Concrete implementation of the {@link QuantumAnomaliesDetectionService} specialized for processing
 * computed technical analysis metrics structured as {@link FinancialIndicators}.
 * <p>It initializes a 4-qubit quantum simulation state specifically tailored to encode RSI, MACD,
 * ATR, and CMF values, while seamlessly bypassing uncalculated historical initialization rows.</p>
 */
public class QuantumFinancialIndicatorsAnomaliesDetectionService extends QuantumAnomaliesDetectionService<FinancialIndicators> {

    /**
     * Instantiates the quantum anomaly service and prepares internal 4-feature state structures
     * aligning directly with the core technical analysis indicators evaluated.
     */
    public QuantumFinancialIndicatorsAnomaliesDetectionService() {

        FEATURE_NAMES = new String[]{"RSI", "MACD", "ATR", "CMF"};
        ORIGINAL_FEATURE_COUNT = FEATURE_NAMES.length;
        QUBITS = ORIGINAL_FEATURE_COUNT;
        QUANTUM_STATE_SIZE = 1 << QUBITS;
    }

    /**
     * Ensures the mathematical quantum models are not disrupted by empty sliding-window periods.
     *
     * @param data The chronological raw sequence of evaluated indicator records.
     * @return A trimmed sub-list excluding null historical rows.
     */
    private List<FinancialIndicators> filterWarmupData(List<FinancialIndicators> data) {
        if (data == null || data.isEmpty()) return data;

        int startIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            FinancialIndicators fi = data.get(i);
            if (fi.getRSI() != 0.0 && fi.getMACD() != 0.0 && fi.getATR() != 0.0 && fi.getCMF() != 0.0) {
                startIndex = i;
                break;
            }
        }
        return data.subList(startIndex, data.size());
    }

    /**
     * {@inheritDoc}
     * <p>Purges empty technical warmup parameters before triggering simulated quantum states during training.</p>
     */
    @Override
    public IsolationForest trainIsolationForest(List<FinancialIndicators> data, int treesNumber) {
        return super.trainIsolationForest(filterWarmupData(data), treesNumber);
    }

    /**
     * {@inheritDoc}
     * <p>Calculates the quantum deviations and restores the empty initialization points into the timeline stream
     * prior to charting result distributions.</p>
     */
    @Override
    public List<AnomalyResult<FinancialIndicators>> searchForAnomaly(IsolationForest isolationForest, List<FinancialIndicators> data, double threshold) {
        List<FinancialIndicators> validData = filterWarmupData(data);
        List<AnomalyResult<FinancialIndicators>> validResults = super.searchForAnomaly(isolationForest, validData, threshold);

        List<AnomalyResult<FinancialIndicators>> fullResults = new ArrayList<>();
        int warmupCount = data.size() - validData.size();

        for (int i = 0; i < warmupCount; i++) {
            fullResults.add(new AnomalyResult<>(data.get(i), 0.0, new HashMap<>()));
        }

        fullResults.addAll(validResults);

        return fullResults;
    }

    /**
     * {@inheritDoc}
     * <p>Extracts the sequential parsed metrics (RSI, MACD, ATR, CMF) array from the source entities.</p>
     */
    @Override
    protected double[][] parseData(List<FinancialIndicators> data) {
        double[][] parsedData = new double[data.size()][ORIGINAL_FEATURE_COUNT];

        for (int i = 0; i < data.size(); i++) {
            parsedData[i][0] = data.get(i).getRSI();
            parsedData[i][1] = data.get(i).getMACD();
            parsedData[i][2] = data.get(i).getATR();
            parsedData[i][3] = data.get(i).getCMF();
        }

        return parsedData;
    }
}