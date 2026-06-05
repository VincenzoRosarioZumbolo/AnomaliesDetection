package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FinancialIndicators {
    private double RSI;
    private double MACD;
    private double ATR;
    private double CMF;
}
