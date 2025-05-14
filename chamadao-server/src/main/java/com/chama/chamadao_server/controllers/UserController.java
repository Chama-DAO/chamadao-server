package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.mappers.UserMapper;
import com.chama.chamadao_server.models.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
