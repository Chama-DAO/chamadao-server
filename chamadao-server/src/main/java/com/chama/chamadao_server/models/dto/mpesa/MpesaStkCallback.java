package com.chama.chamadao_server.models.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Callback model for M-Pesa STK Push API
 * This is the callback received after a payment is completed or fails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpesaStkCallback {

    @JsonProperty("Body")
    private Body body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        @JsonProperty("stkCallback")
        private StkCallback stkCallback;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StkCallback {
        @JsonProperty("MerchantRequestID")
        private String merchantRequestID;

        @JsonProperty("CheckoutRequestID")
        private String checkoutRequestID;

        @JsonProperty("ResultCode")
        private int resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("CallbackMetadata")
        private CallbackMetadata callbackMetadata;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallbackMetadata {
        @JsonProperty("Item")
        private List<Item> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private Object value;
    }

    /**
     * Get the amount from the callback
     * @return The amount as a string, or null if not found
     */
    public String getAmount() {
        return getItemValue("Amount");
    }

    /**
     * Get the receipt number from the callback
     * @return The receipt number as a string, or null if not found
     */
    public String getReceiptNumber() {
        return getItemValue("MpesaReceiptNumber");
    }

    /**
     * Get the transaction date from the callback
     * @return The transaction date as a string, or null if not found
     */
    public String getTransactionDate() {
        return getItemValue("TransactionDate");
    }

    /**
     * Get the phone number from the callback
     * @return The phone number as a string, or null if not found
     */
    public String getPhoneNumber() {
        return getItemValue("PhoneNumber");
    }

    /**
     * Helper method to get an item value by name
     * @param name The name of the item
     * @return The value as a string, or null if not found
     */
    private String getItemValue(String name) {
        if (body != null && body.getStkCallback() != null && 
            body.getStkCallback().getCallbackMetadata() != null && 
            body.getStkCallback().getCallbackMetadata().getItems() != null) {
            
            return body.getStkCallback().getCallbackMetadata().getItems().stream()
                    .filter(item -> name.equals(item.getName()))
                    .findFirst()
                    .map(item -> item.getValue().toString())
                    .orElse(null);
        }
        return null;
    }
}