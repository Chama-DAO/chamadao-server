package com.chama.chamadao_server.services;

import com.chama.chamadao_server.mappers.ChamaMapper;
import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.ChamaDto;
import com.chama.chamadao_server.models.enums.UserRole;
import com.chama.chamadao_server.repository.ChamaRepository;
import com.chama.chamadao_server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChamaService {
    private final ChamaRepository chamaRepository;
    private final UserRepository userRepository;
    private final ChamaMapper chamaMapper;

    /**
     * Find a Chama by its wallet address
     * @param walletAddress The wallet address of the Chama
     * @return The Chama DTO
     */
    public ChamaDto findChamaByWalletAddress(String walletAddress) {
        Chama chama = chamaRepository.findById(walletAddress)
                .orElseThrow(() -> new RuntimeException("Chama not found with wallet address: " + walletAddress));
        return chamaMapper.toDto(chama);
    }

    /**
     * Get all Chamas
     * @return List of Chama DTOs
     */
    public List<ChamaDto> getAllChamas() {
        return chamaRepository.findAll().stream()
                .map(chamaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new Chama
     * @param chamaDto The Chama details
     * @param creatorWalletAddress The wallet address of the creator (not used, kept for backward compatibility)
     * @return The created Chama DTO
     */
    @Transactional
    public ChamaDto createChama(ChamaDto chamaDto, String creatorWalletAddress) {
        log.info("Creating new Chama with wallet address: {}", 
                chamaDto.getWalletAddress());

        // Check if the chama already exists
        if (chamaRepository.findById(chamaDto.getWalletAddress()).isPresent()) {
            throw new RuntimeException("Chama already exists with wallet address: " + chamaDto.getWalletAddress());
        }

        // Get the creator user - commented out as per requirements
        // User creator = userRepository.findByWalletAddress(creatorWalletAddress)
        //         .orElseThrow(() -> new RuntimeException("Creator not found with wallet address: " + creatorWalletAddress));

        // Convert DTO to entity
        Chama chama = chamaMapper.toEntity(chamaDto);

        // Set the creator - commented out as per requirements
        // chama.setCreator(creator);

        // Add the creator as a member - commented out as per requirements
        // chama.addMember(creator);

        // Add CHAMA_ADMIN role to the creator if they don't have it - commented out as per requirements
        // if (!creator.hasRole(UserRole.CHAMA_ADMIN)) {
        //     creator.addRole(UserRole.CHAMA_ADMIN);
        // }

        // Save the Chama
        Chama savedChama = chamaRepository.save(chama);

        log.info("Chama created successfully: {}", savedChama.getWalletAddress());

        return chamaMapper.toDto(savedChama);
    }

    /**
     * Add a member to a Chama
     * @param chamaWalletAddress The wallet address of the Chama
     * @param userWalletAddress The wallet address of the user to add
     * @return The updated Chama DTO
     */
    @Transactional
    public Chama addMemberToChama(String chamaWalletAddress, String userWalletAddress) {
        log.info("Adding member {} to Chama {}", userWalletAddress, chamaWalletAddress);

        // Get the Chama
        Chama chama = chamaRepository.findById(chamaWalletAddress)
                .orElseThrow(() -> new RuntimeException("Chama not found with wallet address: " + chamaWalletAddress));

        // Get the User
        User user = userRepository.findByWalletAddress(userWalletAddress)
                .orElseThrow(() -> new RuntimeException("User not found with wallet address: " + userWalletAddress));

        // Check if user is already a member of this Chama
        if (user.getChama() != null && user.getChama().getWalletAddress().equals(chamaWalletAddress)) {
            throw new RuntimeException("User is already a member of this Chama");
        }

        // Check if user is already a member of another Chama
        if (user.getChama() != null) {
            throw new RuntimeException("User is already a member of another Chama. Please leave that Chama first.");
        }

        // Add CHAMA_MEMBER role to the user if they don't have it
//        if (!user.hasRole(UserRole.CHAMA_MEMBER)) {
//            user.addRole(UserRole.CHAMA_MEMBER);
//        }

        // Add user to chama using the helper method
        chama.addMember(user);

        // Save the updated chama
        Chama updatedChama = chamaRepository.save(chama);

        log.info("Member added successfully to Chama {}", chamaWalletAddress);

        return updatedChama;
    }

    /**
     * Remove a member from a Chama
     * @param chamaWalletAddress The wallet address of the Chama
     * @param userWalletAddress The wallet address of the user to remove
     * @return The updated Chama DTO
     */
    @Transactional
    public ChamaDto removeMemberFromChama(String chamaWalletAddress, String userWalletAddress) {
        log.info("Removing member {} from Chama {}", userWalletAddress, chamaWalletAddress);

        // Get the Chama
        Chama chama = chamaRepository.findById(chamaWalletAddress)
                .orElseThrow(() -> new RuntimeException("Chama not found with wallet address: " + chamaWalletAddress));

        // Get the User
        User user = userRepository.findByWalletAddress(userWalletAddress)
                .orElseThrow(() -> new RuntimeException("User not found with wallet address: " + userWalletAddress));

        // Check if user is a member of this Chama
        if (user.getChama() == null || !user.getChama().getWalletAddress().equals(chamaWalletAddress)) {
            throw new RuntimeException("User is not a member of this Chama");
        }

        // Check if user is the creator of the Chama - commented out as per requirements
        // if (chama.getCreator() != null && chama.getCreator().getWalletAddress().equals(userWalletAddress)) {
        //     throw new RuntimeException("Cannot remove the creator of the Chama");
        // }

        // Remove user from chama using the helper method
        chama.removeMember(user);

        // Save the updated chama
        Chama updatedChama = chamaRepository.save(chama);

        log.info("Member removed successfully from Chama {}", chamaWalletAddress);

        return chamaMapper.toDto(updatedChama);
    }
}
