package com.chama.chamadao_server.repository;

import com.chama.chamadao_server.models.enums.LoanStatus;
import com.chama.chamadao_server.models.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByChama_ChamaAddress(String chamaAddress);
    List<Loan> findByBorrowerWalletAddress(String walletAddress);
    List<Loan> findByStatus(LoanStatus status);

    @Query("SELECT l from Loan l LEFT JOIN FETCH l.chama LEFT JOIN FETCH l.borrower WHERE l.id = :id")
    Optional<Loan> findWithChamaAndBorrowerById(@Param("id") Long id);

    @Query("SELECT l from Loan l LEFT JOIN FETCH l.chama LEFT JOIN FETCH l.borrower WHERE l.chama.chamaAddress = :chamaAddress")
    List<Loan> findByChamaAddressWithChamaAndBorrower(@Param("chamaAddress") String chamaAddress);

    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.guarantors WHERE l.id = :id")
    Optional<Loan> findWithGuarantorsById(@Param("id") Long id);
}
