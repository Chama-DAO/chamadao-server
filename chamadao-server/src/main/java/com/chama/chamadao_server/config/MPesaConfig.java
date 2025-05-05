package com.chama.chamadao_server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for M-Pesa API integration
 */
@Configuration
@ConfigurationProperties(prefix = "mpesa")
@Data
public class MPesaConfig {

    /**
     * M-Pesa API consumer key
     */
    private String consumerKey;

    /**
     * M-Pesa API consumer secret
     */
    private String consumerSecret;

    /**
     * M-Pesa API passkey
     */
    private String passkey;

    /**
     * M-Pesa API business short code
     */
    private String businessShortCode;

    /**
     * M-Pesa API transaction type
     */
    private String transactionType;

    /**
     * M-Pesa API callback URL
     */
    private String callbackUrl;

    /**
     * M-Pesa API timeout URL
     */
    private String timeoutUrl;

    /**
     * M-Pesa API access token URL
     */
    private String accessTokenUrl;

    /**
     * M-Pesa API STK push URL
     */
    private String stkPushUrl;

    /**
     * M-Pesa API query URL
     */
    private String queryUrl;

    /**
     * M-Pesa API B2C URL
     */
    private String b2cUrl;

    /**
     * M-Pesa API account reference
     */
    private String accountReference;

    /**
     * M-Pesa API transaction description
     */
    private String transactionDescription;
}