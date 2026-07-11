package app.service;

import app.dto.FinancialIndicators;
import app.dto.FinancialIndicatorsPeriods;

import java.util.List;

/**
 * Service for calculating and managing financial technical indicators.
 * <p>
 * Defines the contract for components responsible for the synchronized processing
 * of momentum, trend, volatility, and volume metrics (RSI, MACD, ATR, CMF).
 * </p>
 */
public interface IndicatorsService {

    /**
     * Simultaneously computes RSI, MACD, ATR, and CMF technical indicators.
     *
     * @param periods     A container holding the explicit period configurations for each indicator.
     * @return A populated {@link List} containing the dto entities {@link FinancialIndicators} containing the results.
     */
    List<FinancialIndicators> calculateRSInMACDnATRnCMF(FinancialIndicatorsPeriods periods);
}