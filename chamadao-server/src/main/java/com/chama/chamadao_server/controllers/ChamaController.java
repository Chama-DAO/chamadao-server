package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.dto.ChamaDto;
import com.chama.chamadao_server.services.ChamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chamas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chama Operations", description = "APIs for managing Chamas")
public class ChamaController {

    private final ChamaService chamaService;

    /**
     * Get all Chamas
     * @return List of all Chamas
     */
    @Operation(summary = "Get all Chamas", description = "Returns a list of all Chamas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of Chamas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChamaDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<ChamaDto>> getAllChamas() {
        log.info("Request to get all Chamas");
        return ResponseEntity.ok(chamaService.getAllChamas());
    }

    /**
     * Get a Chama by wallet address
     * @param walletAddress The wallet address of the Chama
     * @return The Chama with the specified wallet address
     */
    @Operation(summary = "Get a Chama by wallet address", description = "Returns a Chama as per the wallet address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Chama",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChamaDto.class))),
            @ApiResponse(responseCode = "404", description = "Chama not found", content = @Content)
    })
    @GetMapping("/{walletAddress}")
    public ResponseEntity<ChamaDto> getChama(
            @Parameter(description = "Wallet address of the Chama", required = true)
            @PathVariable String walletAddress) {
        log.info("Request to get Chama with wallet address: {}", walletAddress);
        return ResponseEntity.ok(chamaService.findChamaByWalletAddress(walletAddress));
    }

    /**
     * Create a new Chama
     * @param chamaDto The Chama details
     * @return The created Chama
     */
    @Operation(summary = "Create a new Chama", description = "Creates a new Chama with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chama created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChamaDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Chama already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ChamaDto> createChama(
            @Parameter(description = "Chama details", required = true)
            @Valid @RequestBody ChamaDto chamaDto
            // @Parameter(description = "Wallet address of the creator", required = true)
            // @RequestParam String creatorWalletAddress
            ) {
        log.info("Request to create Chama with wallet address: {}", 
                chamaDto.getWalletAddress());
        ChamaDto createdChama = chamaService.createChama(chamaDto); // Passing null as creator wallet address
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChama);
    }

    /**
     * Add a member to a Chama
     * @param chamaWalletAddress The wallet address of the Chama
     * @param userWalletAddress The wallet address of the user to add
     * @return The updated Chama
     */
    @Operation(summary = "Add a member to a Chama", description = "Adds a user as a member to a Chama")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChamaDto.class))),
            @ApiResponse(responseCode = "404", description = "Chama or User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "User is already a member of this or another Chama", content = @Content)
    })
    @PostMapping("/{chamaWalletAddress}/members/{userWalletAddress}")
    public ResponseEntity<Chama> addMemberToChama(
            @Parameter(description = "Wallet address of the Chama", required = true)
            @PathVariable String chamaWalletAddress,
            @Parameter(description = "Wallet address of the user to add", required = true)
            @PathVariable String userWalletAddress) {
        log.info("Request to add member {} to Chama {}", userWalletAddress, chamaWalletAddress);
        Chama updatedChama = chamaService.addMemberToChama(chamaWalletAddress, userWalletAddress);
        return ResponseEntity.ok(updatedChama);
    }

    /**
     * Remove a member from a Chama
     * @param chamaWalletAddress The wallet address of the Chama
     * @param userWalletAddress The wallet address of the user to remove
     * @return The updated Chama
     */
    @Operation(summary = "Remove a member from a Chama", description = "Removes a user from a Chama")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChamaDto.class))),
            @ApiResponse(responseCode = "404", description = "Chama or User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "User is not a member of this Chama or is the creator", content = @Content)
    })
    @DeleteMapping("/{chamaWalletAddress}/members/{userWalletAddress}")
    public ResponseEntity<ChamaDto> removeMemberFromChama(
            @Parameter(description = "Wallet address of the Chama", required = true)
            @PathVariable String chamaWalletAddress,
            @Parameter(description = "Wallet address of the user to remove", required = true)
            @PathVariable String userWalletAddress) {
        log.info("Request to remove member {} from Chama {}", userWalletAddress, chamaWalletAddress);
        ChamaDto updatedChama = chamaService.removeMemberFromChama(chamaWalletAddress, userWalletAddress);
        return ResponseEntity.ok(updatedChama);
    }

    @Operation( summary = "upload chama image", description = "end point to upload the chama profile")
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(chamaService.uploadChamaImage(file));
    }

    /**
     * Upload a profile image for a Chama
     *
     * @param file The image file to upload
     * @param chamaWalletAddress The wallet address of the Chama
     * @return The URL/path of the uploaded image
     */
    @Operation(summary = "Upload Chama profile image",
            description = "Uploads and associates a profile image with a specific Chama")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or file format"),
            @ApiResponse(responseCode = "404", description = "Chama not found"),
            @ApiResponse(responseCode = "500", description = "Failed to process image")
    })
    @PostMapping("/{chamaWalletAddress}/image")
    public ResponseEntity<String> uploadChamaImage(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Wallet address of the Chama", required = true)
            @PathVariable String chamaWalletAddress) {
        log.info("Request to upload image for Chama {}", chamaWalletAddress);
        String imageUrl = chamaService.uploadChamaImage(file, chamaWalletAddress);
        return ResponseEntity.ok(imageUrl);
    }
}
