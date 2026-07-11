package app.dto;

import java.time.Instant;

/**
 * Represents a standard, unified contract for any time-series data row within the system.
 * <p>Enables components like generic anomaly detection engines and dashboard view structures
 * to process diverse financial records polymorphically based on chronological parameters.</p>
 */
public interface TimeSeriesRow {

    /**
     * Retrieves the precise temporal point tracking when this row snapshot was recorded.
     *
     * @return An {@link Instant} representing the timeline marker for the row.
     */
    Instant getTimestamp();
}