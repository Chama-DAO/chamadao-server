package com.chama.chamadao_server.tests;

import com.chama.chamadao_server.models.dto.mpesa.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String validWalletAddress = "0x1234567890123456789012345678901234567890";
    private final String validPhoneNumber = "+254712345678";
    private final BigDecimal depositAmount = new BigDecimal("1000");
    private final BigDecimal withdrawalAmount = new BigDecimal("500");

    @Test
    public void testInitiateDeposit_Success() {
        // Execute
        ResponseEntity<MpesaStkPushResponse> response = restTemplate.getForEntity(
                "/api/payments/deposit?walletAddress=" + validWalletAddress + 
                "&phoneNumber=" + validPhoneNumber + 
                "&amount=" + depositAmount,
                MpesaStkPushResponse.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMerchantRequestID());
        assertNotNull(response.getBody().getCheckoutRequestID());
        
        System.out.println("[DEBUG_LOG] Initiate deposit response: " + response.getBody());
    }

    @Test
    public void testInitiateWithdrawal_Success() {
        // Execute
        ResponseEntity<MpesaB2CResponse> response = restTemplate.getForEntity(
                "/api/payments/withdraw?walletAddress=" + validWalletAddress + 
                "&phoneNumber=" + validPhoneNumber + 
                "&amount=" + withdrawalAmount,
                MpesaB2CResponse.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getConversationID());
        assertNotNull(response.getBody().getOriginatorConversationID());
        
        System.out.println("[DEBUG_LOG] Initiate withdrawal response: " + response.getBody());
    }

    @Test
    public void testStkCallback_Success() {
        // Setup
        MpesaStkCallback callback = createStkCallback();
        
        // Execute
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/payments/mpesa/stk-callback",
                callback,
                String.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Callback processed successfully", response.getBody());
        
        System.out.println("[DEBUG_LOG] STK callback response: " + response.getBody());
    }

    @Test
    public void testB2CCallback_Success() {
        // Setup
        MpesaB2CCallback callback = createB2CCallback();
        
        // Execute
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/payments/mpesa/b2c-callback",
                callback,
                String.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Callback processed successfully", response.getBody());
        
        System.out.println("[DEBUG_LOG] B2C callback response: " + response.getBody());
    }

    private MpesaStkCallback createStkCallback() {
        MpesaStkCallback callback = new MpesaStkCallback();
        
        MpesaStkCallback.Body body = new MpesaStkCallback.Body();
        MpesaStkCallback.StkCallback stkCallback = new MpesaStkCallback.StkCallback();
        stkCallback.setMerchantRequestID("12345-67890-1");
        stkCallback.setCheckoutRequestID("ws_CO_123456789012345678");
        stkCallback.setResultCode(0);
        stkCallback.setResultDesc("The service request is processed successfully.");
        
        MpesaStkCallback.CallbackMetadata metadata = new MpesaStkCallback.CallbackMetadata();
        List<MpesaStkCallback.Item> items = new ArrayList<>();
        
        MpesaStkCallback.Item amountItem = new MpesaStkCallback.Item();
        amountItem.setName("Amount");
        amountItem.setValue(depositAmount);
        items.add(amountItem);
        
        MpesaStkCallback.Item receiptItem = new MpesaStkCallback.Item();
        receiptItem.setName("MpesaReceiptNumber");
        receiptItem.setValue("PBL123456");
        items.add(receiptItem);
        
        MpesaStkCallback.Item dateItem = new MpesaStkCallback.Item();
        dateItem.setName("TransactionDate");
        dateItem.setValue(20230616112233L);
        items.add(dateItem);
        
        MpesaStkCallback.Item phoneItem = new MpesaStkCallback.Item();
        phoneItem.setName("PhoneNumber");
        phoneItem.setValue(254712345678L);
        items.add(phoneItem);
        
        metadata.setItems(items);
        stkCallback.setCallbackMetadata(metadata);
        body.setStkCallback(stkCallback);
        callback.setBody(body);
        
        return callback;
    }

    private MpesaB2CCallback createB2CCallback() {
        MpesaB2CCallback callback = new MpesaB2CCallback();
        
        MpesaB2CCallback.Result result = new MpesaB2CCallback.Result();
        result.setResultType(0);
        result.setResultCode(0);
        result.setResultDesc("The service request is processed successfully.");
        result.setOriginatorConversationID("12345-67890-2");
        result.setConversationID("AG_20230616_12345678901234567");
        result.setTransactionID("PBL123457");
        
        MpesaB2CCallback.ResultParameters resultParameters = new MpesaB2CCallback.ResultParameters();
        List<MpesaB2CCallback.ResultParameter> parameters = new ArrayList<>();
        
        MpesaB2CCallback.ResultParameter amountParam = new MpesaB2CCallback.ResultParameter();
        amountParam.setKey("TransactionAmount");
        amountParam.setValue(withdrawalAmount);
        parameters.add(amountParam);
        
        MpesaB2CCallback.ResultParameter receiptParam = new MpesaB2CCallback.ResultParameter();
        receiptParam.setKey("TransactionReceipt");
        receiptParam.setValue("PBL123457");
        parameters.add(receiptParam);
        
        MpesaB2CCallback.ResultParameter dateParam = new MpesaB2CCallback.ResultParameter();
        dateParam.setKey("TransactionCompletionDate");
        dateParam.setValue("20230616112233");
        parameters.add(dateParam);
        
        MpesaB2CCallback.ResultParameter phoneParam = new MpesaB2CCallback.ResultParameter();
        phoneParam.setKey("RecipientPhoneNumber");
        phoneParam.setValue("254712345678");
        parameters.add(phoneParam);
        
        resultParameters.setResultParameter(parameters);
        result.setResultParameters(resultParameters);
        callback.setResult(result);
        
        return callback;
    }
}