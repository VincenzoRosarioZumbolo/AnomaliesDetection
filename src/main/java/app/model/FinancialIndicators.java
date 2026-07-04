package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * A storage container entity grouping calculated strategic technical analysis indicator metrics.
 * <p>This model simultaneously captures values for Relative Strength Index (RSI), Moving Average Convergence
 * Divergence (MACD), Average True Range (ATR), and Chaikin Money Flow (CMF) metrics.</p>
 */
@Data
@AllArgsConstructor
public class FinancialIndicators implements TimeSeriesRow {

    /**
     * The point in time tracking when this particular snapshot record occurs.
     */
    private Instant timestamp;

    /**
     * The Relative Strength Index calculation evaluating recent price directional velocities.
     */
    private double RSI;

    /**
     * The Moving Average Convergence Divergence evaluation signaling trend shifts and momentum.
     */
    private double MACD;

    /**
     * The Average True Range calculation measuring asset market price volatility characteristics.
     */
    private double ATR;

    /**
     * The Chaikin Money Flow metric aggregating accumulation vs distribution weight signals over time.
     */
    private double CMF;
}