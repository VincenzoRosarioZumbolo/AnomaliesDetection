package app.model;

import lombok.Data;

import java.security.InvalidParameterException;

/**
 * Handles the configuration, validation, and parsing of calculation period windows
 * required by various financial technical indicator calculations.
 */
@Data
public class FinancialIndicatorsPeriods {

    /**
     * The period interval configuration for RSI (Relative Strength Index) calculations.
     */
    int RSIPeriod;

    /**
     * An internal array tracking the three mandatory structural interval periods for MACD calculations
     * (typically indexing fast period, slow period, and signaling smoothing period lines).
     */
    int[] MACDPeriod = new int[3];

    /**
     * The period interval configuration for ATR (Average True Range) calculations.
     */
    int ATRPeriod;

    /**
     * The period interval configuration for CMF (Chaikin Money Flow) calculations.
     */
    int CMFPeriod;

    public static final FinancialIndicatorsPeriods STANDARD_PERIODS = new FinancialIndicatorsPeriods
            ("14", new String[]{"12", "26", "9"}, "14", "20");

    /**
     * Constructs a validated instance of FinancialIndicatorsPeriods by parsing input string data parameters.
     *
     * @param RSIPeriod   The string text parsing window size for RSI calculations.
     * @param MACDPeriods A string array consisting of exactly 3 text targets configuration mapping for MACD.
     * @param ATRPeriod   The string text parsing window size for ATR calculations.
     * @param CMFPeriod   The string text parsing window size for CMF calculations.
     * @throws InvalidParameterException If any of the provided input string components fail numerical format conversion.
     */
    public FinancialIndicatorsPeriods(String RSIPeriod, String[] MACDPeriods, String ATRPeriod, String CMFPeriod) throws InvalidParameterException {

        try {
            this.RSIPeriod = Integer.parseInt(RSIPeriod);
            this.MACDPeriod[0] = Integer.parseInt(MACDPeriods[0]);
            this.MACDPeriod[1] = Integer.parseInt(MACDPeriods[1]);
            this.MACDPeriod[2] = Integer.parseInt(MACDPeriods[2]);
            this.ATRPeriod = Integer.parseInt(ATRPeriod);
            this.CMFPeriod = Integer.parseInt(CMFPeriod);

        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Invalid period");
        }
    }
}