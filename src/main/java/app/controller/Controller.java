package app.controller;

import app.exception.*;
import app.model.AppState;
import app.model.DataRecord;
import app.model.FinancialIndicators;
import app.model.FinancialIndicatorsPeriods;
import app.service.AnomaliesDetectionService;
import app.service.AnomaliesDetectionServiceFactory;
import app.service.DataSourceService;
import app.service.impl.indicators.*;
import app.service.impl.datasource.YahooFinanceService;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * The main controller of the application, acting as the orchestrator between the user interface layer,
 * the application state, and the background services (data fetching, indicator calculation, and anomaly detection).
 * <p>This class implements the <b>Singleton</b> design pattern to ensure unified control across the application.</p>
 */
public class Controller {

    /**
     * The single active instance of the Controller class (Singleton pattern).
     */
    private static Controller instance;

    /**
     * Private constructor to prevent direct instantiation from outside the class.
     */
    private Controller() {}

    /**
     * Returns the unique instance of {@code Controller}. If the instance does not exist yet,
     * it is lazily initialized.
     *
     * @return The Singleton instance of {@link Controller}.
     */
    public static Controller getInstance() {
        if (instance == null)
            instance = new Controller();

        return instance;
    }

    /**
     * Validates the provided date range and fetches financial data for a specific asset and granularity.
     * The retrieved records, along with the query configuration, are saved directly into the {@link AppState}.
     *
     * @param asset       The financial ticker or asset symbol to search for.
     * @param granularity The time interval/frequency of the data points (e.g., daily, hourly).
     * @param startDate   The beginning of the timeframe to query. Must not be null.
     * @param endDate     The end of the timeframe to query. Must not be null or before the startDate.
     * @throws ValidationException  If either date is null, or if the startDate occurs after the endDate.
     * @throws NetworkException     If a network-related connection failure occurs during data fetching.
     * @throws ApiException         If the remote data provider returns an error response code.
     * @throws DataParsingException If the incoming raw data cannot be successfully mapped to {@link DataRecord} models.
     */
    public void searchData(String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws ValidationException, NetworkException, ApiException, DataParsingException {

        if (startDate == null || endDate == null)
            throw new ValidationException("Please select both start and end dates.");

        if (startDate.isAfter(endDate))
            throw new ValidationException("Start date cannot be after end date.");

        AppState.getInstance().setAsset(asset);
        AppState.getInstance().setGranularity(granularity);

        DataSourceService service = new YahooFinanceService();
        AppState.getInstance().setDataRecords(service.fetchData(asset, granularity, startDate, endDate));
    }

    /**
     * Calculates a suite of complex financial technical indicators (RSI, MACD, ATR, and CMF)
     * using the specified intervals, updating the global application state with the results.
     *
     * @param periods The configuration bundle specifying the timeframes and periods for each indicator calculation.
     * @throws InvalidParameterException If the provided parameter configuration contains invalid values for the calculation.
     */
    public void calculateRSInMACDnATRnCMF(FinancialIndicatorsPeriods periods) throws InvalidParameterException {

        AppState appState = AppState.getInstance();

        appState.setFinancialIndicators(new IndicatorsServiceImpl().
                calculateRSInMACDnATRnCMF(periods));
    }

    /**
     * Performs anomaly detection on the active price dataset. It dynamically builds the chosen detection service,
     * queries history for an isolated training dataset context up until the first recorded timestamp of active data,
     * fits an Isolation Forest model, evaluates the raw price rows against the given threshold, and stores the anomalies in {@link AppState}.
     *
     * @param implementation The identifier key for the anomaly detection algorithm implementation strategy.
     * @param startDate      The starting point from which to fetch training data historical context.
     * @param threshold      The numerical sensitivity/contamination threshold represented as a String to evaluate deviations.
     * @param treesNumber    The number of trees to build during the anomaly detection process.
     * @throws ValidationException       If parameters fail initialization requirements during building.
     * @throws NetworkException          If a connection issue blocks retrieving necessary background training data.
     * @throws ApiException              If the data server explicitly rejects the historical training data request.
     * @throws DataParsingException      If historical records from the data provider cannot be successfully deserialized.
     * @throws AnomalyDetectionException If a `RuntimeException` occurs wrapping the underlying training or scanning routines.
     */
    public void searchForDataRecordAnomaly(String implementation, LocalDateTime startDate, String threshold, String treesNumber)
            throws ValidationException, NetworkException, ApiException, DataParsingException, AnomalyDetectionException {

        double parsedThreshold = AnomaliesDetectionService.validateThreshold(threshold);
        int parsedTreesNumber = AnomaliesDetectionService.validateTreesNumber(treesNumber);
        AppState appState = AppState.getInstance();

        AnomaliesDetectionService<DataRecord> anomaliesDetectionService = AnomaliesDetectionServiceFactory.
                buildDataRecordAnomalyDetectionService(implementation, startDate);

        DataSourceService dataSourceService = new YahooFinanceService();

        List<DataRecord> trainingDataRecords = dataSourceService.fetchData(
                appState.getAsset(),
                appState.getGranularity(),
                startDate,
                LocalDateTime.ofInstant(appState.getDataRecords().getFirst().getTimestamp(), ZoneOffset.UTC)
        );

        try {
            appState.setDataRecordAnomalyResults(anomaliesDetectionService.searchForAnomaly(
                    anomaliesDetectionService.trainIsolationForest(trainingDataRecords, parsedTreesNumber),
                    appState.getDataRecords(),
                    parsedThreshold));

        } catch (RuntimeException e) {
            throw new AnomalyDetectionException("Internal error during anomaly detection logic", e);
        }
    }

    /**
     * Performs anomaly detection on the generated financial technical indicators dataset.
     * It queries historical data records, computes the relative technical indicators to establish a background baseline context,
     * fits an Isolation Forest model, evaluates the technical indicators series against the designated threshold,
     * and stores the resulting breakages inside the {@link AppState}.
     *
     * @param implementation The identifier key for the anomaly detection algorithm implementation strategy.
     * @param startDate      The starting point from which to fetch background historical records for training.
     * @param threshold      The numerical sensitivity/contamination threshold represented as a String to evaluate deviations.
     * @param treesNumber    The number of trees to build during the anomaly detection process.
     * @throws ValidationException       If parameters fail initialization requirements during building.
     * @throws NetworkException          If a connection issue blocks retrieving necessary background training data.
     * @throws ApiException              If the data server explicitly rejects the historical training data request.
     * @throws DataParsingException      If historical records from the data provider cannot be successfully deserialized.
     * @throws AnomalyDetectionException If a `RuntimeException` occurs wrapping the underlying training or scanning routines.
     */
    public void searchForFinancialIndicatorsAnomaly(String implementation, LocalDateTime startDate, String threshold, String treesNumber)
            throws ValidationException, NetworkException, ApiException, DataParsingException, AnomalyDetectionException {

        double parsedThreshold = AnomaliesDetectionService.validateThreshold(threshold);
        int parsedTreesNumber = AnomaliesDetectionService.validateTreesNumber(treesNumber);
        AppState appState = AppState.getInstance();

        AnomaliesDetectionService<FinancialIndicators> anomaliesDetectionService = AnomaliesDetectionServiceFactory.
                buildFinancialIndicatorsAnomalyDetectionService(implementation, startDate);

        DataSourceService dataSourceService = new YahooFinanceService();

        List<DataRecord> trainingDataRecords = dataSourceService.fetchData(
                appState.getAsset(),
                appState.getGranularity(),
                startDate,
                LocalDateTime.ofInstant(appState.getDataRecords().getFirst().getTimestamp(), ZoneOffset.UTC)
        );

        List<FinancialIndicators> trainingFinancialIndicators = new IndicatorsServiceImpl().calculateRSInMACDnATRnCMF(trainingDataRecords);

        try {
            appState.setFinancialIndicatorsAnomalyResults(anomaliesDetectionService.searchForAnomaly(
                    anomaliesDetectionService.trainIsolationForest(trainingFinancialIndicators, parsedTreesNumber),
                    appState.getFinancialIndicators(),
                    parsedThreshold));

        } catch (RuntimeException e) {
            throw new AnomalyDetectionException("Internal error during anomaly detection logic", e);
        }
    }
}