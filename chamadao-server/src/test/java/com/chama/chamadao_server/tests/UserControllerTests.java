package com.chama.chamadao_server.tests;

import com.chama.chamadao_server.models.dto.UserDto;
import com.chama.chamadao_server.models.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String validWalletAddress = "0x1234567890123456789012345678901234567890";
    private final String newWalletAddress = "0x2345678901234567890123456789012345678901";

    @Test
    public void testGetUserProfile_Success() {
        // Execute
        ResponseEntity<UserDto> response = restTemplate.getForEntity(
                "/api/users/" + validWalletAddress, UserDto.class);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validWalletAddress, response.getBody().getWalletAddress());
        
        System.out.println("[DEBUG_LOG] User profile response: " + response.getBody());
    }

    @Test
    public void testGetUserProfile_InvalidWalletAddress() {
        // Execute
        ResponseEntity<UserDto> response = restTemplate.getForEntity(
                "/api/users/invalid-wallet-address", UserDto.class);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        System.out.println("[DEBUG_LOG] Invalid wallet address response status: " + response.getStatusCode());
    }

    @Test
    public void testUpdateUserProfile_Success() {
        // Setup
        UserDto userDto = new UserDto();
        userDto.setWalletAddress(validWalletAddress);
        userDto.setFullName("John Doe Updated");
        userDto.setMobileNumber("+254712345678");
        userDto.setEmail("john.updated@example.com");
       // userDto.setKycVerified(false);
//        Set<UserRole> roles = new HashSet<>();
//        roles.add(UserRole.CHAMA_MEMBER);
        //userDto.setRoles(roles);

        // Execute
        ResponseEntity<UserDto> response = restTemplate.exchange(
                "/api/users/" + validWalletAddress,
                HttpMethod.PUT,
                new HttpEntity<>(userDto),
                UserDto.class);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validWalletAddress, response.getBody().getWalletAddress());
        assertEquals("John Doe Updated", response.getBody().getFullName());
        assertEquals("john.updated@example.com", response.getBody().getEmail());
        
        System.out.println("[DEBUG_LOG] Updated user profile response: " + response.getBody());
    }

    @Test
    public void testCreateUserProfile_Success() {
        // Setup
        UserDto userDto = new UserDto();
        userDto.setWalletAddress(newWalletAddress);
        userDto.setFullName("Jane Smith");
        userDto.setMobileNumber("+254723456789");
        userDto.setEmail("jane.smith@example.com");
        //userDto.setKycVerified(false);
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.CHAMA_MEMBER);
        //userDto.setRoles(roles);

        // Execute
        ResponseEntity<UserDto> response = restTemplate.postForEntity(
                "/api/users",
                userDto,
                UserDto.class);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newWalletAddress, response.getBody().getWalletAddress());
        assertEquals("Jane Smith", response.getBody().getFullName());
        assertEquals("jane.smith@example.com", response.getBody().getEmail());
        
        System.out.println("[DEBUG_LOG] Created user profile response: " + response.getBody());
    }
}