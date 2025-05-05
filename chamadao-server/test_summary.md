# ChamaDAO API Test Summary

This document provides a summary of the test data and tests created for the ChamaDAO API.

## Test Data

The test data is documented in `postman_test_data.md`, which provides:
- Setup instructions for Postman
- Test data for all endpoints
- Example requests and responses
- Step-by-step instructions for using each endpoint in Postman

## Automated Tests

The automated tests are located in the `src/test/java/com/chama/chamadao_server/tests` directory:

### UserControllerTests.java

Tests for the User endpoints:
1. `testGetUserProfile_Success` - Tests the successful retrieval of a user profile
2. `testGetUserProfile_InvalidWalletAddress` - Tests the error handling when an invalid wallet address is provided
3. `testUpdateUserProfile_Success` - Tests the successful update of a user profile
4. `testCreateUserProfile_Success` - Tests the successful creation of a new user profile

### KycControllerTests.java

Tests for the KYC endpoints:
1. `testUploadKycDocument_Success` - Tests the successful upload of a KYC document
2. `testGetKycDocuments_Success` - Tests the successful retrieval of KYC documents
3. `testGetKycStatus_Success` - Tests the successful retrieval of KYC status
4. `testVerifyKycDocuments_Success` - Tests the successful verification of KYC documents
5. `testRejectKycDocuments_Success` - Tests the successful rejection of KYC documents

### PaymentControllerTests.java

Tests for the Payment endpoints:
1. `testInitiateDeposit_Success` - Tests the successful initiation of a deposit
2. `testInitiateWithdrawal_Success` - Tests the successful initiation of a withdrawal
3. `testStkCallback_Success` - Tests the successful processing of an STK callback
4. `testB2CCallback_Success` - Tests the successful processing of a B2C callback

## Test Resources

The test resources are located in the `src/test/resources` directory:
- `test-id.jpg` - A test image file for KYC document upload testing (needs to be created manually)

## Running the Tests

### Running All Tests

To run all tests, use the following command:

```bash
mvn test
```

### Running Specific Tests

To run a specific test class, use the following command:

```bash
mvn test -Dtest=UserControllerTests
```

To run a specific test method, use the following command:

```bash
mvn test -Dtest=UserControllerTests#testGetUserProfile_Success
```

## Test Coverage

The tests cover all endpoints in the ChamaDAO API:

1. User Endpoints:
   - GET /api/users/{walletAddress}
   - PUT /api/users/{walletAddress}
   - POST /api/users

2. KYC Endpoints:
   - POST /api/users/{walletAddress}/kyc/documents
   - GET /api/users/{walletAddress}/kyc/documents
   - GET /api/users/{walletAddress}/kyc/status
   - POST /api/users/{walletAddress}/kyc/verify
   - POST /api/users/{walletAddress}/kyc/reject

3. Payment Endpoints:
   - POST /api/payments/deposit
   - POST /api/payments/withdraw
   - POST /api/payments/mpesa/stk-callback
   - POST /api/payments/mpesa/b2c-callback

## Notes

- The tests use Spring Boot's `TestRestTemplate` to make HTTP requests to the endpoints and verify the responses.
- For the KYC document upload test, a test image file named `test-id.jpg` needs to be created in the `src/test/resources` directory.
- The payment tests create mock M-Pesa callback objects to simulate the callbacks from the M-Pesa API.
- All tests include debug logging to help with troubleshooting.