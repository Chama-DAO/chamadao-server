package com.chama.chamadao_server.services;

import com.chama.chamadao_server.exceptions.ChamaException;
import com.chama.chamadao_server.mappers.ChamaMapper;
import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.ChamaDto;
import com.chama.chamadao_server.repository.ChamaRepository;
import com.chama.chamadao_server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChamaService {
    private final ChamaRepository chamaRepository;
    private final UserRepository userRepository;
    private final ChamaMapper chamaMapper;
    private final FileStorageService fileStorageService;


    /**
     * Find a Chama by its wallet address
     * @param walletAddress The wallet address of the Chama
     * @return The Chama DTO
     */
    public ChamaDto findChamaByWalletAddress(String walletAddress) {
        Chama chama = chamaRepository.findById(walletAddress)
                .orElseThrow(() -> new RuntimeException("Chama not found with wallet address: " + walletAddress));
        log.info("Chama found: {}", chama.getChamaAddress());
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
     * @return The created Chama DTO
     */
    @Transactional
    public ChamaDto createChama(ChamaDto chamaDto, String creatorWalletAddress) {
        // Find the creator by wallet address
        User creator = userRepository.findByWalletAddress(creatorWalletAddress)
                .orElseThrow(() -> new RuntimeException("User not found with wallet address: " + creatorWalletAddress));

        // Map the DTO to entity
        Chama chama = chamaMapper.toEntity(chamaDto);
        //check if the chama already exists
        if (chamaRepository.existsById(chama.getChamaAddress())) {
            throw new ChamaException("Chama already exists with wallet address: " + chama.getChamaAddress());
        }
        // Set the creator and add them as first member
        chama.setCreator(creator);
        chama.getMembers().add(creator);

        // Save the chama
        Chama savedChama = chamaRepository.save(chama);

        // Update the creator's chamas lists
        creator.getCreatedChamas().add(savedChama);
        creator.getMemberChamas().add(savedChama);
        userRepository.save(creator);

        // Map back to DTO and return
        return chamaMapper.toDto(savedChama);
    }

        /**
     * Add a member to a Chama
     * @param chamaWalletAddress The wallet address of the Chama
     * @param userWalletAddress The wallet address of the user to add
     * @return The updated Chama
     */
    @Transactional
    public ChamaDto addMemberToChama(String chamaWalletAddress, String userWalletAddress) {
        log.info("Adding member {} to Chama {}", userWalletAddress, chamaWalletAddress);

        // Get the Chama
        Chama chama = chamaRepository.findById(chamaWalletAddress)
                .orElseThrow(() -> new ChamaException("Chama not found with wallet address: " + chamaWalletAddress));

        // Get the User
        User user = userRepository.findByWalletAddress(userWalletAddress)
                .orElseThrow(() -> new ChamaException("User not found with wallet address: " + userWalletAddress));

        // Check if user is already a member of this Chama
        if (user.getMemberChamas().stream()
                .anyMatch(memberChama -> memberChama.getChamaAddress().equals(chamaWalletAddress))) {
            throw new ChamaException("User is already a member of this Chama");
        }

        // Check if chama is at maximum capacity
        if (chama.getMaximumMembers() != null && 
            chama.getMembers().size() >= chama.getMaximumMembers()) {
            throw new ChamaException("Chama has reached maximum member capacity");
        }

        // Add user to chama using the helper method
        chama.addMember(user);

        // Save the updated chama
        Chama updatedChama = chamaRepository.save(chama);

        log.info("Member added successfully to Chama {}", chamaWalletAddress);

        return chamaMapper.toDto(updatedChama);
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

        // Check if user is actually a member of this Chama
        boolean isMember = user.getMemberChamas().stream()
                .anyMatch(memberChama -> memberChama.getChamaAddress().equals(chamaWalletAddress));
        
        if (!isMember) {
            throw new ChamaException("User is not a member of this Chama");
        }

        // Check if user is the creator of the Chama
        if (chama.getCreator() != null && chama.getCreator().getWalletAddress().equals(userWalletAddress)) {
            throw new ChamaException("Creator cannot be removed from the Chama");
        }

        // Remove user from chama using the helper method
        chama.removeMember(user);

        // Save the updated chama
        Chama updatedChama = chamaRepository.save(chama);

        log.info("Member removed successfully from Chama {}", chamaWalletAddress);

        return chamaMapper.toDto(updatedChama);
    }

    /**
     * Uploads and associates an image with a specific Chama
     *
     * @param file The image file to upload
     * @param chamaWalletAddress The wallet address of the Chama
     * @return The URL/path of the uploaded image
     * @throws IllegalArgumentException for invalid file inputs
     * @throws EntityNotFoundException if Chama not found
     */
    @Transactional
    public String uploadChamaImage(MultipartFile file, String chamaWalletAddress) {
        log.info("Processing image upload for Chama with wallet address: {}", chamaWalletAddress);
        
        // Find and validate Chama
        Chama chama = chamaRepository.findById(chamaWalletAddress)
                .orElseThrow(() -> new EntityNotFoundException("Chama not found with wallet address: " + chamaWalletAddress));
        
        try {
            // Use FileStorageService to store the file
            String filename = fileStorageService.storeFile(file, "chama_");
            
            // Update Chama entity with image filename
            chama.setProfileImage(filename);
            chamaRepository.save(chama);
            
            log.info("Successfully uploaded image for Chama {}: {}", chamaWalletAddress, filename);
            return filename;
        } catch (Exception e) {
            log.error("Failed to upload image for Chama {}: {}", chamaWalletAddress, e.getMessage(), e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    /**
     * Get the image URL for a specific Chama
     *
     * @param chamaWalletAddress The wallet address of the Chama
     * @return The URL/path of the Chama's image
     * @throws EntityNotFoundException if Chama not found
     */
    public Resource getChamaProfileImage(String chamaWalletAddress){
        log.info("Fetching image for Chama with wallet address: {}", chamaWalletAddress);
        
        // Find and validate Chama
        Chama chama = chamaRepository.findById(chamaWalletAddress)
                .orElseThrow(() -> new EntityNotFoundException("Chama not found with wallet address: " + chamaWalletAddress));
        
                String profileImage = chama.getProfileImage();
        if (profileImage == null || profileImage.isEmpty()) {
            log.warn("No profile image found for Chama: {}", chamaWalletAddress);
            throw new EntityNotFoundException("No profile image found for this Chama");
        }
    
        // Load the image using FileStorageService
        return fileStorageService.loadFileAsResource(chama.getProfileImage());
    }

    /**
     * Get the content type of a Chama's profile image
     * 
     * @param chamaWalletAddress The wallet address of the Chama
     * @return The content type string
    */
    public String getChamaProfileImageContentType(String chamaWalletAddress) {
        Chama chama = chamaRepository.findById(chamaWalletAddress)
                .orElseThrow(() -> new EntityNotFoundException("Chama not found with wallet address: " + chamaWalletAddress));
        
        String profileImage = chama.getProfileImage();
        if (profileImage == null || profileImage.isEmpty()) {
            throw new EntityNotFoundException("No profile image found for this Chama");
        }
        
        return fileStorageService.getContentType(profileImage);
    }
}
