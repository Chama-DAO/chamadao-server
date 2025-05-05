package com.chama.chamadao_server.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CurrencyConversionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    private final BigDecimal testExchangeRate = new BigDecimal("130.00");
    private final BigDecimal testAmountKES = new BigDecimal("1000.00");
    private final BigDecimal testAmountUSDT = new BigDecimal("7.692308");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Replace the RestTemplate in the service with our mock
        ReflectionTestUtils.setField(currencyConversionService, "restTemplate", restTemplate);

        // Set default values for the properties
        ReflectionTestUtils.setField(currencyConversionService, "exchangeRateApiUrl", "https://v6.exchangerate-api.com/v6/87e1c74d3df1076e4dd856d2/latest/USD");
        ReflectionTestUtils.setField(currencyConversionService, "cacheMinutes", 60);
    }

    @Test
    public void testConvertKesToUsdt_Success() {
        // Setup
        mockExchangeRateApiResponse();

        // Execute
        BigDecimal result = currencyConversionService.convertKesToUsdt(testAmountKES);

        // Verify
        assertNotNull(result);
        assertEquals(testAmountUSDT.setScale(6, RoundingMode.HALF_UP), result);

        System.out.println("[DEBUG_LOG] Successfully converted KES to USDT: " + result);
    }

    @Test
    public void testConvertUsdtToKes_Success() {
        // Setup
        mockExchangeRateApiResponse();

        // Execute
        BigDecimal result = currencyConversionService.convertUsdtToKes(testAmountUSDT);

        // Verify
        assertNotNull(result);
        assertEquals(testAmountKES.setScale(2, RoundingMode.HALF_UP), result);

        System.out.println("[DEBUG_LOG] Successfully converted USDT to KES: " + result);
    }

    @Test
    public void testFallbackExchangeRate_WhenApiCallFails() {
        // Setup
        when(restTemplate.getForEntity(
                anyString(),
                eq(Object.class)))
                .thenThrow(new RuntimeException("API call failed"));

        // Execute
        BigDecimal result = currencyConversionService.convertKesToUsdt(testAmountKES);

        // Verify
        assertNotNull(result);
        // Should use the fallback rate of 130.00
        BigDecimal expected = testAmountKES.divide(new BigDecimal("130.00"), 6, RoundingMode.HALF_UP);
        assertEquals(expected, result);

        System.out.println("[DEBUG_LOG] Successfully used fallback exchange rate when API call failed");
    }

    private void mockExchangeRateApiResponse() {
        // Create a mock response that matches the structure expected by the service
        // We need to create an object that matches the ExchangeRateResponse class in CurrencyConversionService

        // Create a mock response entity
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(createMockExchangeRateResponse(), HttpStatus.OK);

        // Mock the RestTemplate to return our response
        when(restTemplate.getForEntity(
                anyString(),
                eq(Object.class)))
                .thenReturn(responseEntity);
    }

    private Object createMockExchangeRateResponse() {
        // Create a map that matches the structure of the ExchangeRateResponse class
        return new MockExchangeRateResponse();
    }

    // Mock classes to match the private classes in CurrencyConversionService
    private class MockExchangeRateResponse {
        private String base = "USD";
        private MockRates rates = new MockRates();

        public String getBase() {
            return base;
        }

        public MockRates getRates() {
            return rates;
        }
    }

    private class MockRates {
        private BigDecimal KES = testExchangeRate;

        public BigDecimal getKES() {
            return KES;
        }
    }
}
