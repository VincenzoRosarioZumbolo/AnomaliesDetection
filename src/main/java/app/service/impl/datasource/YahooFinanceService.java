package app.service.impl.datasource;

import app.client.ApiClient;
import app.exception.ApiException;
import app.exception.DataParsingException;
import app.exception.NetworkException;
import app.dto.DataRecord;
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

/**
 * Concrete implementation of {@link DataSourceService} interacting with Yahoo Finance
 * to source historical asset equity metrics.
 * <p>Features local multi-tier architecture mapping that checks existing local CSV file cache segments
 * before dispatching outbound HTTP client request handshakes to the server endpoints.</p>
 */
public class YahooFinanceService implements DataSourceService {

    /**
     * Synchronizes and acquires a historical data record array matching specific asset parameters.
     * Evaluates local folder rows first. If cache metrics are insufficient, it initiates a fresh remote handshake,
     * parses the response text, merges records seamlessly, writes back to disk storage, and returns the list.
     *
     * @param asset       The target financial ticker label descriptor.
     * @param granularity The timeline frequency bar resolution interval.
     * @param startDate   The baseline beginning calendar date boundary marking requests.
     * @param endDate     The terminal concluding calendar date boundary marking requests.
     * @return A consolidated chronological {@link List} containing matching {@link DataRecord} results.
     * @throws NetworkException     If the remote host handshake breaks or times out.
     * @throws ApiException  If the destination processing server responds with error headers.
     * @throws DataParsingException If structural payloads from remote systems violate mapping formats.
     */
    @Override
    public List<DataRecord> fetchData(String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws NetworkException, ApiException, DataParsingException {

        String fileName = mapAssetToTicker(asset).replace("%5E", "^") + "_" + granularity + ".csv";

        try {
            List<DataRecord> localData = CsvStorageService.loadDataRecordsFromCsv(fileName, startDate, endDate);

            if (isCacheSufficient(localData, startDate, endDate, granularity)) {
                LoggerUtil.logApiRequest("Cache", asset, "Data fetched from local cache.");
                return localData;
            }
        } catch (IOException e) {
            LoggerUtil.logError("DataService", "Local cache reading error: " + e.getMessage());
        }

        long period1 = startDate.toEpochSecond(ZoneOffset.UTC);
        long period2 = endDate.toEpochSecond(ZoneOffset.UTC);
        String ticker = mapAssetToTicker(asset);
        String interval = mapGranularityToInterval(granularity);

        String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?period1=%d&period2=%d&interval=%s",
                ticker, period1, period2, interval);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        var response = ApiClient.getInstance().sendRequest(requestBuilder);

        try {
            JsonNode rootNode = ApiClient.getInstance().getObjectMapper().readTree(response.body());
            YahooFinanceParser parser = new YahooFinanceParser();
            List<DataRecord> remoteRecords = parser.parseData(rootNode);

            CsvStorageService.saveDataRecordsToCsv(fileName, remoteRecords);

            return CsvStorageService.loadDataRecordsFromCsv(fileName, startDate, endDate);

        } catch (IOException e) {
            LoggerUtil.logError("DataService", "Error managing data storage: " + e.getMessage());
            throw new DataParsingException("Failed to save or process local data serialization.");
        }
    }

    /**
     * Translates application user interface frequency names into explicit Yahoo Finance API structural format tags.
     *
     * @param granularity The plain text timeline bar resolution context.
     * @return The API matching shorthand code keyword mapping.
     */
    private String mapGranularityToInterval(String granularity) {

        return switch (granularity) {
            case "Minutely" -> "1m";
            case "Hourly" -> "1h";
            case "Daily" -> "1d";
            default -> "";
        };
    }

    /**
     * Maps user-friendly financial security naming descriptions into corresponding market ticker lookup codes.
     *
     * @param asset The user interface targeted readable parameter keyword.
     * @return The matching standard financial symbol shorthand.
     */
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

    /**
     * Performs a deterministic coverage verification check to confirm whether local data rows are sufficient
     * to fulfill a specified temporal range without triggering remote handshakes.
     *
     * @param localData   The active structural dataset collection pulled from disk storage files.
     * @param start       The requested tracking start constraint parameter.
     * @param end         The requested tracking end constraint parameter.
     * @param granularity The data spacing tracking metric configuration.
     * @return {@code true} if local data sequences reliably bridge limits, otherwise {@code false}.
     */
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
            case "Daily" -> 86400L;
            default -> Long.MAX_VALUE;
        };

        long expectedCount = (secondsDifference / unitSeconds);

        return inRange.size() >= (expectedCount * 0.95);
    }
}