package com.chama.chamadao_server.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class WalletService {

    public boolean verifyWalletAddress(String walletAddress){
        return walletAddress != null &&
                walletAddress.startsWith("0x") &&
                walletAddress.length() == 42 &&
                walletAddress.substring(2).matches("[a-fA-F0-9]+");

    }

    public boolean verifyWalletSignature(String walletAddress, String message, String signature) {
        return verifyWalletAddress(walletAddress);
    }

    public String generateSecureNonce() {
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

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating secure nonce", e);
            throw new RuntimeException("Failed to generate secure nonce", e);
        }
    }

}
