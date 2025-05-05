package com.chama.chamadao_server.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for currency conversion
 * Handles conversion between KES and USDT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${currency.exchange.api.url}")
    private String exchangeRateApiUrl;

    @Value("${currency.exchange.cache.minutes:60}")
    private int cacheMinutes;

    // Cache for exchange rates
    private final Map<String, ExchangeRateData> exchangeRateCache = new ConcurrentHashMap<>();

    /**
     * Convert KES to USDT
     * @param amountKES The amount in KES
     * @return The amount in USDT
     */
    public BigDecimal convertKesToUsdt(BigDecimal amountKES) {
        log.info("Converting {} KES to USDT", amountKES);

        BigDecimal exchangeRate = getKesUsdtExchangeRate();
        BigDecimal amountUSDT = amountKES.divide(exchangeRate, 6, RoundingMode.HALF_UP);

        log.info("Converted {} KES to {} USDT (rate: {})", amountKES, amountUSDT, exchangeRate);
        return amountUSDT;
    }

    /**
     * Convert USDT to KES
     * @param amountUSDT The amount in USDT
     * @return The amount in KES
     */
    public BigDecimal convertUsdtToKes(BigDecimal amountUSDT) {
        log.info("Converting {} USDT to KES", amountUSDT);

        BigDecimal exchangeRate = getKesUsdtExchangeRate();
        BigDecimal amountKES = amountUSDT.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        log.info("Converted {} USDT to {} KES (rate: {})", amountUSDT, amountKES, exchangeRate);
        return amountKES;
    }

    /**
     * Get the exchange rate between KES and USDT
     * @return The exchange rate (KES per USDT)
     */
    private BigDecimal getKesUsdtExchangeRate() {
        // Check if we have a cached rate that's still valid
        ExchangeRateData cachedData = exchangeRateCache.get("KES_USDT");
        if (cachedData != null && !cachedData.isExpired()) {
            log.debug("Using cached exchange rate: {}", cachedData.rate());
            return cachedData.rate();
        }

        // If not, fetch a new rate
        log.info("Fetching current KES/USDT exchange rate");

        try {
            // Make a simple GET request to the API
            ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(
                    exchangeRateApiUrl, ExchangeRateResponse.class);

            if (response.getBody() != null && response.getBody().getRates() != null) {
                BigDecimal kesRate = response.getBody().getRates().getKES();

                // Cache the rate
                ExchangeRateData newData = new ExchangeRateData(kesRate, System.currentTimeMillis(), cacheMinutes);
                exchangeRateCache.put("KES_USDT", newData);

                log.info("Fetched exchange rate: 1 USDT = {} KES", kesRate);
                return kesRate;
            } else {
                log.error("Failed to fetch exchange rate, response body is null or missing rates");
                // Return a fallback rate if API call fails
                return getFallbackExchangeRate();
            }
        } catch (Exception e) {
            log.error("Error fetching exchange rate", e);
            // Return a fallback rate if API call fails
            return getFallbackExchangeRate();
        }
    }

    /**
     * Get a fallback exchange rate if the API call fails
     * @return A fallback exchange rate
     */
    private BigDecimal getFallbackExchangeRate() {
        // Check if we have any cached rate, even if expired
        ExchangeRateData cachedData = exchangeRateCache.get("KES_USDT");
        if (cachedData != null) {
            log.warn("Using expired cached exchange rate: {}", cachedData.rate());
            return cachedData.rate();
        }

        // If no cached rate, use a hardcoded fallback
        BigDecimal fallbackRate = new BigDecimal("130.00"); // Approximate KES/USD rate
        log.warn("Using hardcoded fallback exchange rate: {}", fallbackRate);
        return fallbackRate;
    }

    /**
         * Class to hold exchange rate data with expiration
         */
        private record ExchangeRateData(BigDecimal rate, long timestamp, int validMinutes) {

        public boolean isExpired() {
                long currentTime = System.currentTimeMillis();
                long validTime = (long) validMinutes * 60 * 1000; // Convert minutes to milliseconds
                return currentTime - timestamp > validTime;
            }
        }

    /**
     * Response class for exchange rate API
     */
    @Setter
    @Getter
    private static class ExchangeRateResponse {
        private String base;
        private Rates rates;

    }

    /**
     * Rates class for exchange rate API response
     */
    @Setter
    @Getter
    private static class Rates {
        private BigDecimal KES;

    }
}
