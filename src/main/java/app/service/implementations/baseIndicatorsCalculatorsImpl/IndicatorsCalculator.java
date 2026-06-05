package app.service.implementations.baseIndicatorsCalculatorsImpl;

import app.model.AppState;
import app.model.DataRecord;
import app.model.FinancialIndicators;
import app.model.FinancialIndicatorsPeriods;
import app.util.CsvStorageService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class IndicatorsCalculator {

    private static IndicatorsCalculator instance;

    private static final int STD_RSI = 14;
    private static final int[] STD_MACD = {12, 26, 9};
    private static final int STD_ATR = 14;
    private static final int STD_CMF = 20;

    public static IndicatorsCalculator getInstance() {
        if (instance == null)
            instance = new IndicatorsCalculator();

        return instance;
    }

    public FinancialIndicators calculateRSInMACDnATRnCMF(String asset, String granularity, FinancialIndicatorsPeriods periods) {

        String fileName = asset.replace("%5E", "^") + "_" + granularity + "_indicators.csv";
        FinancialIndicators financialIndicators;

        if (isStandard(periods))
            try {
                financialIndicators = getIndicatorsFromCsv(fileName);

                if(financialIndicators != null)
                    return financialIndicators;
            } catch (IOException ignored){}

        financialIndicators = calculateIndicators(periods);

        if (isStandard(periods))
            saveIndicators(fileName);

        return financialIndicators;
    }

    private FinancialIndicators calculateIndicators(FinancialIndicatorsPeriods periods) {

        AppState appState = AppState.getInstance();

        List<Double> closures = appState.getClosures();
        List<Double> highs = appState.getHighs();
        List<Double> lows = appState.getLows();
        List<Long> volumes = appState.getVolumes();

        double RSI = RSICalculator.calculateRSI(periods.getRSIPeriod(), closures);
        double MACD = MACDCalculator.calculateMACD(periods.getMACDPeriod(), closures);
        double ATR = ATRCalculator.calculateATR(periods.getATRPeriod(), highs, lows, closures);
        double CMF = CMFCalculator.calculateCMF(periods.getCMFPeriod(), highs, lows, closures, volumes);

        return new FinancialIndicators(RSI, MACD, ATR, CMF);
    }

    private FinancialIndicators getIndicatorsFromCsv(String fileName) throws IOException {

        Instant lastTimeStamp = AppState.getInstance().getDataRecords().getLast().getTimestamp();

        Map<String, Double> cachedValues = CsvStorageService.loadIndicatorsFromCsv(
                fileName, lastTimeStamp, AppState.getInstance().getDataRecords().size());

        if (cachedValues != null)
            return new FinancialIndicators(
                    cachedValues.get("RSI"),
                    cachedValues.get("MACD"),
                    cachedValues.get("ATR"),
                    cachedValues.get("CMF")
            );

        return null;
    }

    private boolean isStandard(FinancialIndicatorsPeriods periods) {
        return periods.getRSIPeriod() == STD_RSI &&
                periods.getMACDPeriod()[0] == STD_MACD[0] &&
                periods.getMACDPeriod()[1] == STD_MACD[1] &&
                periods.getMACDPeriod()[2] == STD_MACD[2] &&
                periods.getATRPeriod() == STD_ATR &&
                periods.getCMFPeriod() == STD_CMF;
    }

    private void saveIndicators(String fileName) {

        List<DataRecord> dataRecords = AppState.getInstance().getDataRecords();

        if (dataRecords == null || dataRecords.isEmpty()) return;

        Instant lastTimestamp = dataRecords.getLast().getTimestamp();

        try {
            CsvStorageService.saveIndicatorsToCsv(fileName, lastTimestamp, dataRecords.size(), AppState.getInstance().getFinancialIndicators());
        } catch (IOException e) {
            System.err.println("Cannot save financial values to csv." + e.getMessage());
        }
    }
}
