package app.service.impl.indicators;

import app.model.AppState;
import app.model.DataRecord;
import app.model.FinancialIndicators;
import app.model.FinancialIndicatorsPeriods;
import app.service.IndicatorsService;
import app.util.CsvStorageService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * High-level coordination service component that implements {@link IndicatorsService}.
 * <p>
 * Manages the simultaneous calculation of multiple technical indicators by dispatching data
 * to dedicated sub-calculators (RSI, MACD, ATR, CMF). It handles a local CSV file caching
 * mechanism to minimize recalculation overhead for standard parameter configurations.
 * </p>
 * <p>This class is structured as a non-thread-safe Singleton using lazy initialization.</p>
 */
public class IndicatorsCalculator implements IndicatorsService {

    /**
     * Singleton instance reference tracking handle.
     */
    private static IndicatorsCalculator instance;

    /** Standard default lookback constant for Relative Strength Index (RSI) evaluations. */
    private static final int STD_RSI = 14;

    /** Standard default Fast, Slow, and Signal smoothing allocations for MACD calculations. */
    private static final int[] STD_MACD = {12, 26, 9};

    /** Standard default lookback window configuration for Average True Range (ATR) assessments. */
    private static final int STD_ATR = 14;

    /** Standard default trailing benchmark timeline configuration for Chaikin Money Flow (CMF) metrics. */
    private static final int STD_CMF = 20;

    /**
     * Private constructor to prevent direct external instantiation (Singleton Pattern).
     */
    private IndicatorsCalculator() {}

    /**
     * Retrieves or initializes the global active instance of the calculator service.
     *
     * @return The active {@link IndicatorsCalculator} Singleton reference handle.
     */
    public static IndicatorsCalculator getInstance() {
        if (instance == null)
            instance = new IndicatorsCalculator();

        return instance;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation checks the local CSV storage cache first if standard lookback periods
     * are requested. In case of a cache miss or non-standard periods, it triggers a live calculation
     * loop and updates the disk cache if eligible.
     * </p>
     */
    @Override
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

    /**
     * Dispatches current application data records directly to individual sub-calculators
     * to compile the multi-indicator matrix.
     *
     * @param periods Parameter settings container holding period configs.
     * @return A newly built {@link FinancialIndicators} instance containing fresh calculations.
     */
    private FinancialIndicators calculateIndicators(FinancialIndicatorsPeriods periods) {

        AppState appState = AppState.getInstance();

        double rsi = RSICalculator.calculateRSI(periods.getRSIPeriod(), appState.getClosures());
        double macd = MACDCalculator.calculateMACD(periods.getMACDPeriod(), appState.getClosures());
        double atr = ATRCalculator.calculateATR(periods.getATRPeriod(), appState.getHighs(), appState.getLows(), appState.getClosures());
        double cmf = CMFCalculator.calculateCMF(periods.getCMFPeriod(), appState.getHighs(), appState.getLows(), appState.getClosures(), appState.getVolumes());

        FinancialIndicators financialIndicators = new FinancialIndicators(rsi, macd, atr, cmf);
        AppState.getInstance().setFinancialIndicators(financialIndicators);

        return financialIndicators;
    }

    /**
     * Checks local indicator logs on disk to pull pre-calculated results for standard runs.
     *
     * @param fileName Target destination filename mapped to disk files.
     * @return The mapped {@link FinancialIndicators} if existing rows match parameters, or {@code null} if a cache miss occurs.
     * @throws IOException If parsing, reading, or streaming files from disk fails.
     */
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

    /**
     * Validates whether a given periods context matches standard technical baseline criteria.
     *
     * @param periods Target indicator intervals package under review.
     * @return {@code true} if parameters perfectly match system standard values, otherwise {@code false}.
     */
    private boolean isStandard(FinancialIndicatorsPeriods periods) {
        return periods.getRSIPeriod() == STD_RSI &&
                periods.getMACDPeriod()[0] == STD_MACD[0] &&
                periods.getMACDPeriod()[1] == STD_MACD[1] &&
                periods.getMACDPeriod()[2] == STD_MACD[2] &&
                periods.getATRPeriod() == STD_ATR &&
                periods.getCMFPeriod() == STD_CMF;
    }

    /**
     * Persists standardized calculated indicators directly to local disk tracking sheets.
     *
     * @param fileName Target destination filename identifier.
     */
    private void saveIndicators(String fileName) {

        List<DataRecord> dataRecords = AppState.getInstance().getDataRecords();

        if (dataRecords == null || dataRecords.isEmpty()) return;

        Instant lastTimestamp = dataRecords.getLast().getTimestamp();

        try {
            CsvStorageService.saveIndicatorsToCsv(fileName, lastTimestamp, dataRecords.size(), AppState.getInstance().getFinancialIndicators());
        } catch (IOException e) {
            System.err.println("Cannot save financial values to: " + fileName);
        }
    }
}