package com.chama.chamadao_server.models.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model for M-Pesa B2C API
 * This is the response received after initiating a payment to a customer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpesaB2CResponse {

    @JsonProperty("ConversationID")
    private String conversationID;

    @JsonProperty("OriginatorConversationID")
    private String originatorConversationID;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseDescription")
    private String responseDescription;
}