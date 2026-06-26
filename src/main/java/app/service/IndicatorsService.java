package app.service;

import app.model.FinancialIndicators;
import app.model.FinancialIndicatorsPeriods;

/**
 * Service for calculating and managing financial technical indicators.
 * <p>
 * Defines the contract for components responsible for the synchronized processing
 * of momentum, trend, volatility, and volume metrics (RSI, MACD, ATR, CMF).
 * </p>
 */
public interface IndicatorsService {

    /**
     * Simultaneously computes RSI, MACD, ATR, and CMF technical indicators for a given asset.
     * <p>
     * Implementation classes may apply caching or optimization strategies if the
     * requested periods match standard market baseline configurations.
     * </p>
     *
     * @param asset       The ticker symbol identifying the target asset.
     * @param granularity The time-frequency resolution interval of the dataset.
     * @param periods     A container holding the explicit period configurations for each indicator.
     * @return A populated {@link FinancialIndicators} model entity containing the results.
     */
    FinancialIndicators calculateRSInMACDnATRnCMF(String asset, String granularity, FinancialIndicatorsPeriods periods);
}