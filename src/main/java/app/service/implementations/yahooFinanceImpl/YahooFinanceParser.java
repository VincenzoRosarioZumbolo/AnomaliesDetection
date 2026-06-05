package app.service.implementations.yahooFinanceImpl;

import app.exception.DataParsingException;
import app.model.DataRecord;
import app.service.DataSourceParser;
import app.util.LoggerUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of {@link DataSourceParser} tailored to handle and map
 * the JSON payload responses specifically returned by the Yahoo Finance API.
 * <p>Extracts market charting metrics, filters out incomplete timelines, and translates nodes
 * into internal domain models.</p>
 */
public class YahooFinanceParser implements DataSourceParser {

    /**
     * Extracts multi-layer historical transactional pricing sequences from a Yahoo Finance
     * hierarchical JSON structural document tree, mapping valid lines into a continuous index.
     *
     * @param data The root Jackson {@link JsonNode} representing the raw server payload.
     * @return A mapped chronological {@link List} of entity model {@link DataRecord} instances.
     * @throws DataParsingException If structural elements fail schema validation constraints or if expected sub-nodes are missing.
     */
    @Override
    public List<DataRecord> parseData(JsonNode data) throws DataParsingException {

        try {
            List<DataRecord> records = new ArrayList<>();

            JsonNode result = data.path("chart").path("result").get(0);

            if (result == null || result.isMissingNode())
                throw new DataParsingException("The received data is empty or invalid for this asset.");

            JsonNode timestamps = result.path("timestamp");
            JsonNode indicators = result.path("indicators").path("quote").get(0);

            for (int i = 0; i < timestamps.size(); i++) {

                if (indicators.path("open").get(i).isNull())
                    continue;

                records.add(DataRecord.builder()
                        .timestamp(Instant.ofEpochSecond(timestamps.get(i).asLong()))
                        .open(indicators.path("open").get(i).asDouble())
                        .high(indicators.path("high").get(i).asDouble())
                        .low(indicators.path("low").get(i).asDouble())
                        .close(indicators.path("close").get(i).asDouble())
                        .volume(indicators.path("volume").get(i).asLong())
                        .build());
            }

            return records;

        } catch (RuntimeException e) {

            LoggerUtil.logError("Parser", "JSON Structure mismatch: " + e.getMessage());
            throw new DataParsingException("Error while parsing the data format. Check logs for details.");
        }
    }
}