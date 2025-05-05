package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.KycDocument;
import com.chama.chamadao_server.models.enums.DocumentType;
import com.chama.chamadao_server.models.enums.KycStatus;
import com.chama.chamadao_server.services.KycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class KycControllerTest {

    @Mock
    private KycService kycService;

    @InjectMocks
    private KycController kycController;

    private KycDocument testDocument;
    private final String validWalletAddress = "0x1234567890123456789012345678901234567890";
    private MultipartFile mockFile;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test document
        testDocument = KycDocument.builder()
                .id(1L)
                .userWalletAddress(validWalletAddress)
                .documentType(DocumentType.NATIONAL_ID)
                .documentPath("/uploads/kyc/" + validWalletAddress + "/test.jpg")
                .documentHash("abcdef1234567890")
                .verified(false)
                .uploadedAt(LocalDate.now())
                .build();

        // Setup mock file
        mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    public void testUploadKycDocument_Success() throws IOException {
        // Setup
        when(kycService.uploadKycDocument(anyString(), any(MultipartFile.class), any(DocumentType.class)))
                .thenReturn(testDocument);

        // Execute
        ResponseEntity<KycDocument> response = kycController.uploadKycDocument(
                validWalletAddress,
                mockFile,
                DocumentType.NATIONAL_ID
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validWalletAddress, response.getBody().getUserWalletAddress());
        assertEquals(DocumentType.NATIONAL_ID, response.getBody().getDocumentType());
        
        System.out.println("[DEBUG_LOG] Successfully uploaded KYC document for wallet address: " + validWalletAddress);
    }

    @Test
    public void testGetKycDocuments_Success() {
        // Setup
        List<KycDocument> documents = Arrays.asList(testDocument);
        when(kycService.getKycDocuments(anyString())).thenReturn(documents);

        // Execute
        ResponseEntity<List<KycDocument>> response = kycController.getKycDocuments(validWalletAddress);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(validWalletAddress, response.getBody().get(0).getUserWalletAddress());
        
        System.out.println("[DEBUG_LOG] Successfully retrieved KYC documents for wallet address: " + validWalletAddress);
    }

    @Test
    public void testGetKycStatus_Success() {
        // Setup
        when(kycService.getKycStatus(anyString())).thenReturn(KycStatus.PENDING);

        // Execute
        ResponseEntity<KycStatus> response = kycController.getKycStatus(validWalletAddress);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(KycStatus.PENDING, response.getBody());
        
        System.out.println("[DEBUG_LOG] Successfully retrieved KYC status for wallet address: " + validWalletAddress);
    }

    @Test
    public void testVerifyKycDocuments_Success() {
        // Setup
        when(kycService.verifyKycDocuments(anyString())).thenReturn("KYC documents verified successfully");

        // Execute
        ResponseEntity<String> response = kycController.verifyKycDocuments(validWalletAddress);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("KYC documents verified successfully", response.getBody());
        
        System.out.println("[DEBUG_LOG] Successfully verified KYC documents for wallet address: " + validWalletAddress);
    }

    @Test
    public void testRejectKycDocuments_Success() {
        // Setup
        when(kycService.rejectKycDocuments(anyString())).thenReturn("KYC documents rejected");

        // Execute
        ResponseEntity<String> response = kycController.rejectKycDocuments(validWalletAddress);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("KYC documents rejected", response.getBody());
        
        System.out.println("[DEBUG_LOG] Successfully rejected KYC documents for wallet address: " + validWalletAddress);
    }
}