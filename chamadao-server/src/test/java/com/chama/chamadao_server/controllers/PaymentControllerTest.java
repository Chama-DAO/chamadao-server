package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.dto.mpesa.MpesaB2CCallback;
import com.chama.chamadao_server.models.dto.mpesa.MpesaB2CResponse;
import com.chama.chamadao_server.models.dto.mpesa.MpesaStkCallback;
import com.chama.chamadao_server.models.dto.mpesa.MpesaStkPushResponse;
import com.chama.chamadao_server.services.MpesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PaymentControllerTest {

    @Mock
    private MpesaService mpesaService;

    @InjectMocks
    private PaymentController paymentController;

    private final String validWalletAddress = "0x1234567890123456789012345678901234567890";
    private final String validPhoneNumber = "+254712345678";
    private final BigDecimal validAmount = new BigDecimal("1000.00");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitiateDeposit_Success() {
        // Setup
        MpesaStkPushResponse mockResponse = new MpesaStkPushResponse();
        mockResponse.setMerchantRequestID("123456");
        mockResponse.setCheckoutRequestID("789012");
        mockResponse.setResponseCode("0");
        mockResponse.setResponseDescription("Success");
        mockResponse.setCustomerMessage("Please enter your PIN");

        when(mpesaService.initiateDeposit(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(mockResponse);

        // Execute
        ResponseEntity<MpesaStkPushResponse> response = paymentController.initiateDeposit(
                validWalletAddress, validPhoneNumber, validAmount);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("123456", response.getBody().getMerchantRequestID());
        assertEquals("789012", response.getBody().getCheckoutRequestID());
        
        System.out.println("[DEBUG_LOG] Successfully initiated deposit");
    }

    @Test
    public void testInitiateWithdrawal_Success() {
        // Setup
        MpesaB2CResponse mockResponse = new MpesaB2CResponse();
        mockResponse.setConversationID("AG_123456");
        mockResponse.setOriginatorConversationID("789012");
        mockResponse.setResponseCode("0");
        mockResponse.setResponseDescription("Success");

        when(mpesaService.initiateWithdrawal(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(mockResponse);

        // Execute
        ResponseEntity<MpesaB2CResponse> response = paymentController.initiateWithdrawal(
                validWalletAddress, validPhoneNumber, validAmount);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AG_123456", response.getBody().getConversationID());
        assertEquals("789012", response.getBody().getOriginatorConversationID());
        
        System.out.println("[DEBUG_LOG] Successfully initiated withdrawal");
    }

    @Test
    public void testStkCallback_Success() {
        // Setup
        MpesaStkCallback.Body body = new MpesaStkCallback.Body();
        MpesaStkCallback.StkCallback stkCallback = new MpesaStkCallback.StkCallback();
        stkCallback.setMerchantRequestID("123456");
        stkCallback.setCheckoutRequestID("789012");
        stkCallback.setResultCode(0);
        stkCallback.setResultDesc("Success");
        body.setStkCallback(stkCallback);

        MpesaStkCallback mockCallback = new MpesaStkCallback();
        mockCallback.setBody(body);

        when(mpesaService.processDepositCallback(any(MpesaStkCallback.class)))
                .thenReturn(true);

        // Execute
        ResponseEntity<String> response = paymentController.stkCallback(mockCallback);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Callback processed successfully", response.getBody());
        
        System.out.println("[DEBUG_LOG] Successfully processed STK callback");
    }

    @Test
    public void testB2CCallback_Success() {
        // Setup
        MpesaB2CCallback.Result result = new MpesaB2CCallback.Result();
        result.setResultCode(0);
        result.setResultDesc("Success");
        result.setConversationID("AG_123456");
        result.setOriginatorConversationID("789012");

        MpesaB2CCallback mockCallback = new MpesaB2CCallback();
        mockCallback.setResult(result);

        when(mpesaService.processWithdrawalCallback(any(MpesaB2CCallback.class)))
                .thenReturn(true);

        // Execute
        ResponseEntity<String> response = paymentController.b2cCallback(mockCallback);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Callback processed successfully", response.getBody());
        
        System.out.println("[DEBUG_LOG] Successfully processed B2C callback");
    }
}