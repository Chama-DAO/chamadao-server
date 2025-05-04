package com.chama.chamadao_server.repository;

import com.chama.chamadao_server.models.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    List<KycDocument> findByUserWalletAddress(String walletAddress);
}
