package app.service.impl.anomalies;

import app.model.AnomalyResult;
import app.model.FinancialIndicators;
import smile.anomaly.IsolationForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuantumFinancialIndicatorsAnomaliesDetectionService extends QuantumAnomaliesDetectionService<FinancialIndicators> {

    public QuantumFinancialIndicatorsAnomaliesDetectionService() {

        FEATURE_NAMES = new String[]{"RSI", "MACD", "ATR", "CMF"};
        ORIGINAL_FEATURE_COUNT = FEATURE_NAMES.length;
        QUBITS = ORIGINAL_FEATURE_COUNT;
        QUANTUM_STATE_SIZE = 1 << QUBITS;
    }

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

    @Override
    public IsolationForest trainIsolationForest(List<FinancialIndicators> data, int treesNumber) {
        return super.trainIsolationForest(filterWarmupData(data), treesNumber);
    }

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
