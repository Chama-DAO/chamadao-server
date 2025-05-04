package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.KycDocument;
import com.chama.chamadao_server.models.enums.DocumentType;
import com.chama.chamadao_server.models.enums.KycStatus;
import com.chama.chamadao_server.services.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users/{walletAddress}/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/documents")
    public ResponseEntity<KycDocument> uploadKycDocument(
            @PathVariable String walletAddress,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType) throws IOException {

        KycDocument document = kycService.uploadKycDocument(walletAddress, file, documentType);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<KycDocument>> getKycDocuments(@PathVariable String walletAddress) {
        List<KycDocument> documents = kycService.getKycDocuments(walletAddress);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/status")
    public ResponseEntity<KycStatus> getKycStatus(@PathVariable String walletAddress) {
        KycStatus status = kycService.getKycStatus(walletAddress);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyKycDocuments(@PathVariable String walletAddress) {
        kycService.verifyKycDocuments(walletAddress);
        return ResponseEntity.ok().build();
    }
}