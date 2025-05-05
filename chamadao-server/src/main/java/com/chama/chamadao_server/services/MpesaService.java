package com.chama.chamadao_server.services;

import com.chama.chamadao_server.config.MPesaConfig;
import com.chama.chamadao_server.models.Transaction;
import com.chama.chamadao_server.models.dto.mpesa.*;
import com.chama.chamadao_server.models.enums.TransactionStatus;
import com.chama.chamadao_server.models.enums.TransactionType;
import com.chama.chamadao_server.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Service for M-Pesa API integration
 * Handles communication with the M-Pesa API for deposits and withdrawals
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaService {

    private final MPesaConfig mpesaConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TransactionRepository transactionRepository;
    private final CurrencyConversionService currencyConversionService;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate an access token for M-Pesa API
     * @return The access token response
     * @throws IOException if there's an error during the HTTP request
     */
    public AccessTokenResponse generateAccessToken() throws IOException {
        String consumerKey = mpesaConfig.getConsumerKey();
        String consumerSecret = mpesaConfig.getConsumerSecret();
        String credentials = Credentials.basic(consumerKey, consumerSecret);

        // Prepare the request
        Request request = new Request.Builder()
                .url(mpesaConfig.getAccessTokenUrl())
                .get()
                .addHeader("Authorization", credentials)
                .build();
        log.debug("Request prepared: {}", request);

        // Execute the request
        try (Response response = okHttpClient.newCall(request).execute()) {
            log.debug("Response received: {}", response);
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                log.error("Failed to generate access token. Response: {} - {}", response.code(), responseBody);
                throw new IOException("Failed to generate access token");
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBody, AccessTokenResponse.class);
        } catch (IOException e) {
            log.error("Failed to generate access token.", e);
            throw e;
        }
    }

    /**
     * Initiate an STK push request for deposit
     * @param walletAddress The wallet address of the user
     * @param phoneNumber The phone number to send the STK push to
     * @param amount The amount in KES
     * @return The STK push response
     */
    public MpesaStkPushResponse initiateDeposit(String walletAddress, String phoneNumber, BigDecimal amount) {
        log.info("Initiating deposit for wallet address: {}, phone number: {}, amount: {}", 
                walletAddress, phoneNumber, amount);

        // Format phone number (remove leading 0 or +254 and add 254)
        String formattedPhone = formatPhoneNumber(phoneNumber);

        // Generate timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // Generate password
        String password = Base64.getEncoder().encodeToString(
                (mpesaConfig.getBusinessShortCode() + mpesaConfig.getPasskey() + timestamp).getBytes());

        // Create STK push request
        MpesaStkPushRequest request = MpesaStkPushRequest.builder()
                .businessShortCode(mpesaConfig.getBusinessShortCode())
                .password(password)
                .timestamp(timestamp)
                .transactionType(mpesaConfig.getTransactionType())
                .amount(amount.toString())
                .partyA(formattedPhone)
                .partyB(mpesaConfig.getBusinessShortCode())
                .phoneNumber(formattedPhone)
                .callBackURL(mpesaConfig.getCallbackUrl())
                .accountReference(mpesaConfig.getAccountReference())
                .transactionDesc(mpesaConfig.getTransactionDescription())
                .build();

        // Send request to M-Pesa API
        String accessToken;
        try {
            AccessTokenResponse tokenResponse = generateAccessToken();
            accessToken = tokenResponse.getAccessToken();
            if (accessToken == null || accessToken.isEmpty()) {
                log.error("Failed to generate access token for STK push");
                throw new RuntimeException("Failed to generate access token for STK push");
            }
        } catch (IOException e) {
            log.error("Failed to generate access token for STK push", e);
            throw new RuntimeException("Failed to generate access token for STK push", e);
        }

        String stkPushUrl = mpesaConfig.getStkPushUrl();
        log.debug("STK push URL: {}", stkPushUrl);
        log.debug("STK push request: {}", request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<MpesaStkPushRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.debug("Sending STK push request to M-Pesa API");
            ResponseEntity<MpesaStkPushResponse> response = restTemplate.exchange(
                    stkPushUrl, HttpMethod.POST, entity, MpesaStkPushResponse.class);

            log.debug("STK push response status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully initiated deposit: {}", response.getBody().getCheckoutRequestID());
                log.debug("STK push response: {}", response.getBody());

                // Create transaction record
                // For deposits, we'll set the USDT amount when the callback is received
                Transaction transaction = Transaction.builder()
                        .walletAddress(walletAddress)
                        .mobileNumber(phoneNumber)
                        .type(TransactionType.DEPOSIT)
                        .amountKES(amount)
                        .amountUSDT(BigDecimal.ZERO) // Will be updated when the callback is received
                        .status(TransactionStatus.PENDING)
                        .description("M-Pesa deposit initiated")
                        .createdAt(LocalDateTime.now())
                        .build();

                transactionRepository.save(transaction);

                return response.getBody();
            } else {
                log.error("Failed to initiate deposit: {}", response.getStatusCode());
                if (response.getBody() != null) {
                    log.error("Error response: {}", response.getBody());
                }
                throw new RuntimeException("Failed to initiate deposit: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception while initiating deposit", e);
            throw new RuntimeException("Failed to initiate deposit: " + e.getMessage(), e);
        }
    }

    /**
     * Process STK push callback
     * @param callback The callback from M-Pesa
     * @return True if the callback was processed successfully
     */
    public boolean processDepositCallback(MpesaStkCallback callback) {
        log.info("Processing deposit callback: {}", callback.getBody().getStkCallback().getCheckoutRequestID());

        // Check if the transaction was successful
        if (callback.getBody().getStkCallback().getResultCode() != 0) {
            log.warn("Deposit failed: {}", callback.getBody().getStkCallback().getResultDesc());
            return false;
        }

        // Get transaction details from callback
        String amount = callback.getAmount();
        String receiptNumber = callback.getReceiptNumber();
        String phoneNumber = callback.getPhoneNumber();

        // Find transaction by phone number and update it
        Transaction transaction = transactionRepository.findByMobileNumber(phoneNumber, null)
                .getContent().stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING && t.getType() == TransactionType.DEPOSIT)
                .findFirst()
                .orElse(null);

        if (transaction != null) {
            // Update transaction with receipt number and status
            transaction.setMpesaReceiptNumber(receiptNumber);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());

            // Convert KES to USDT using exchange rate service
            BigDecimal amountUSDT = currencyConversionService.convertKesToUsdt(transaction.getAmountKES());
            transaction.setAmountUSDT(amountUSDT);

            transactionRepository.save(transaction);

            log.info("Deposit completed: {}", receiptNumber);
            return true;
        } else {
            log.warn("Transaction not found for phone number: {}", phoneNumber);
            return false;
        }
    }

    /**
     * Initiate a B2C transaction for withdrawal
     * @param walletAddress The wallet address of the user
     * @param phoneNumber The phone number to send the money to
     * @param amount The amount in KES
     * @return The B2C response
     */
    public MpesaB2CResponse initiateWithdrawal(String walletAddress, String phoneNumber, BigDecimal amount) {
        log.info("Initiating withdrawal for wallet address: {}, phone number: {}, amount: {}", 
                walletAddress, phoneNumber, amount);

        // Format phone number
        String formattedPhone = formatPhoneNumber(phoneNumber);

        // Create B2C request
        MpesaB2CRequest request = MpesaB2CRequest.builder()
                .initiatorName("ChamaDAO")
                .securityCredential("") // TODO: Generate security credential
                .commandID("BusinessPayment")
                .amount(amount.toString())
                .partyA(mpesaConfig.getBusinessShortCode())
                .partyB(formattedPhone)
                .remarks("ChamaDAO Withdrawal")
                .queueTimeOutURL(mpesaConfig.getTimeoutUrl())
                .resultURL(mpesaConfig.getCallbackUrl())
                .occasion("Withdrawal")
                .build();

        // Send request to M-Pesa API
        String accessToken;
        try {
            AccessTokenResponse tokenResponse = generateAccessToken();
            accessToken = tokenResponse.getAccessToken();
            if (accessToken == null || accessToken.isEmpty()) {
                log.error("Failed to generate access token for B2C withdrawal");
                throw new RuntimeException("Failed to generate access token for B2C withdrawal");
            }
        } catch (IOException e) {
            log.error("Failed to generate access token for B2C withdrawal", e);
            throw new RuntimeException("Failed to generate access token for B2C withdrawal", e);
        }

        String b2cUrl = mpesaConfig.getB2cUrl();
        log.debug("B2C URL: {}", b2cUrl);
        log.debug("B2C request: {}", request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<MpesaB2CRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.debug("Sending B2C request to M-Pesa API");
            ResponseEntity<MpesaB2CResponse> response = restTemplate.exchange(
                    b2cUrl, HttpMethod.POST, entity, MpesaB2CResponse.class);

            log.debug("B2C response status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully initiated withdrawal: {}", response.getBody().getConversationID());
                log.debug("B2C response: {}", response.getBody());

                // Create transaction record
                // For withdrawals, we first calculate how much USDT is equivalent to the KES amount
                BigDecimal amountUSDT = currencyConversionService.convertKesToUsdt(amount);

                Transaction transaction = Transaction.builder()
                        .walletAddress(walletAddress)
                        .mobileNumber(phoneNumber)
                        .type(TransactionType.WITHDRAWAL)
                        .amountKES(amount)
                        .amountUSDT(amountUSDT)
                        .status(TransactionStatus.PENDING)
                        .description("M-Pesa withdrawal initiated")
                        .createdAt(LocalDateTime.now())
                        .build();

                transactionRepository.save(transaction);

                return response.getBody();
            } else {
                log.error("Failed to initiate withdrawal: {}", response.getStatusCode());
                if (response.getBody() != null) {
                    log.error("Error response: {}", response.getBody());
                }
                throw new RuntimeException("Failed to initiate withdrawal: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception while initiating withdrawal", e);
            throw new RuntimeException("Failed to initiate withdrawal: " + e.getMessage(), e);
        }
    }

    /**
     * Process B2C callback
     * @param callback The callback from M-Pesa
     * @return True if the callback was processed successfully
     */
    public boolean processWithdrawalCallback(MpesaB2CCallback callback) {
        log.info("Processing withdrawal callback: {}", callback.getResult().getConversationID());

        // Check if the transaction was successful
        if (callback.getResult().getResultCode() != 0) {
            log.warn("Withdrawal failed: {}", callback.getResult().getResultDesc());
            return false;
        }

        // Get transaction details from callback
        String amount = callback.getTransactionAmount();
        String receiptNumber = callback.getTransactionReceipt();
        String phoneNumber = callback.getRecipientPhoneNumber();

        // Find transaction by phone number and update it
        Transaction transaction = transactionRepository.findByMobileNumber(phoneNumber, null)
                .getContent().stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING && t.getType() == TransactionType.WITHDRAWAL)
                .findFirst()
                .orElse(null);

        if (transaction != null) {
            // Update transaction with receipt number and status
            transaction.setMpesaReceiptNumber(receiptNumber);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());

            // For withdrawals, we need to calculate how much USDT was converted to KES
            BigDecimal amountUSDT = currencyConversionService.convertKesToUsdt(transaction.getAmountKES());
            transaction.setAmountUSDT(amountUSDT);

            transactionRepository.save(transaction);

            log.info("Withdrawal completed: {}", receiptNumber);
            return true;
        } else {
            log.warn("Transaction not found for phone number: {}", phoneNumber);
            return false;
        }
    }

    /**
     * Format phone number for M-Pesa API
     * @param phoneNumber The phone number to format
     * @return The formatted phone number
     */
    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String digitsOnly = phoneNumber.replaceAll("\\D", "");

        // If the number starts with 0, replace it with 254
        if (digitsOnly.startsWith("0")) {
            digitsOnly = "254" + digitsOnly.substring(1);
        }

        // If the number starts with +254, remove the +
        if (digitsOnly.startsWith("254")) {
            return digitsOnly;
        }

        // Otherwise, add 254 prefix
        return "254" + digitsOnly;
    }

    /**
     * Inner class for access token response
     */
    private static class AccessTokenResponse {
        private String access_token;
        private String expires_in;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }

        public String getExpiresIn() {
            return expires_in;
        }

        public void setExpiresIn(String expires_in) {
            this.expires_in = expires_in;
        }

        @Override
        public String toString() {
            return "AccessTokenResponse{" +
                    "access_token='" + (access_token != null ? access_token.substring(0, Math.min(10, access_token.length())) + "..." : "null") + '\'' +
                    ", expires_in='" + expires_in + '\'' +
                    '}';
        }
    }
}
