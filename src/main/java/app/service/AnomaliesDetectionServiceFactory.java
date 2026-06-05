package app.service;

import app.exception.AnomalyDetectionException;
import app.exception.ValidationException;
import app.model.AppState;
import app.service.implementations.quantumAnomaliesDetectionImpl.QuantumAnomaliesDetectionService;
import app.service.implementations.baseAnomaliesDetectionImpl.BaseAnomaliesDetectionService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Factory class responsible for configuring, validating, and instantiating
 * concrete implementations of the {@link AnomaliesDetectionService}.
 */
public class AnomaliesDetectionServiceFactory {

    /**
     * Dynamically instantiates a specified anomaly detection service implementation after verifying
     * that the provided boundary constraints, thresholds, and application data states are fully valid.
     *
     * @param implementation The case-sensitive identifier name of the target service logic algorithm strategy (e.g., "Base implementation", "Quantum implementation").
     * @param threshold      The numerical sensitivity limit represented as a string value text to parse.
     * @param startDate      The beginning timeline date marking historical training contextual data boundaries.
     * @return A fully initialized concrete implementation instance of {@link AnomaliesDetectionService}.
     * @throws ValidationException       If the parsed threshold is out of the valid range (0, 1), if the string format is invalid, or if the training start date does not occur chronologically before active dataset records.
     * @throws AnomalyDetectionException If active state files contain no records to analyze, or if the specified strategy type cannot be resolved.
     */
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