package app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds the centralized operational runtime state of the application.
 * <p>This class implements the <b>Singleton</b> pattern, providing global access to targeted assets,
 * active time granularity, fetched historical data rows, calculated indicators, and detected anomaly results.</p>
 */
public class AppState {

    /**
     * The single active state instance of the AppState class (Singleton pattern).
     */
    private static AppState instance;

    /**
     * The current historical timeframe/frequency interval configuration (e.g., daily, hourly).
     */
    @Getter@Setter
    private String granularity;

    /**
     * The active ticker identifier symbol or name representing the tracked financial asset.
     */
    @Getter@Setter
    private String asset;

    /**
     * The working collection list of pulled financial database records.
     */
    @Getter@Setter
    private List<DataRecord> dataRecords;

    /**
     * The working list tracking the results of the anomaly scanning operations.
     */
    @Getter@Setter
    private List<AnomalyResult> anomalyResults;

    /**
     * The structured wrapper hosting computed algorithmic financial indicators (RSI, MACD, etc.).
     */
    @Getter@Setter
    private FinancialIndicators financialIndicators;

    /**
     * Private constructor to enforce exclusive instantiation via the Singleton instance method.
     */
    private AppState() {}

    /**
     * Returns the unique runtime instance of {@code AppState}. If it does not exist yet,
     * it is lazily initialized.
     *
     * @return The centralized Singleton instance of {@link AppState}.
     */
    public static AppState getInstance() {
        if (instance == null)
            instance = new AppState();

        return instance;
    }

    /**
     * Extracts and compiles a list containing only the timestamps of the loaded data records.
     *
     * @return A {@link List} of {@link Instant} values representing record dates.
     */
    public List<Instant> getInstants() {
        return dataRecords.stream().map(DataRecord::getTimestamp).collect(Collectors.toList());
    }

    /**
     * Extracts and compiles a list containing only the opening pricing quotes of the loaded data records.
     *
     * @return A {@link List} of {@link Double} values representing opening market points.
     */
    public List<Double> getOpens() {
        return dataRecords.stream().map(DataRecord::getOpen).collect(Collectors.toList());
    }

    /**
     * Extracts and compiles a list containing only the closing market quotes of the loaded data records.
     *
     * @return A {@link List} of {@link Double} values representing closing market points.
     */
    public List<Double> getClosures() {
        return dataRecords.stream().map(DataRecord::getClose).collect(Collectors.toList());
    }

    /**
     * Extracts and compiles a list containing only the peak high market boundaries of the loaded data records.
     *
     * @return A {@link List} of {@link Double} values representing highest period points.
     */
    public List<Double> getHighs() {
        return dataRecords.stream().map(DataRecord::getHigh).collect(Collectors.toList());
    }

    /**
     * Extracts and compiles a list containing only the baseline low market boundaries of the loaded data records.
     *
     * @return A {@link List} of {@link Double} values representing lowest period points.
     */
    public List<Double> getLows() {
        return dataRecords.stream().map(DataRecord::getLow).collect(Collectors.toList());
    }

    /**
     * Extracts and compiles a list containing only the volume metrics of the loaded data records.
     *
     * @return A {@link List} of {@link Long} values representing periodic trading volumes.
     */
    public List<Long> getVolumes() {
        return dataRecords.stream().map(DataRecord::getVolume).collect(Collectors.toList());
    }
}