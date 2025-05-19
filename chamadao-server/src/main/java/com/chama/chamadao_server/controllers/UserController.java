package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.mappers.UserMapper;
import com.chama.chamadao_server.models.dto.UserDto;
import com.chama.chamadao_server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/**
 * Controller for managing user profiles
 * This controller handles operations related to user profiles, which are associated with wallet addresses.
 * Note that user accounts are created by the mobile app using account abstraction or wallet imports,
 * and the server only manages profiles after account creation.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Get a user profile by wallet address
     * If the profile doesn't exist, a basic profile will be initialized automatically.
     * This endpoint supports the account abstraction model where user accounts are created
     * by the mobile app, and the server only manages profiles associated with wallet addresses.
     * 
     * @param walletAddress The wallet address to look up
     * @return The user profile, or a 400 Bad Request if the wallet address format is invalid
     */
    @Operation(
        summary = "Get a user profile by wallet address",
        description = "Retrieves a user profile by wallet address. If the profile doesn't exist, a basic profile will be initialized automatically."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User profile retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid wallet address format",
            content = @Content
        )
    })
    @GetMapping("/{walletAddress}")
    public ResponseEntity<UserDto> getUserProfile(
            @Parameter(description = "Wallet address of the user", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress) {
        try {
            UserDto user = userService.getUserByWalletAddress(walletAddress);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing user profile
     * This endpoint updates the profile information for a wallet address that already exists in the system.
     * 
     * @param walletAddress The wallet address of the user to update
     * @param userDto The updated user information
     * @return The updated user profile, a 400 Bad Request if the wallet address format is invalid,
     *         or a 404 Not Found if the user doesn't exist
     */
    @Operation(
        summary = "Update an existing user profile",
        description = "Updates the profile information for a wallet address that already exists in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User profile updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid wallet address format or invalid request body",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content
        )
    })
    @PutMapping("/{walletAddress}")
    public ResponseEntity<UserDto> updateUserProfile(
            @Parameter(description = "Wallet address of the user to update", example = "0x1234567890123456789012345678901234567890")
            @PathVariable String walletAddress,
            @Parameter(description = "Updated user information")
            @Valid @RequestBody UserDto userDto) {
        try {

            return ResponseEntity.ok(userService.updateUserProfile(walletAddress, userDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new user profile (admin/testing purposes only)
     * In normal operation, profiles are initialized automatically when accessed via getUserProfile.
     * This endpoint is primarily for administrative use and testing.
     * 
     * @param userDto The user to create
     * @return The created user profile, a 400 Bad Request if the wallet address format is invalid,
     *         or if the user already exists
     */
    @Operation(
        summary = "Create a new user profile",
        description = "Creates a new user profile. In normal operation, profiles are initialized automatically when accessed via getUserProfile. This endpoint is primarily for administrative use and testing."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User profile created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid wallet address format, invalid request body, or user already exists",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<UserDto> createUserProfile(
            @Parameter(description = "User information to create")
            @Valid @RequestBody UserDto userDto) {
        try {
            UserDto createdUserDto = userService.createUserProfile(userDto);
            return ResponseEntity.ok(createdUserDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Add these methods to your UserController

@Operation(
    summary = "Upload User profile image",
    description = "Uploads and associates a profile image with a specific User"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input or file format"),
    @ApiResponse(responseCode = "404", description = "User not found"),
    @ApiResponse(responseCode = "500", description = "Failed to process image")
})
@PostMapping("/{walletAddress}/image")
public ResponseEntity<String> uploadUserProfileImage(
        @Parameter(description = "Image file to upload", required = true)
        @RequestParam("file") MultipartFile file,
        @Parameter(description = "Wallet address of the User", required = true, example = "0x1234567890123456789012345678901234567890")
        @PathVariable String walletAddress) {
    try {
        String imageFilename = userService.uploadUserProfileImage(file, walletAddress);
        return ResponseEntity.ok(imageFilename);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        log.error("Error uploading image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
    }
}

@Operation(
    summary = "Get User profile image",
    description = "Retrieves the profile image of a specific User"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
    @ApiResponse(responseCode = "404", description = "User or image not found"),
    @ApiResponse(responseCode = "500", description = "Server error")
})
@GetMapping("/{walletAddress}/image")
public ResponseEntity<Resource> getUserProfileImage(
        @Parameter(description = "Wallet address of the User", required = true, example = "0x1234567890123456789012345678901234567890")
        @PathVariable String walletAddress) {
    try {
        Resource resource = userService.getUserProfileImage(walletAddress);
        String contentType = userService.getUserProfileImageContentType(walletAddress);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        log.error("Error retrieving image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}
