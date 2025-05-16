package com.chama.chamadao_server.repository;

import com.chama.chamadao_server.models.loan.LoanGuarantor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanGuarantorRepository extends JpaRepository<LoanGuarantor, Long> {
    List<LoanGuarantor> findByLoanId(Long loanId);

    List<LoanGuarantor> findByGuarantorWalletAddress(String walletAddress);

    Optional<LoanGuarantor> findByLoanIdAndGuarantorWalletAddress(Long loanId, String walletAddress);
}
