package app.model;

import lombok.Data;

import java.security.InvalidParameterException;

@Data
public class FinancialIndicatorsPeriods {

    int RSIPeriod;
    int[] MACDPeriod = new int[3];
    int ATRPeriod;
    int CMFPeriod;

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
