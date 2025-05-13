package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.dto.mpesa.MpesaB2CCallback;
import com.chama.chamadao_server.models.dto.mpesa.MpesaB2CResponse;
import com.chama.chamadao_server.models.dto.mpesa.MpesaStkCallback;
import com.chama.chamadao_server.models.dto.mpesa.MpesaStkPushResponse;
import com.chama.chamadao_server.services.MpesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller for payment operations
 * Handles deposits and withdrawals using M-Pesa
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Operations", description = "APIs for handling deposits and withdrawals using M-Pesa")
public class PaymentController {

    private final MpesaService mpesaService;

    /**
     * Initiate a deposit using M-Pesa STK push
     * @param walletAddress The wallet address of the user
     * @param phoneNumber The phone number to send the STK push to
     * @param amount The amount in KES
     * @return The STK push response
     */
    @Operation(
        summary = "Initiate a deposit using M-Pesa",
        description = "Initiates a deposit using M-Pesa STK push. This will send a prompt to the user's phone to confirm the payment."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Deposit initiated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MpesaStkPushResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error communicating with M-Pesa API",
            content = @Content
        )
    })
    @PostMapping("/deposit")
    public ResponseEntity<MpesaStkPushResponse> initiateDeposit(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @RequestParam String walletAddress,
            @Parameter(description = "Phone number to send the STK push to", example = "+254712345678")
            @RequestParam String phoneNumber,
            @Parameter(description = "Amount in KES to deposit", example = "1000.00")
            @RequestParam BigDecimal amount) {

        log.info("Received deposit request for wallet address: {}, phone number: {}, amount: {}",
                walletAddress, phoneNumber, amount);

        MpesaStkPushResponse response = mpesaService.initiateDeposit(walletAddress,phoneNumber, amount);
        return ResponseEntity.ok(response);
    }

    /**
     * Initiate a withdrawal using M-Pesa B2C
     * @param walletAddress The wallet address of the user
     * @param phoneNumber The phone number to send the money to
     * @param amount The amount in KES
     * @return The B2C response
     */
    @Operation(
        summary = "Initiate a withdrawal using M-Pesa",
        description = "Initiates a withdrawal using M-Pesa B2C (Business to Customer). This will send money to the user's M-Pesa account."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Withdrawal initiated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MpesaB2CResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error communicating with M-Pesa API",
            content = @Content
        )
    })
    @PostMapping("/withdraw")
    public ResponseEntity<MpesaB2CResponse> initiateWithdrawal(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @RequestParam String walletAddress,
            @Parameter(description = "Phone number to send the money to", example = "+254712345678")
            @RequestParam String phoneNumber,
            @Parameter(description = "Amount in KES to withdraw", example = "500.00")
            @RequestParam BigDecimal amount) {

        log.info("Received withdrawal request for wallet address: {}, phone number: {}, amount: {}", 
                walletAddress, phoneNumber, amount);

        MpesaB2CResponse response = mpesaService.initiateWithdrawal(walletAddress, phoneNumber, amount);
        return ResponseEntity.ok(response);
    }

    /**
     * Callback endpoint for M-Pesa STK push (deposit)
     * @param callback The callback from M-Pesa
     * @return A success response
     */
    @Operation(
        summary = "M-Pesa STK push callback",
        description = "Callback endpoint for M-Pesa STK push (deposit). This endpoint is called by the M-Pesa API after a user completes or cancels a payment."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Callback processed",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        )
    })
    @PostMapping("/mpesa/stk-callback")
    public ResponseEntity<String> stkCallback(
            @Parameter(description = "Callback data from M-Pesa", required = true)
            @RequestBody MpesaStkCallback callback) {
        log.info("Received STK callback: {}", callback.getBody().getStkCallback().getCheckoutRequestID());

        boolean success = mpesaService.processDepositCallback(callback);

        if (success) {
            return ResponseEntity.ok("Callback processed successfully");
        } else {
            return ResponseEntity.ok("Callback processing failed");
        }
    }

    /**
     * Callback endpoint for M-Pesa B2C (withdrawal)
     * @param callback The callback from M-Pesa
     * @return A success response
     */
    @Operation(
        summary = "M-Pesa B2C callback",
        description = "Callback endpoint for M-Pesa B2C (withdrawal). This endpoint is called by the M-Pesa API after a withdrawal is completed or fails."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Callback processed",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        )
    })
    @PostMapping("/mpesa/b2c-callback")
    public ResponseEntity<String> b2cCallback(
            @Parameter(description = "Callback data from M-Pesa", required = true)
            @RequestBody MpesaB2CCallback callback) {
        log.info("Received B2C callback: {}", callback.getResult().getConversationID());

        boolean success = mpesaService.processWithdrawalCallback(callback);

        if (success) {
            return ResponseEntity.ok("Callback processed successfully");
        } else {
            return ResponseEntity.ok("Callback processing failed");
        }
    }
}
