package com.chama.chamadao_server.repository;

import com.chama.chamadao_server.models.Chama;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Chama entities
 * The primary key is walletAddress (String), so findById can be used to find a Chama by wallet address
 */
public interface ChamaRepository extends JpaRepository<Chama, String> {
    // No need for findByWalletAddress since walletAddress is the ID and findById already exists
}
