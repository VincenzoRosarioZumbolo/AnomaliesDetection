package app.service;

import app.exception.AnomalyDetectionException;
import app.exception.ValidationException;
import app.model.AppState;
import app.model.DataRecord;
import app.model.FinancialIndicators;
import app.service.impl.anomalies.BaseDataRecordAnomaliesDetectionService;
import app.service.impl.anomalies.BaseFinancialIndicatorsAnomaliesDetectionService;
import app.service.impl.anomalies.QuantumDataRecordAnomaliesDetectionService;
import app.service.impl.anomalies.QuantumFinancialIndicatorsAnomaliesDetectionService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Factory class responsible for configuring, validating, and instantiating
 * concrete implementations of the {@link AnomaliesDetectionService}.
 */
public class AnomaliesDetectionServiceFactory {

    /**
     * Dynamically instantiates a specified anomaly detection service implementation after verifying
     * that the provided boundary constraints and application data states are fully valid.
     *
     * @param implementation The case-sensitive identifier name of the target service logic algorithm strategy (e.g., "Base implementation", "Quantum implementation").
     * @param startDate      The beginning timeline date marking historical training contextual data boundaries.
     * @return A fully initialized concrete implementation instance of {@link AnomaliesDetectionService}.
     * @throws ValidationException       if the training start date does not occur chronologically before active dataset records.
     * @throws AnomalyDetectionException If active state files contain no records to analyze, or if the specified strategy type cannot be resolved.
     */
    public static AnomaliesDetectionService<DataRecord> buildDataRecordAnomalyDetectionService(String implementation, LocalDateTime startDate)
            throws ValidationException, AnomalyDetectionException {

        AppState appState = AppState.getInstance();

        if (appState.getDataRecords() == null || appState.getDataRecords().isEmpty())
            throw new AnomalyDetectionException("No data available to search for anomalies. Perform a search first.");

        else if (startDate == null || !startDate.isBefore(LocalDateTime.ofInstant(appState.getDataRecords().getFirst().getTimestamp(), ZoneOffset.UTC)))
            throw new ValidationException("Please select a valid start date for training");

        return switch (implementation) {
            case "Base implementation" -> new BaseDataRecordAnomaliesDetectionService();
            case "Quantum implementation" -> new QuantumDataRecordAnomaliesDetectionService();
            default -> throw new AnomalyDetectionException("Anomaly detection service not found.");
        };
    }

    public static AnomaliesDetectionService<FinancialIndicators> buildFinancialIndicatorsAnomalyDetectionService(String implementation, LocalDateTime startDate)
            throws ValidationException, AnomalyDetectionException {

        AppState appState = AppState.getInstance();

        if (appState.getDataRecords() == null || appState.getDataRecords().isEmpty())
            throw new AnomalyDetectionException("No data available to search for anomalies. Perform a search first.");

        else if (startDate == null || !startDate.isBefore(LocalDateTime.ofInstant(appState.getDataRecords().getFirst().getTimestamp(), ZoneOffset.UTC)))
            throw new ValidationException("Please select a valid start date for training");

        return switch (implementation) {
            case "Base implementation" -> new BaseFinancialIndicatorsAnomaliesDetectionService();
            case "Quantum implementation" -> new QuantumFinancialIndicatorsAnomaliesDetectionService();
            default -> throw new AnomalyDetectionException("Anomaly detection service not found.");
        };
    }
}