package com.chama.chamadao_server.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;

/**
 * Service for wallet-related operations
 * Handles wallet address validation, signature verification, and nonce generation
 */
@Service
@Slf4j
public class WalletService {

    /**
     * Verify that a wallet address is in the correct format
     * @param walletAddress The wallet address to verify
     * @return True if the wallet address is valid, false otherwise
     */
    public boolean verifyWalletAddress(String walletAddress){
        log.debug("Verifying wallet address format: {}", walletAddress);
        return walletAddress != null &&
                walletAddress.startsWith("0x") &&
                walletAddress.length() == 42 &&
                walletAddress.substring(2).matches("[a-fA-F0-9]+");
    }

    /**
     * Verify a wallet signature
     * This is a placeholder implementation that should be replaced with actual signature verification
     * using a library like web3j or ethers.js
     * 
     * @param walletAddress The wallet address that supposedly signed the message
     * @param message The message that was signed
     * @param signature The signature to verify
     * @return True if the signature is valid, false otherwise
     */
    public boolean verifyWalletSignature(String walletAddress, String message, String signature) {
        log.info("Verifying signature for wallet address: {}", walletAddress);

        // Verify that the wallet address is valid
        if (!verifyWalletAddress(walletAddress)) {
            log.warn("Invalid wallet address format: {}", walletAddress);
            return false;
        }

        // TODO: Implement actual signature verification using web3j or ethers.js
        // This would involve:
        // 1. Recovering the address from the signature and message
        // 2. Comparing the recovered address with the provided wallet address

        log.warn("Using placeholder signature verification for wallet address: {}", walletAddress);

        // For now, we'll just return true if the wallet address is valid
        // This should be replaced with actual signature verification in production
        return true;
    }

    /**
     * Generate a secure random nonce for use in authentication challenges
     * This can be used to prevent replay attacks when verifying wallet signatures
     * 
     * @return A secure random nonce as a hexadecimal string
     * @throws RuntimeException if there's an error generating the nonce
     */
    public String generateSecureNonce() {
        log.debug("Generating secure nonce");
        byte[] nonceBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(nonceBytes);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(nonceBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            log.debug("Generated secure nonce: {}", hexString);
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating secure nonce", e);
            throw new RuntimeException("Failed to generate secure nonce", e);
        }
    }

}
