package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.Transaction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

/**
 * Service for blockchain operations
 * Handles USDT transfers on the Ethereum blockchain
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {

    private final WalletService walletService;

    @Value("${blockchain.rpc.url:https://mainnet.infura.io/v3/}")
    private String rpcUrl;

    @Value("${blockchain.wallet.private-key}")
    private String privateKey;

    @Value("${blockchain.usdt.contract-address:0xdAC17F958D2ee523a2206206994597C13D831ec7}")
    private String usdtContractAddress;

    private Web3j web3j;
    private Credentials credentials;

    /**
     * Initialize the Web3j instance and credentials
     * This method is called automatically when the service is created
     */
    @PostConstruct
    public void init() {
        log.info("Initializing BlockchainService with RPC URL: {}", rpcUrl);
        web3j = Web3j.build(new HttpService(rpcUrl));
        credentials = Credentials.create(privateKey);
        log.info("BlockchainService initialized with wallet address: {}", credentials.getAddress());
    }

    /**
     * Transfer USDT to a user's wallet
     * @param transaction The transaction containing the wallet address and amount
     * @return The transaction hash
     */
    public CompletableFuture<String> transferUsdtToWallet(Transaction transaction) {
        log.info("Transferring {} USDT to wallet: {}", 
                transaction.getAmountUSDT(), transaction.getWalletAddress());

        // Verify wallet address
        if (!walletService.verifyWalletAddress(transaction.getWalletAddress())) {
            log.error("Invalid wallet address: {}", transaction.getWalletAddress());
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Invalid wallet address"));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // NOTE: This is a placeholder implementation for demonstration purposes
                // In a real implementation, you would:
                // 1. Load the USDT ERC20 contract using its ABI and address
                // 2. Call the transfer or transferFrom method on the contract
                // 3. Handle gas fees and nonce management

                log.info("Sending {} USDT to {}", 
                        transaction.getAmountUSDT(), transaction.getWalletAddress());

                // Example of how it would be implemented with a proper ERC20 contract:
                // ERC20 usdt = ERC20.load(
                //     usdtContractAddress,
                //     web3j,
                //     credentials,
                //     new DefaultGasProvider()
                // );
                // 
                // BigInteger tokenAmount = Convert.toWei(
                //     transaction.getAmountUSDT().toString(), 
                //     Convert.Unit.MWEI
                // ).toBigInteger();
                // 
                // TransactionReceipt receipt = usdt.transfer(
                //     transaction.getWalletAddress(), 
                //     tokenAmount
                // ).send();

                // For now, we're using ETH transfer as a placeholder
                TransactionReceipt receipt = Transfer.sendFunds(
                        web3j, 
                        credentials, 
                        transaction.getWalletAddress(), 
                        transaction.getAmountUSDT(), 
                        Convert.Unit.ETHER
                ).send();

                String txHash = receipt.getTransactionHash();
                log.info("USDT transfer successful. Transaction hash: {}", txHash);
                return txHash;
            } catch (Exception e) {
                log.error("Failed to transfer USDT", e);
                throw new RuntimeException("Failed to transfer USDT: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Check if a transaction has been confirmed on the blockchain
     * @param txHash The transaction hash
     * @return True if the transaction is confirmed, false otherwise
     */
    public boolean isTransactionConfirmed(String txHash) {
        log.info("Checking if transaction is confirmed: {}", txHash);

        try {
            // Get transaction receipt
            org.web3j.protocol.core.methods.response.EthGetTransactionReceipt receipt = 
                    web3j.ethGetTransactionReceipt(txHash).send();

            // Check if transaction is confirmed
            if (receipt.getTransactionReceipt().isPresent()) {
                boolean confirmed = receipt.getTransactionReceipt().get().getBlockNumber() != null;
                log.info("Transaction {} is {}", txHash, confirmed ? "confirmed" : "pending");
                return confirmed;
            } else {
                log.info("Transaction {} not found", txHash);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to check transaction status", e);
            return false;
        }
    }
}
