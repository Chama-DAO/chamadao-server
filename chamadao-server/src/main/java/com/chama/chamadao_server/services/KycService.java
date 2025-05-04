package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.KycDocument;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.enums.DocumentType;
import com.chama.chamadao_server.models.enums.KycStatus;
import com.chama.chamadao_server.repository.KycDocumentRepository;
import com.chama.chamadao_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycDocumentRepository kycDocumentRepository;
    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "/uploads/kyc/";

    public KycDocument uploadKycDocument(String walletAddress, MultipartFile document, DocumentType documentType) throws IOException {

        //check it the user exists
        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //create a dir if it does not exist
        Path uploadPath = Paths.get(UPLOAD_DIR + walletAddress);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        //generate a unique file name  and save the docs
        String fileName = UUID.randomUUID() + "_" + document.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(document.getInputStream(), filePath);

        //we now create the kyc record
        KycDocument kycDocument = KycDocument.builder()
                .userWalletAddress(walletAddress)
                .documentType(documentType)
                .documentPath(filePath.toString())
                .documentHash("") // TODO: generate hash
                .verified(false)
                .uploadedAt(LocalDate.now())
                .build();

        return kycDocumentRepository.save(kycDocument);
    }

    public List<KycDocument> getKycDocuments(String walletAddress) {
        return kycDocumentRepository.findByUserWalletAddress(walletAddress);
    }

    public String verifyKycDocuments(String walletAddress) {
        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setKycStatus(KycStatus.VERIFIED);
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);

        List<KycDocument> kycDocuments = kycDocumentRepository.findByUserWalletAddress(walletAddress);
        kycDocuments.forEach(doc -> {
            doc.setVerified(true);
            doc.setVerifiedAt(LocalDate.now());
        });
        kycDocumentRepository.saveAll(kycDocuments);
        return "KYC documents verified successfully";
    }

    public KycStatus getKycStatus(String walletAddress) {
        User user = userRepository.findById(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getKycStatus();
    }

}
