package com.chama.chamadao_server.services;

import com.chama.chamadao_server.mappers.UserMapper;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.UserDto;
import com.chama.chamadao_server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final UserMapper userMapper;

    /**
     * Validates wallet address format
     * @param walletAddress The wallet address to validate
     * @throws IllegalArgumentException if the wallet address format is invalid
     */
    private void validateWalletAddress(String walletAddress) {
        if (!walletService.verifyWalletAddress(walletAddress)) {
            log.warn("Invalid wallet address format: {}", walletAddress);
            throw new IllegalArgumentException("Invalid wallet address format");
        }
    }

    /**
     * Get a user by wallet address
     *
     * @param walletAddress The wallet address to look up
     * @return The user DTO
     * @throws IllegalArgumentException if the wallet address format is invalid
     * @throws EntityNotFoundException if the user doesn't exist
     */
    public UserDto getUserByWalletAddress(String walletAddress) {
        log.info("Getting user profile for wallet address: {}", walletAddress);
        validateWalletAddress(walletAddress);

        log.debug("Searching for user with wallet address: {}", walletAddress);

        User user = userRepository.findUserWithChamasByWalletAddress(walletAddress)
                .orElseThrow(() -> {
                    log.error("User not found with wallet address: {}", walletAddress);
                    return new EntityNotFoundException("User not found");
                });
        return userMapper.toDto(user);
    }

    /**
     * Update an existing user profile with new information
     *
     * @param walletAddress The wallet address of the user to update
     * @param updatedUserDto The updated user information
     * @return The updated user profile as DTO
     * @throws IllegalArgumentException if the wallet address format is invalid
     * @throws EntityNotFoundException if the user doesn't exist
     */
    public UserDto updateUserProfile(String walletAddress, UserDto updatedUserDto) {
        log.info("Updating user profile for wallet address: {}", walletAddress);
        validateWalletAddress(walletAddress);

        User existingUser = userRepository.findById(walletAddress)
                .orElseThrow(() -> {
                    log.error("User not found with wallet address: {}", walletAddress);
                    return new EntityNotFoundException("User not found");
                });

        // Update only non-null fields
        if (updatedUserDto.getFullName() != null) {
            log.debug("Updating full name for wallet address: {}", walletAddress);
            existingUser.setFullName(updatedUserDto.getFullName());
        }
        if (updatedUserDto.getMobileNumber() != null) {
            log.debug("Updating mobile number for wallet address: {}", walletAddress);
            existingUser.setMobileNumber(updatedUserDto.getMobileNumber());
        }
        if (updatedUserDto.getEmail() != null) {
            log.debug("Updating email for wallet address: {}", walletAddress);
            existingUser.setEmail(updatedUserDto.getEmail());
        }
        if (updatedUserDto.getCountry() != null) {
            log.debug("Updating country for wallet address: {}", walletAddress);
            existingUser.setCountry(updatedUserDto.getCountry());
        }
        if (updatedUserDto.getIdNumber() != null) {
            log.debug("Updating ID number for wallet address: {}", walletAddress);
            existingUser.setIdNumber(updatedUserDto.getIdNumber());
        }

        log.info("Saving updated user profile for wallet address: {}", walletAddress);
        User savedUser = userRepository.save(existingUser);
        return userMapper.toDto(savedUser);
    }

    /**
     * Create a new user profile
     *
     * @param userDto The user to create
     * @return The created user profile as DTO
     * @throws IllegalArgumentException if the wallet address format is invalid
     * @throws RuntimeException if the user already exists
     */
    public UserDto createUserProfile(UserDto userDto) {
        String walletAddress = userDto.getWalletAddress();
        log.info("Creating user profile for wallet address: {}", walletAddress);
        validateWalletAddress(walletAddress);

        // Check if the user already exists
        if (userRepository.existsById(walletAddress)) {
            log.warn("User already exists for wallet address: {}", walletAddress);
            throw new RuntimeException("User already exists");
        }

        // Convert DTO to entity
        User newUser = userMapper.toEntity(userDto);

        // Set default reputation score if not provided
        if (newUser.getReputationScore() == null) {
            log.debug("Setting default reputation score for wallet address: {}", walletAddress);
            newUser.setReputationScore(0.0);
        }

        log.info("Saving new user profile for wallet address: {}", walletAddress);
        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }
}