package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.enums.KycStatus;
import com.chama.chamadao_server.models.enums.UserRole;
import com.chama.chamadao_server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;

    /**
     * Get a user by wallet address, initializing a basic profile if one doesn't exist
     * This method supports the account abstraction model where user accounts are created
     * by the mobile app, and the server only manages profiles associated with wallet addresses.
     * 
     * @param walletAddress The wallet address to look up
     * @return The user, either existing or newly initialized
     * @throws IllegalArgumentException if the wallet address format is invalid
     */
    public User getUserByWalletAddress(String walletAddress) {
        log.info("Getting user profile for wallet address: {}", walletAddress);

        if (!walletService.verifyWalletAddress(walletAddress)) {
            log.warn("Invalid wallet address format: {}", walletAddress);
            throw new IllegalArgumentException("Invalid wallet address format");
        }

        return userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    /**
     * Initialize a basic user profile for a new wallet address
     * This method is called automatically when a wallet address is encountered for the first time,
     * supporting the account abstraction model where accounts are created by the mobile app.
     * 
     * @param walletAddress The wallet address to initialize a profile for
     * @return The newly created user profile
     */
    private User initializeUserProfile(String walletAddress) {
        log.info("Initializing new user profile for wallet address: {}", walletAddress);

        User newUser = new User();
        newUser.setWalletAddress(walletAddress);
        newUser.setKycStatus(KycStatus.PENDING);
        newUser.setReputationScore(0.0);
        // createdAt and updatedAt will be set automatically by JPA auditing
        newUser.setRoles(new HashSet<>());
        newUser.addRole(UserRole.CHAMA_MEMBER);

        log.info("Saving new user profile for wallet address: {}", walletAddress);
        return userRepository.save(newUser);
    }

    /**
     * Update an existing user profile with new information
     * This method updates the profile information for a wallet address that already exists in the system.
     * 
     * @param walletAddress The wallet address of the user to update
     * @param updatedUser The updated user information
     * @return The updated user profile
     * @throws IllegalArgumentException if the wallet address format is invalid
     * @throws RuntimeException if the user doesn't exist
     */
    public User updateUserProfile(String walletAddress, User updatedUser) {
        log.info("Updating user profile for wallet address: {}", walletAddress);

        User existingUser = getUserByWalletAddress(walletAddress);

        // Update only non-null fields
        if (updatedUser.getFullName() != null) {
            log.debug("Updating full name for wallet address: {}", walletAddress);
            existingUser.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getMobileNumber() != null) {
            log.debug("Updating mobile number for wallet address: {}", walletAddress);
            existingUser.setMobileNumber(updatedUser.getMobileNumber());
        }
        if (updatedUser.getEmail() != null) {
            log.debug("Updating email for wallet address: {}", walletAddress);
            existingUser.setEmail(updatedUser.getEmail());
        }

        // updatedAt will be set automatically by JPA auditing

        log.info("Saving updated user profile for wallet address: {}", walletAddress);
        return userRepository.save(existingUser);
    }

    /**
     * Create a new user profile (used for admin purposes or testing)
     * Note: In normal operation, profiles are initialized automatically when accessed
     * via getUserByWalletAddress. This method is primarily for administrative use.
     * 
     * @param user The user to create
     * @return The created user profile
     * @throws IllegalArgumentException if the wallet address format is invalid
     * @throws RuntimeException if the user already exists
     */
    public User createUserProfile(User user) {
        log.info("Creating user profile for wallet address: {}", user.getWalletAddress());

        if (!walletService.verifyWalletAddress(user.getWalletAddress())) {
            log.warn("Invalid wallet address format: {}", user.getWalletAddress());
            throw new IllegalArgumentException("Invalid wallet address format");
        }

        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByWalletAddress(user.getWalletAddress());
        if (existingUser.isPresent()) {
            log.warn("User already exists for wallet address: {}", user.getWalletAddress());
            throw new RuntimeException("User already exists");
        }

        // Ensure required fields are set
        if (user.getKycStatus() == null) {
            log.debug("Setting default KYC status for wallet address: {}", user.getWalletAddress());
            user.setKycStatus(KycStatus.PENDING);
        }
        if (user.getReputationScore() == null) {
            log.debug("Setting default reputation score for wallet address: {}", user.getWalletAddress());
            user.setReputationScore(0.0);
        }
        // createdAt and updatedAt will be set automatically by JPA auditing
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.debug("Setting default role for wallet address: {}", user.getWalletAddress());
            user.setRoles(new HashSet<>());
            user.addRole(UserRole.CHAMA_MEMBER);
        }

        log.info("Saving new user profile for wallet address: {}", user.getWalletAddress());
        return userRepository.save(user);
    }
}
