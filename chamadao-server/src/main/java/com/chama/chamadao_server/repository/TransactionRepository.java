package com.chama.chamadao_server.repository;

import com.chama.chamadao_server.models.Transaction;
import com.chama.chamadao_server.models.enums.TransactionStatus;
import com.chama.chamadao_server.models.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Transaction entity
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find transactions by wallet address
     * @param walletAddress The wallet address to search for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByWalletAddress(String walletAddress, Pageable pageable);

    /**
     * Find transactions by mobile number
     * @param mobileNumber The mobile number to search for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByMobileNumber(String mobileNumber, Pageable pageable);

    /**
     * Find transactions by type
     * @param type The transaction type to search for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByType(TransactionType type, Pageable pageable);

    /**
     * Find transactions by status
     * @param status The transaction status to search for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);

    /**
     * Find transactions by M-Pesa receipt number
     * @param mpesaReceiptNumber The M-Pesa receipt number to search for
     * @return An optional transaction
     */
    Optional<Transaction> findByMpesaReceiptNumber(String mpesaReceiptNumber);

    /**
     * Find transactions by blockchain transaction hash
     * @param blockchainTxHash The blockchain transaction hash to search for
     * @return An optional transaction
     */
    Optional<Transaction> findByBlockchainTxHash(String blockchainTxHash);

    /**
     * Find transactions created between two dates
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return A page of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Page<Transaction> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find transactions by wallet address and type
     * @param walletAddress The wallet address to search for
     * @param type The transaction type to search for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByWalletAddressAndType(String walletAddress, TransactionType type, Pageable pageable);

    /**
     * Find transactions by wallet address and status
     * @param walletAddress The wallet address to search for
     * @param status The transaction status to search for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByWalletAddressAndStatus(String walletAddress, TransactionStatus status, Pageable pageable);
}