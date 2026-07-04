package app.service.impl.anomalies;

import app.model.DataRecord;
import java.util.List;

public class QuantumDataRecordAnomaliesDetectionService extends QuantumAnomaliesDetectionService<DataRecord> {

    public QuantumDataRecordAnomaliesDetectionService() {

        FEATURE_NAMES = new String[]{"Open", "High", "Low", "Close", "Volume"};
        ORIGINAL_FEATURE_COUNT = FEATURE_NAMES.length;
        QUBITS = ORIGINAL_FEATURE_COUNT;
        QUANTUM_STATE_SIZE = 1 << QUBITS;
    }

    @Override
    protected double[][] parseData(List<DataRecord> data) {
        double[][] parsedData = new double[data.size()][ORIGINAL_FEATURE_COUNT];

        for (int i = 0; i < data.size(); i++) {
            parsedData[i][0] = data.get(i).getOpen();
            parsedData[i][1] = data.get(i).getHigh();
            parsedData[i][2] = data.get(i).getLow();
            parsedData[i][3] = data.get(i).getClose();
            parsedData[i][4] = data.get(i).getVolume();
        }

        return parsedData;
    }
}
