package app.service;

import app.exception.ApiException;
import app.exception.DataParsingException;
import app.exception.NetworkException;
import app.dto.DataRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Core interface responsible for abstracting remote connection handshakes and pulling
 * historical data metrics from specialized external financial market data providers.
 */
public interface DataSourceService {

    /**
     * Fetches, validates, and refines historical financial asset transaction pricing benchmarks
     * matching the specified scope configurations.
     *
     * @param asset       The ticker symbol identifying the targeted market asset.
     * @param granularity The time-frequency bar resolution interval required (e.g., daily, hourly).
     * @param startDate   The baseline beginning calendar date boundary marking requests.
     * @param endDate     The terminal concluding calendar date boundary marking requests.
     * @return A structured {@link List} consisting of populated chronological {@link DataRecord} instances.
     * @throws NetworkException     If the connection fails or drops due to local/remote networking errors.
     * @throws ApiException  If the data vendor processes requests but returns an unsuccesful or forbidden response status code.
     * @throws DataParsingException If the returned structured payload fails structural parsing requirements.
     */
    List<DataRecord> fetchData(String asset, String granularity, LocalDateTime startDate, LocalDateTime endDate)
            throws NetworkException, ApiException, DataParsingException;
}