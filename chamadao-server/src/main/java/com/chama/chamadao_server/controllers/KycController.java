package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.KycDocument;
import com.chama.chamadao_server.models.enums.DocumentType;
import com.chama.chamadao_server.models.enums.KycStatus;
import com.chama.chamadao_server.services.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing KYC (Know Your Customer) documents and verification
 */
@RestController
@RequestMapping("/api/users/{walletAddress}/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC Management", description = "APIs for managing KYC (Know Your Customer) documents and verification")
public class KycController {

    private final KycService kycService;

    /**
     * Upload a KYC document for a user
     * @param walletAddress The wallet address of the user
     * @param file The document file
     * @param documentType The type of document
     * @return The created KYC document
     * @throws IOException If there's an error handling the file
     */
    @Operation(
        summary = "Upload a KYC document",
        description = "Uploads a KYC document for a user. Supported document types include NATIONAL_ID, PASSPORT, DRIVERS_LICENSE, and PROOF_OF_ADDRESS."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Document uploaded successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = KycDocument.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid wallet address or document type",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error handling the file",
            content = @Content
        )
    })
    @PostMapping("/documents")
    public ResponseEntity<KycDocument> uploadKycDocument(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress,
            @Parameter(description = "Document file to upload (image or PDF)")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type of document", example = "NATIONAL_ID")
            @RequestParam("documentType") DocumentType documentType) throws IOException {

        KycDocument document = kycService.uploadKycDocument(walletAddress, file, documentType);
        return ResponseEntity.ok(document);
    }

    /**
     * Get all KYC documents for a user
     * @param walletAddress The wallet address of the user
     * @return A list of KYC documents
     */
    @Operation(
        summary = "Get all KYC documents for a user",
        description = "Retrieves all KYC documents that have been uploaded for a specific user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Documents retrieved successfully",
            content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = KycDocument.class)))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content
        )
    })
    @GetMapping("/documents")
    public ResponseEntity<List<KycDocument>> getKycDocuments(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress) {
        List<KycDocument> documents = kycService.getKycDocuments(walletAddress);
        return ResponseEntity.ok(documents);
    }

    /**
     * Get the KYC status for a user
     * @param walletAddress The wallet address of the user
     * @return The KYC status
     */
    @Operation(
        summary = "Get the KYC status for a user",
        description = "Retrieves the current KYC status for a user. Status can be PENDING, VERIFIED, or REJECTED."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "KYC status retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = KycStatus.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content
        )
    })
    @GetMapping("/status")
    public ResponseEntity<KycStatus> getKycStatus(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress) {
        KycStatus status = kycService.getKycStatus(walletAddress);
        return ResponseEntity.ok(status);
    }

    /**
     * Verify KYC documents for a user
     * @param walletAddress The wallet address of the user
     * @return A success response
     */
    @Operation(
        summary = "Verify KYC documents for a user",
        description = "Approves the KYC documents for a user, changing their KYC status to VERIFIED."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "KYC documents verified successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content
        )
    })
    @PostMapping("/verify")
    public ResponseEntity<String> verifyKycDocuments(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress) {
        String result = kycService.verifyKycDocuments(walletAddress);
        return ResponseEntity.ok(result);
    }

    /**
     * Reject KYC documents for a user
     * @param walletAddress The wallet address of the user
     * @return A success response
     */
    @Operation(
        summary = "Reject KYC documents for a user",
        description = "Rejects the KYC documents for a user, changing their KYC status to REJECTED."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "KYC documents rejected successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content
        )
    })
    @PostMapping("/reject")
    public ResponseEntity<String> rejectKycDocuments(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress) {
        String result = kycService.rejectKycDocuments(walletAddress);
        return ResponseEntity.ok(result);
    }
}
