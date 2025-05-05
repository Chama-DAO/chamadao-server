package com.chama.chamadao_server.models.enums;

/**
 * Enum representing the different states a transaction can be in
 */
public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}