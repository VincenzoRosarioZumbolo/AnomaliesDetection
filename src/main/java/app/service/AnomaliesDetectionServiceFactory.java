package app.service;

import app.exception.AnomalyDetectionException;
import app.exception.ValidationException;
import app.model.AppState;
import app.service.implementations.QuantumAnomaliesDetectionImpl.QuantumAnomaliesDetectionService;
import app.service.implementations.baseAnomaliesDetectionImpl.BaseAnomaliesDetectionService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class AnomaliesDetectionServiceFactory {

    public static app.service.AnomaliesDetectionService buildAnomaliesDetectionService(String implementation, String threshold, LocalDateTime startDate)
            throws ValidationException, AnomalyDetectionException {

        try {
            double parsedThreshold = Double.parseDouble(threshold);

            if (parsedThreshold >= 1 || parsedThreshold <= 0)
                throw new ValidationException("Threshold must be a number between 0 and 1");

            AppState appState = AppState.getInstance();

            if (appState.getDataRecords() == null || appState.getDataRecords().isEmpty())
                throw new AnomalyDetectionException("No data available to search for anomalies. Perform a search first.");

            else if (startDate == null || !startDate.isBefore(LocalDateTime.ofInstant(appState.getDataRecords().getFirst().getTimestamp(), ZoneOffset.UTC)))
                throw new ValidationException("Please select a valid start date for training");

            return switch (implementation) {
                case "Base implementation" -> new BaseAnomaliesDetectionService();
                case "Quantum implementation" -> new QuantumAnomaliesDetectionService();
                default -> throw new AnomalyDetectionException("Anomaly detection service not found.");
            };

        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid threshold value: " + threshold);
        }
    }
}
