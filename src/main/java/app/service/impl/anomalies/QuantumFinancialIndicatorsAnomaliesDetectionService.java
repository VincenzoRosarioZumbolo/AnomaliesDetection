package app.service.impl.anomalies;

import app.model.FinancialIndicators;
import java.util.List;

public class QuantumFinancialIndicatorsAnomaliesDetectionService extends QuantumAnomaliesDetectionService<FinancialIndicators> {

    public QuantumFinancialIndicatorsAnomaliesDetectionService() {

        FEATURE_NAMES = new String[]{"RSI", "MACD", "ATR", "CMF"};
        ORIGINAL_FEATURE_COUNT = FEATURE_NAMES.length;
        QUBITS = ORIGINAL_FEATURE_COUNT;
        QUANTUM_STATE_SIZE = 1 << QUBITS;
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
