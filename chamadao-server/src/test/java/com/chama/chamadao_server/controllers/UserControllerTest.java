//package com.chama.chamadao_server.controllers;
//
//import com.chama.chamadao_server.mappers.UserMapper;
//import com.chama.chamadao_server.models.User;
//import com.chama.chamadao_server.models.dto.UserDto;
//import com.chama.chamadao_server.models.enums.KycStatus;
//import com.chama.chamadao_server.services.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.HashSet;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//public class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private UserController userController;
//
//    private User testUser;
//    private UserDto testUserDto;
//    private final String validWalletAddress = "0x1234567890123456789012345678901234567890";
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // Setup test user
//        testUser = new User();
//        testUser.setWalletAddress(validWalletAddress);
//        testUser.setFullName("Test User");
//        testUser.setEmail("test@example.com");
//        testUser.setMobileNumber("+1234567890");
//        //testUser.setKycStatus(KycStatus.PENDING);
//        testUser.setReputationScore(0.0);
//        testUser.setCreatedAt(LocalDateTime.now());
//        testUser.setUpdatedAt(LocalDateTime.now());
//        //testUser.setRoles(new HashSet<>());
//
//        // Setup test user DTO
//        testUserDto = new UserDto();
//        testUserDto.setWalletAddress(validWalletAddress);
//        testUserDto.setFullName("Test User");
//        testUserDto.setEmail("test@example.com");
//        testUserDto.setMobileNumber("+1234567890");
//        //testUserDto.setKycVerified(false);
//        testUserDto.setCreatedAt(LocalDateTime.now());
//        testUserDto.setUpdatedAt(LocalDateTime.now());
//        //testUserDto.setRoles(new HashSet<>());
//    }
//
//    @Test
//    public void testGetUserProfile_Success() {
//        // Setup
//        when(userService.getUserByWalletAddress(anyString())).thenReturn(testUser);
//        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);
//
//        // Execute
//        ResponseEntity<UserDto> response = userController.getUserProfile(validWalletAddress);
//
//        // Verify
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(validWalletAddress, response.getBody().getWalletAddress());
//        assertEquals("Test User", response.getBody().getFullName());
//
//        System.out.println("[DEBUG_LOG] Successfully retrieved user profile for wallet address: " + validWalletAddress);
//    }
//
//    @Test
//    public void testGetUserProfile_InvalidWalletAddress() {
//        // Setup
//        when(userService.getUserByWalletAddress(anyString())).thenThrow(new IllegalArgumentException("Invalid wallet address format"));
//
//        // Execute
//        ResponseEntity<UserDto> response = userController.getUserProfile("invalid-wallet-address");
//
//        // Verify
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//
//        System.out.println("[DEBUG_LOG] Correctly handled invalid wallet address");
//    }
//
//    @Test
//    public void testUpdateUserProfile_Success() {
//        // Setup
//        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
//        when(userService.updateUserProfile(anyString(), any(User.class))).thenReturn(testUser);
//        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);
//
//        // Execute
//        ResponseEntity<UserDto> response = userController.updateUserProfile(validWalletAddress, testUserDto);
//
//        // Verify
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(validWalletAddress, response.getBody().getWalletAddress());
//
//        System.out.println("[DEBUG_LOG] Successfully updated user profile for wallet address: " + validWalletAddress);
//    }
//
//    @Test
//    public void testCreateUserProfile_Success() {
//        // Setup
//        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
//        when(userService.createUserProfile(any(User.class))).thenReturn(testUser);
//        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);
//
//        // Execute
//        ResponseEntity<UserDto> response = userController.createUserProfile(testUserDto);
//
//        // Verify
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(validWalletAddress, response.getBody().getWalletAddress());
//
//        System.out.println("[DEBUG_LOG] Successfully created user profile for wallet address: " + validWalletAddress);
//    }
//}