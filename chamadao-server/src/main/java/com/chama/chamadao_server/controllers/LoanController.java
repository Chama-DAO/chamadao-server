package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.dto.GuarantorDto;
import com.chama.chamadao_server.models.dto.GuarantorUpdateDto;
import com.chama.chamadao_server.models.dto.LoanDto;
import com.chama.chamadao_server.models.dto.LoanRequestDto;
import com.chama.chamadao_server.models.enums.LoanStatus;
import com.chama.chamadao_server.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Management", description = "APIs for managing loans")
public class LoanController {
    private final LoanService loanService;

    @Operation(
            summary = "Get loans of a chama",
            description = "Retrieves all loans associated with a specific chama (community savings group)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loans",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDto.class))),
            @ApiResponse(responseCode = "404", description = "Chama not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/chama/{chamaAddress}")
    public ResponseEntity<List<LoanDto>> getLoansByChama(
            @Parameter(description = "Blockchain address of the chama", required = true)
            @PathVariable String chamaAddress) {
        return ResponseEntity.ok(loanService.getLoansByChama(chamaAddress));
    }

    @Operation(
            summary = "Get loans of a borrower",
            description = "Retrieves all loans associated with a specific borrower wallet address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loans",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDto.class))),
            @ApiResponse(responseCode = "404", description = "Borrower not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/borrower/{walletAddress}")
    public ResponseEntity<List<LoanDto>> getLoansByBorrower(
            @Parameter(description = "Blockchain wallet address of the borrower", required = true)
            @PathVariable String walletAddress) {
        return ResponseEntity.ok(loanService.getLoansByBorrower(walletAddress));
    }

    @Operation(
            summary = "Get loan guarantors",
            description = "Retrieves all guarantors for a specific loan"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved guarantors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuarantorDto.class))),
            @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/{loanId}/guarantors")
    public ResponseEntity<List<GuarantorDto>> getLoanGuarantors(
            @Parameter(description = "Unique identifier of the loan", required = true)
            @PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanGuarantors(loanId));
    }

    @Operation(
            summary = "Create a new loan",
            description = "Creates a new loan application in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<LoanDto> createLoan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Loan details for creation",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoanRequestDto.class))
            )
            @RequestBody LoanRequestDto createDTO) {
        return new ResponseEntity<>(loanService.createLoan(createDTO), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update guarantor information",
            description = "Updates guarantor information for a specific loan"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guarantor information updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuarantorDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan or guarantor not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/{loanId}/guarantors")
    public ResponseEntity<GuarantorDto> updateGuarantor(
            @Parameter(description = "Unique identifier of the loan", required = true)
            @PathVariable Long loanId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Guarantor update details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GuarantorUpdateDto.class))
            )
            @RequestBody GuarantorUpdateDto updateDTO) {
        return ResponseEntity.ok(loanService.updateGuarantor(loanId, updateDTO));
    }

    @Operation(
            summary = "Update loan status",
            description = "Updates the status of a specific loan"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/{loanId}/status")
    public ResponseEntity<LoanDto> updateLoanStatus(
            @Parameter(description = "Unique identifier of the loan", required = true)
            @PathVariable Long loanId,
            @Parameter(description = "New status to set for the loan", required = true,
                    schema = @Schema(implementation = LoanStatus.class))
            @RequestParam LoanStatus status) {
        return ResponseEntity.ok(loanService.updateLoanStatus(loanId, status));
    }
}