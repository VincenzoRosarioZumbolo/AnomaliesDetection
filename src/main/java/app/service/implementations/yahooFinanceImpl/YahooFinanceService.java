package app.service.implementations.yahooFinanceImpl;

import app.client.ApiClient;
import app.exception.ApiRequestException;
import app.exception.DataParsingException;
import app.exception.NetworkException;
import app.model.DataRecord;
import app.service.DataSourceService;
import app.util.CsvStorageService;
import app.util.LoggerUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class YahooFinanceService implements DataSourceService {

    @Override
    public List<DataRecord> fetchData(String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws NetworkException, ApiRequestException, DataParsingException {

        String fileName = mapAssetToTicker(asset).replace("%5E", "^") + "_" + granularity + ".csv";

        try {
            List<DataRecord> localData = CsvStorageService.loadDataRecordsFromCsv(fileName, startDate, endDate);

            if (isCacheSufficient(localData, startDate, endDate, granularity)) {
                LoggerUtil.logApiRequest("Cache", asset, "Data fetched from local cache.");
                return localData;
            }
        } catch (IOException e) {

            LoggerUtil.logError("DataService", "Local cache read error: " + e.getMessage());
        }

        return handleApiRequest(fileName, asset, granularity, startDate, endDate);
    }

    private List<DataRecord> handleApiRequest(String fileName, String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws NetworkException, ApiRequestException, DataParsingException {

        String ticker = mapAssetToTicker(asset);

        String interval = mapGranularityToInterval(granularity);

        String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=%s&period1=%d&period2=%d",
                ticker, interval, startDate.toEpochSecond(ZoneOffset.UTC), endDate.toEpochSecond(ZoneOffset.UTC));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .GET();

        String responseBody = ApiClient.getInstance().sendRequest(builder).body();

        JsonNode root;
        try {
            root = ApiClient.getInstance().getObjectMapper().readTree(responseBody);
        } catch (IOException e) {
            throw new DataParsingException("Critical error in JSON reply structure.");
        }

        List<DataRecord> dataRecords = new YahooFinanceParser().parseData(root);

        try {
            CsvStorageService.saveDataRecordsToCsv(fileName, dataRecords);
            LoggerUtil.logApiRequest("YahooFinance", asset, "Data fetched from API and saved to CSV.");
        } catch (IOException e) {
            LoggerUtil.logError("YahooFinance", "Unable to save data to local CSV cache. Running in-memory only. Error: " + e.getMessage());
        }

        return dataRecords;
    }

    private static String mapGranularityToInterval(String granularity) {

        return switch (granularity) {
            case "Minutely" -> "1m";
            case "Hourly" -> "1h";
            case "Daily" -> "1d";
            default -> "";
        };
    }

    private String mapAssetToTicker(String asset) {
        
        return switch (asset) {
            case "S&P 500" -> "%5EGSPC";
            case "Gold" -> "GC=F";
            case "Oil" -> "CL=F";
            case "USD index" -> "DX-Y.NYB";
            case "Bitcoin" -> "BTC-USD";
            default -> asset;
        };
    }

    public boolean isCacheSufficient(List<DataRecord> localData, LocalDateTime start, LocalDateTime end, String granularity) {

        if (localData == null || localData.isEmpty()) return false;

        Instant startRequested = start.toInstant(ZoneOffset.UTC);
        Instant endRequested = end.toInstant(ZoneOffset.UTC);

        List<DataRecord> inRange = localData.stream()
                .filter(r -> !r.getTimestamp().isBefore(startRequested) && !r.getTimestamp().isAfter(endRequested))
                .toList();

        long secondsDifference = endRequested.getEpochSecond() - startRequested.getEpochSecond();

        long unitSeconds = switch (granularity) {
            case "Minutely" -> 60L;
            case "Hourly" -> 3600L;
            default -> 86400L;
        };

        long expectedRecords = secondsDifference / unitSeconds;
        return inRange.size() >= (expectedRecords * 0.7);
    }
}