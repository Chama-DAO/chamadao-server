package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.GuarantorDto;
import com.chama.chamadao_server.models.dto.GuarantorUpdateDto;
import com.chama.chamadao_server.models.dto.LoanDto;
import com.chama.chamadao_server.models.dto.LoanRequestDto;
import com.chama.chamadao_server.models.enums.LoanStatus;
import com.chama.chamadao_server.models.loan.Loan;
import com.chama.chamadao_server.models.loan.LoanGuarantor;
import com.chama.chamadao_server.repository.ChamaRepository;
import com.chama.chamadao_server.repository.LoanGuarantorRepository;
import com.chama.chamadao_server.repository.LoanRepository;
import com.chama.chamadao_server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final LoanGuarantorRepository guarantorRepository;
    private final UserRepository userRepository;
    private final ChamaRepository chamaRepository;

    public List<LoanDto> getLoansByChama(String chamaAddress) {
        return loanRepository.findByChama_ChamaAddress(chamaAddress).stream()
                .map(this::mapToLoanDTO)
                .collect(Collectors.toList());
    }

    public List<LoanDto> getLoansByBorrower(String walletAddress) {
        return loanRepository.findByBorrowerWalletAddress(walletAddress).stream()
                .map(this::mapToLoanDTO)
                .collect(Collectors.toList());
    }

    public List<GuarantorDto> getLoanGuarantors(Long loanId) {
        Loan loan = loanRepository.findWithGuarantorsById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        return loan.getGuarantors().stream()
                .map(this::mapToGuarantorDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanDto createLoan(LoanRequestDto createDTO) {
        User borrower = userRepository.findByWalletAddress(createDTO.getBorrowerWalletAddress())
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));

        Chama chama = chamaRepository.findById(createDTO.getChamaAddress())
                .orElseThrow(() -> new EntityNotFoundException("Chama not found"));

        // Calculate due date from term
        LocalDateTime dueDate = LocalDateTime.now().plus(Period.parse(createDTO.getTerm()));

        Loan loan = Loan.builder()
                .chama(chama)
                .borrower(borrower)
                .loanAmount(createDTO.getAmount())
                .loanInterestRate(createDTO.getInterestRate())
                .loanTerm(createDTO.getTerm())
                .dueDate(dueDate)
                .requiredGuarantors(createDTO.getRequiredGuarantors())
                .status(LoanStatus.PENDING)
                .loanPenalty(createDTO.getPenalty())
                .loanPenaltyExpirationPeriod(createDTO.getPenaltyPeriod())
                .totalGuaranteedAmount(BigDecimal.ZERO)
                .amountRepaid(BigDecimal.ZERO)
                .outstandingAmount(createDTO.getAmount())
                .build();

        return mapToLoanDTO(loanRepository.save(loan));
    }

    @Transactional
    public GuarantorDto updateGuarantor(Long loanId, GuarantorUpdateDto updateDTO) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        User guarantor = userRepository.findByWalletAddress(updateDTO.getWalletAddress())
                .orElseThrow(() -> new EntityNotFoundException("Guarantor not found"));

        LoanGuarantor loanGuarantor = guarantorRepository
                .findByLoanIdAndGuarantorWalletAddress(loanId, updateDTO.getWalletAddress())
                .orElse(LoanGuarantor.builder()
                        .loan(loan)
                        .guarantor(guarantor)
                        .build());

        loanGuarantor.setGuaranteedAmount(updateDTO.getAmount());
        loanGuarantor.setStatus(LoanGuarantor.GuarantorStatus.valueOf(updateDTO.getStatus()));

        LoanGuarantor saved = guarantorRepository.save(loanGuarantor);

        // Update loan total guaranteed amount
        updateLoanGuarantorsStatus(loan);

        return mapToGuarantorDTO(saved);
    }

    @Transactional
    public LoanDto updateLoanStatus(Long loanId, LoanStatus status) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        loan.setStatus(status);
        return mapToLoanDTO(loanRepository.save(loan));
    }

    private void updateLoanGuarantorsStatus(Loan loan) {
        BigDecimal approvedAmount = loan.getGuarantors().stream()
                .filter(g -> g.getStatus() == LoanGuarantor.GuarantorStatus.APPROVED)
                .map(LoanGuarantor::getGuaranteedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        loan.setTotalGuaranteedAmount(approvedAmount);

        // Auto approve loan if conditions met
        if (loan.getStatus() == LoanStatus.PENDING
                && approvedAmount.compareTo(loan.getLoanAmount()) >= 0
                && loan.getGuarantors().size() >= loan.getRequiredGuarantors()) {
            loan.setStatus(LoanStatus.APPROVED);
        }

        loanRepository.save(loan);
    }

    // Mapper methods
    private LoanDto mapToLoanDTO(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .chamaAddress(loan.getChama().getChamaAddress())
                .chamaName(loan.getChama().getName())
                .borrowerWalletAddress(loan.getBorrower().getWalletAddress())
                .borrowerName(loan.getBorrower().getFullName())
                .loanAmount(loan.getLoanAmount())
                .interestRate(loan.getLoanInterestRate())
                .term(loan.getLoanTerm())
                .dueDate(loan.getDueDate())
                .status(loan.getStatus())
                .requiredGuarantors(loan.getRequiredGuarantors())
                .totalGuaranteedAmount(loan.getTotalGuaranteedAmount())
                .amountRepaid(loan.getAmountRepaid())
                .outstandingAmount(loan.getOutstandingAmount())
                .build();
    }

    private GuarantorDto mapToGuarantorDTO(LoanGuarantor guarantor) {
        return GuarantorDto.builder()
                .walletAddress(guarantor.getGuarantor().getWalletAddress())
                .name(guarantor.getGuarantor().getFullName())
                .guaranteedAmount(guarantor.getGuaranteedAmount())
                .status(guarantor.getStatus().toString())
                .build();
    }
}
