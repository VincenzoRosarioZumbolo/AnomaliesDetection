package app.controller;

import app.exception.*;
import app.model.AppState;
import app.model.DataRecord;
import app.model.FinancialIndicatorsPeriods;
import app.service.AnomaliesDetectionService;
import app.service.AnomaliesDetectionServiceFactory;
import app.service.DataSourceService;
import app.service.implementations.baseIndicatorsCalculatorsImpl.*;
import app.service.implementations.yahooFinanceImpl.YahooFinanceService;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class Controller {

    private static Controller instance;

    private Controller() {}

    public static Controller getInstance() {
        if (instance == null)
            instance = new Controller();

        return instance;
    }

    public void searchData(String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws ValidationException, NetworkException, ApiRequestException, DataParsingException {

        if (startDate == null || endDate == null)
            throw new ValidationException("Please select both start and end dates.");

        if (startDate.isAfter(endDate))
            throw new ValidationException("Start date cannot be after end date.");

        AppState.getInstance().setAsset(asset);
        AppState.getInstance().setGranularity(granularity);

        DataSourceService service = new YahooFinanceService();
        AppState.getInstance().setDataRecords(service.fetchData(asset, granularity, startDate, endDate));
    }

    public void calculateRSInMACDnATRnCMF(FinancialIndicatorsPeriods periods) throws InvalidParameterException {

        AppState appState = AppState.getInstance();

        appState.setFinancialIndicators(IndicatorsCalculator.getInstance().
                calculateRSInMACDnATRnCMF(appState.getAsset(), appState.getGranularity(), periods));
    }

    public void searchForAnomaly(String implementation, LocalDateTime startDate, String threshold)
            throws ValidationException, NetworkException, ApiRequestException, DataParsingException, AnomalyDetectionException {

        AppState appState = AppState.getInstance();

        try {
            AnomaliesDetectionService anomaliesDetectionService = AnomaliesDetectionServiceFactory.buildAnomaliesDetectionService(implementation, threshold, startDate);

            DataSourceService dataSourceService = new YahooFinanceService();

            List<DataRecord> trainingDataRecords = dataSourceService.fetchData(
                    appState.getAsset(),
                    appState.getGranularity(),
                    startDate,
                    LocalDateTime.ofInstant(appState.getDataRecords().getFirst().getTimestamp(), ZoneOffset.UTC)
            );

            appState.setAnomalyResults(anomaliesDetectionService.searchForAnomaly(
                    anomaliesDetectionService.trainIsolationForest(trainingDataRecords),
                    appState.getDataRecords(),
                    Double.parseDouble(threshold)));
        } catch (RuntimeException e) {
            throw new AnomalyDetectionException("Internal error during anomaly detection logic", e);
        }
    }
}