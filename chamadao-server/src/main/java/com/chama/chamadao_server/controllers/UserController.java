package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{walletAddress}")
    public ResponseEntity<User> getUserProfile(@PathVariable String walletAddress) {
        return userService.getUserByWalletAddress(walletAddress)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUserProfile(@RequestBody User user) {
        User createdUser = userService.createUserProfile(user);
        return ResponseEntity.ok(createdUser);
    }

}