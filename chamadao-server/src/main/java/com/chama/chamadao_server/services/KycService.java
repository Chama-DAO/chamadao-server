package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.KycDocument;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.enums.DocumentType;
import com.chama.chamadao_server.models.enums.KycStatus;
import com.chama.chamadao_server.repository.KycDocumentRepository;
import com.chama.chamadao_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycDocumentRepository kycDocumentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadBaseDir;

    /**
     * Generate a SHA-256 hash for a document file
     * @param file The document file
     * @return The hash as a hexadecimal string
     */
    private String generateDocumentHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("Error generating document hash", e);
            throw new RuntimeException("Failed to generate document hash", e);
        }
    }

    /**
     * Upload a KYC document for a user
     * @param walletAddress The wallet address of the user
     * @param document The document file
     * @param documentType The type of document
     * @return The created KYC document record
     * @throws IOException If there's an error handling the file
     */
    public KycDocument uploadKycDocument(String walletAddress, MultipartFile document, DocumentType documentType) throws IOException {
        log.info("Uploading KYC document for wallet address: {}, document type: {}", walletAddress, documentType);

        // Check if the user exists
        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Variables that need to be accessed outside the try block
        Path uploadDir = Paths.get(uploadBaseDir, "kyc", walletAddress);
        String fileName = UUID.randomUUID() + "_" + document.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);

        try {
            // Create a directory if it does not exist
            log.debug("Creating directory for KYC documents: {}", uploadDir);

            if(!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.debug("Created directory: {}", uploadDir);
            }

            // Save the document
            Files.copy(document.getInputStream(), filePath);
            log.debug("Saved file to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save KYC document: {}", e.getMessage(), e);
            throw new IOException("Failed to save KYC document: " + e.getMessage(), e);
        }

        // Generate a hash for the document
        String documentHash = generateDocumentHash(document);

        // Create the KYC document record
        KycDocument kycDocument = KycDocument.builder()
                .userWalletAddress(walletAddress)
                .documentType(documentType)
                .documentPath(filePath.toString())
                .documentHash(documentHash)
                .verified(false)
                .uploadedAt(LocalDate.now())
                .build();

        // Update user's KYC status if it's not already in progress
        if (user.getKycStatus() == KycStatus.PENDING) {
            user.setKycStatus(KycStatus.PENDING);
            // updatedAt will be set automatically by JPA auditing
            userRepository.save(user);
        }

        log.info("KYC document uploaded successfully for wallet address: {}", walletAddress);
        return kycDocumentRepository.save(kycDocument);
    }

    /**
     * Get all KYC documents for a user
     * @param walletAddress The wallet address of the user
     * @return A list of KYC documents
     */
    public List<KycDocument> getKycDocuments(String walletAddress) {
        log.info("Retrieving KYC documents for wallet address: {}", walletAddress);
        return kycDocumentRepository.findByUserWalletAddress(walletAddress);
    }

    /**
     * Verify KYC documents for a user
     * @param walletAddress The wallet address of the user
     * @return A success message
     */
    public String verifyKycDocuments(String walletAddress) {
        log.info("Verifying KYC documents for wallet address: {}", walletAddress);

        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setKycStatus(KycStatus.VERIFIED);
        // updatedAt will be set automatically by JPA auditing
        userRepository.save(user);

        List<KycDocument> kycDocuments = kycDocumentRepository.findByUserWalletAddress(walletAddress);
        kycDocuments.forEach(doc -> {
            doc.setVerified(true);
            doc.setVerifiedAt(LocalDate.now());
        });
        kycDocumentRepository.saveAll(kycDocuments);

        log.info("KYC documents verified successfully for wallet address: {}", walletAddress);
        return "KYC documents verified successfully";
    }

    /**
     * Reject KYC documents for a user
     * @param walletAddress The wallet address of the user
     * @return A success message
     */
    public String rejectKycDocuments(String walletAddress) {
        log.info("Rejecting KYC documents for wallet address: {}", walletAddress);

        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setKycStatus(KycStatus.REJECTED);
        // updatedAt will be set automatically by JPA auditing
        userRepository.save(user);

        log.info("KYC documents rejected for wallet address: {}", walletAddress);
        return "KYC documents rejected";
    }

    /**
     * Get the KYC status for a user
     * @param walletAddress The wallet address of the user
     * @return The KYC status
     */
    public KycStatus getKycStatus(String walletAddress) {
        log.info("Retrieving KYC status for wallet address: {}", walletAddress);
        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getKycStatus();
    }

}
