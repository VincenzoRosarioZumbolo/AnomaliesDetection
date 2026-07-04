package app.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

/**
 * A data transfer and storage model object representing a single snapshot of financial OHLCV
 * (Open, High, Low, Close, Volume) data for a given timestamp.
 * <p>Equipped with Lombok's Builder pattern capabilities for flexible initialization.</p>
 */
@Data
@Builder
public class DataRecord implements TimeSeriesRow {

    /**
     * The point in time tracking when this particular snapshot record occurs.
     */
    private Instant timestamp;

    /**
     * The open transaction price recording at the start of the timeframe interval.
     */
    private double open;

    /**
     * The maximum achieved transactional peak boundary during the interval timeframe.
     */
    private double high;

    /**
     * The minimum achieved transactional low boundary during the interval timeframe.
     */
    private double low;

    /**
     * The absolute trailing transaction price matching the end of the interval timeframe.
     */
    private double close;

    /**
     * The aggregate amount count of units or tokens traded within the interval timeframe.
     */
    private long volume;
}