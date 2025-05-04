package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserByWalletAddress(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress);
    }

    //create user
    public User createUserProfile(User user) {
        //check if the user already exists
        Optional<User> existingUser = userRepository.findByWalletAddress(user.getWalletAddress());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        return userRepository.save(user);
    }

}
