package com.chama.chamadao_server.models.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Callback model for M-Pesa B2C API
 * This is the callback received after a payment to a customer is completed or fails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpesaB2CCallback {

    @JsonProperty("Result")
    private Result result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        @JsonProperty("ResultType")
        private int resultType;

        @JsonProperty("ResultCode")
        private int resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("OriginatorConversationID")
        private String originatorConversationID;

        @JsonProperty("ConversationID")
        private String conversationID;

        @JsonProperty("TransactionID")
        private String transactionID;

        @JsonProperty("ResultParameters")
        private ResultParameters resultParameters;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultParameters {
        @JsonProperty("ResultParameter")
        private List<ResultParameter> resultParameter;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultParameter {
        @JsonProperty("Key")
        private String key;

        @JsonProperty("Value")
        private Object value;
    }

    /**
     * Get the transaction amount from the callback
     * @return The amount as a string, or null if not found
     */
    public String getTransactionAmount() {
        return getParameterValue("TransactionAmount");
    }

    /**
     * Get the transaction receipt from the callback
     * @return The receipt as a string, or null if not found
     */
    public String getTransactionReceipt() {
        return getParameterValue("TransactionReceipt");
    }

    /**
     * Get the transaction completion date from the callback
     * @return The completion date as a string, or null if not found
     */
    public String getTransactionCompletionDate() {
        return getParameterValue("TransactionCompletionDate");
    }

    /**
     * Get the recipient phone number from the callback
     * @return The phone number as a string, or null if not found
     */
    public String getRecipientPhoneNumber() {
        return getParameterValue("RecipientPhoneNumber");
    }

    /**
     * Helper method to get a parameter value by key
     * @param key The key of the parameter
     * @return The value as a string, or null if not found
     */
    private String getParameterValue(String key) {
        if (result != null && result.getResultParameters() != null && 
            result.getResultParameters().getResultParameter() != null) {
            
            return result.getResultParameters().getResultParameter().stream()
                    .filter(param -> key.equals(param.getKey()))
                    .findFirst()
                    .map(param -> param.getValue().toString())
                    .orElse(null);
        }
        return null;
    }
}