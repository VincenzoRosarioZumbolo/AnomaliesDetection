package app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class AppState {

    private static AppState instance;

    @Getter@Setter
    private String granularity;
    @Getter@Setter
    private String asset;
    @Getter@Setter
    private List<DataRecord> dataRecords;
    @Getter@Setter
    private List<AnomalyResult> anomalyResults;
    @Getter@Setter
    private FinancialIndicators financialIndicators;

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null)
            instance = new AppState();

        return instance;
    }

    public List<Instant> getInstants() {
        return dataRecords.stream().map(DataRecord::getTimestamp).collect(Collectors.toList());
    }

    public List<Double> getOpens() {
        return dataRecords.stream().map(DataRecord::getOpen).collect(Collectors.toList());
    }

    public List<Double> getClosures() {
        return dataRecords.stream().map(DataRecord::getClose).collect(Collectors.toList());
    }

    public List<Double> getHighs() {
        return dataRecords.stream().map(DataRecord::getHigh).collect(Collectors.toList());
    }

    public List<Double> getLows() {
        return dataRecords.stream().map(DataRecord::getLow).collect(Collectors.toList());
    }

    public List<Long> getVolumes() {
        return dataRecords.stream().map(DataRecord::getVolume).collect(Collectors.toList());
    }
}

