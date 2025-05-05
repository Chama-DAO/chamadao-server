package com.chama.chamadao_server.tests;

import com.chama.chamadao_server.models.KycDocument;
import com.chama.chamadao_server.models.enums.DocumentType;
import com.chama.chamadao_server.models.enums.KycStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KycControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String validWalletAddress = "0x1234567890123456789012345678901234567890";

    @Test
    public void testUploadKycDocument_Success() {
        // Setup
        Resource testFile = new ClassPathResource("test-id.jpg");
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", testFile);
        body.add("documentType", DocumentType.NATIONAL_ID);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        // Execute
        ResponseEntity<KycDocument> response = restTemplate.exchange(
                "/api/users/" + validWalletAddress + "/kyc/documents",
                HttpMethod.POST,
                requestEntity,
                KycDocument.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validWalletAddress, response.getBody().getUserWalletAddress());
        assertEquals(DocumentType.NATIONAL_ID, response.getBody().getDocumentType());
        assertFalse(response.getBody().isVerified());
        
        System.out.println("[DEBUG_LOG] Uploaded KYC document response: " + response.getBody());
    }

    @Test
    public void testGetKycDocuments_Success() {
        // Execute
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/users/" + validWalletAddress + "/kyc/documents",
                List.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        System.out.println("[DEBUG_LOG] KYC documents response: " + response.getBody());
    }

    @Test
    public void testGetKycStatus_Success() {
        // Execute
        ResponseEntity<KycStatus> response = restTemplate.getForEntity(
                "/api/users/" + validWalletAddress + "/kyc/status",
                KycStatus.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        System.out.println("[DEBUG_LOG] KYC status response: " + response.getBody());
    }

    @Test
    public void testVerifyKycDocuments_Success() {
        // Execute
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/users/" + validWalletAddress + "/kyc/verify",
                null,
                String.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("KYC documents verified successfully", response.getBody());
        
        System.out.println("[DEBUG_LOG] Verify KYC documents response: " + response.getBody());
    }

    @Test
    public void testRejectKycDocuments_Success() {
        // Execute
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/users/" + validWalletAddress + "/kyc/reject",
                null,
                String.class);
        
        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("KYC documents rejected", response.getBody());
        
        System.out.println("[DEBUG_LOG] Reject KYC documents response: " + response.getBody());
    }
}