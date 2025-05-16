package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.dto.GuarantorDto;
import com.chama.chamadao_server.models.dto.GuarantorUpdateDto;
import com.chama.chamadao_server.models.dto.LoanDto;
import com.chama.chamadao_server.models.dto.LoanRequestDto;
import com.chama.chamadao_server.models.enums.LoanStatus;
import com.chama.chamadao_server.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @GetMapping("/chama/{chamaAddress}")
    public ResponseEntity<List<LoanDto>> getLoansByChama(@PathVariable String chamaAddress) {
        return ResponseEntity.ok(loanService.getLoansByChama(chamaAddress));
    }

    @GetMapping("/borrower/{walletAddress}")
    public ResponseEntity<List<LoanDto>> getLoansByBorrower(@PathVariable String walletAddress) {
        return ResponseEntity.ok(loanService.getLoansByBorrower(walletAddress));
    }

    @GetMapping("/{loanId}/guarantors")
    public ResponseEntity<List<GuarantorDto>> getLoanGuarantors(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanGuarantors(loanId));
    }

    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@RequestBody LoanRequestDto createDTO) {
        return new ResponseEntity<>(loanService.createLoan(createDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{loanId}/guarantors")
    public ResponseEntity<GuarantorDto> updateGuarantor(
            @PathVariable Long loanId,
            @RequestBody GuarantorUpdateDto updateDTO) {
        return ResponseEntity.ok(loanService.updateGuarantor(loanId, updateDTO));
    }

    @PutMapping("/{loanId}/status")
    public ResponseEntity<LoanDto> updateLoanStatus(
            @PathVariable Long loanId,
            @RequestParam LoanStatus status) {
        return ResponseEntity.ok(loanService.updateLoanStatus(loanId, status));
    }
}
