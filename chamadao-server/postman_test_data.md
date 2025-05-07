# ChamaDAO API Test Data for Postman

This document provides test data for all endpoints in the ChamaDAO application and instructions on how to use it in Postman.

## Table of Contents
- [Setup Instructions](#setup-instructions)
- [User Endpoints](#user-endpoints)
- [KYC Endpoints](#kyc-endpoints)
- [Payment Endpoints](#payment-endpoints)

## Setup Instructions

1. Download and install [Postman](https://www.postman.com/downloads/)
2. Import the provided collection (optional)
3. Set up environment variables (optional)
4. Use the test data provided below for each endpoint

### Blockchain Configuration

The application integrates with the Ethereum blockchain for USDT transfers. The following configuration is used:

- **RPC URL**: The URL of the Ethereum node (default: https://mainnet.infura.io/v3/)
- **USDT Contract Address**: The address of the USDT token contract on Ethereum (default: 0xdAC17F958D2ee523a2206206994597C13D831ec7)
- **Wallet Private Key**: The private key of the wallet used for sending USDT (this should be kept secure)

When testing the deposit flow, after a successful M-Pesa payment, the system will automatically:
1. Convert the KES amount to USDT using current exchange rates
2. Transfer the USDT to the user's wallet address
3. Record the blockchain transaction hash in the database

## User Endpoints

### 1. Get User Profile

**Endpoint:** `GET /api/users/{walletAddress}`

**Description:** Get a user profile by wallet address. If the profile doesn't exist, a basic profile will be initialized automatically.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user

**Example Request:**
```
GET /api/users/0x1234567890123456789012345678901234567890
```

**Example Response:**
```json
{
  "walletAddress": "0x1234567890123456789012345678901234567890",
  "fullName": "John Doe",
  "mobileNumber": "+254712345678",
  "email": "john.doe@example.com",
  "kycVerified": false,
  "createdAt": "2023-06-15",
  "updatedAt": "2023-06-15",
  "roles": ["CHAMA_MEMBER"]
}
```

**Postman Instructions:**
1. Create a new GET request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890`
3. Send the request
4. Verify the response matches the expected format

### 2. Update User Profile

**Endpoint:** `PUT /api/users/{walletAddress}`

**Description:** Update an existing user profile.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user

**Request Body:**
```json
{
  "walletAddress": "0x1234567890123456789012345678901234567890",
  "fullName": "John Doe Updated",
  "mobileNumber": "+254712345678",
  "email": "john.updated@example.com",
  "kycVerified": false,
  "roles": ["CHAMA_MEMBER"]
}
```

**Example Response:**
```json
{
  "walletAddress": "0x1234567890123456789012345678901234567890",
  "fullName": "John Doe Updated",
  "mobileNumber": "+254712345678",
  "email": "john.updated@example.com",
  "kycVerified": false,
  "createdAt": "2023-06-15",
  "updatedAt": "2023-06-16",
  "roles": ["CHAMA_MEMBER"]
}
```

**Postman Instructions:**
1. Create a new PUT request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890`
3. Go to the Body tab, select "raw" and "JSON"
4. Enter the request body JSON
5. Send the request
6. Verify the response matches the expected format

### 3. Create User Profile

**Endpoint:** `POST /api/users`

**Description:** Create a new user profile (admin/testing purposes only).

**Request Body:**
```json
{
  "walletAddress": "0x2345678901234567890123456789012345678901",
  "fullName": "Jane Smith",
  "mobileNumber": "+254723456789",
  "email": "jane.smith@example.com",
  "kycVerified": false,
  "roles": ["CHAMA_MEMBER"]
}
```

**Example Response:**
```json
{
  "walletAddress": "0x2345678901234567890123456789012345678901",
  "fullName": "Jane Smith",
  "mobileNumber": "+254723456789",
  "email": "jane.smith@example.com",
  "kycVerified": false,
  "createdAt": "2023-06-16",
  "updatedAt": "2023-06-16",
  "roles": ["CHAMA_MEMBER"]
}
```

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/users`
3. Go to the Body tab, select "raw" and "JSON"
4. Enter the request body JSON
5. Send the request
6. Verify the response matches the expected format

## KYC Endpoints

### 1. Upload KYC Document

**Endpoint:** `POST /api/users/{walletAddress}/kyc/documents`

**Description:** Upload a KYC document for a user.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user
- `file` (form data): The document file
- `documentType` (form data): The type of document (NATIONAL_ID, PASSPORT, DRIVERS_LICENSE, PROOF_OF_ADDRESS)

**Example Response:**
```json
{
  "id": 1,
  "userWalletAddress": "0x1234567890123456789012345678901234567890",
  "documentType": "NATIONAL_ID",
  "documentPath": "/uploads/kyc/0x1234567890123456789012345678901234567890/12345678-1234-1234-1234-123456789012_id.jpg",
  "documentHash": "abcdef1234567890abcdef1234567890",
  "verified": false,
  "uploadedAt": "2023-06-16",
  "verifiedAt": null
}
```

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890/kyc/documents`
3. Go to the Body tab, select "form-data"
4. Add a key "file" of type "File" and select a file from your computer
5. Add a key "documentType" of type "Text" and enter "NATIONAL_ID"
6. Send the request
7. Verify the response matches the expected format

### 2. Get KYC Documents

**Endpoint:** `GET /api/users/{walletAddress}/kyc/documents`

**Description:** Get all KYC documents for a user.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user

**Example Response:**
```json
[
  {
    "id": 1,
    "userWalletAddress": "0x1234567890123456789012345678901234567890",
    "documentType": "NATIONAL_ID",
    "documentPath": "/uploads/kyc/0x1234567890123456789012345678901234567890/12345678-1234-1234-1234-123456789012_id.jpg",
    "documentHash": "abcdef1234567890abcdef1234567890",
    "verified": false,
    "uploadedAt": "2023-06-16",
    "verifiedAt": null
  }
]
```

**Postman Instructions:**
1. Create a new GET request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890/kyc/documents`
3. Send the request
4. Verify the response matches the expected format

### 3. Get KYC Status

**Endpoint:** `GET /api/users/{walletAddress}/kyc/status`

**Description:** Get the KYC status for a user.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user

**Example Response:**
```
"PENDING"
```

**Postman Instructions:**
1. Create a new GET request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890/kyc/status`
3. Send the request
4. Verify the response is one of: "PENDING", "VERIFIED", or "REJECTED"

### 4. Verify KYC Documents

**Endpoint:** `POST /api/users/{walletAddress}/kyc/verify`

**Description:** Verify KYC documents for a user.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user

**Example Response:**
```
"KYC documents verified successfully"
```

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890/kyc/verify`
3. Send the request
4. Verify the response matches the expected format

### 5. Reject KYC Documents

**Endpoint:** `POST /api/users/{walletAddress}/kyc/reject`

**Description:** Reject KYC documents for a user.

**Request Parameters:**
- `walletAddress` (path parameter): The wallet address of the user

**Example Response:**
```
"KYC documents rejected"
```

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/users/0x1234567890123456789012345678901234567890/kyc/reject`
3. Send the request
4. Verify the response matches the expected format

## Payment Endpoints

### 1. Initiate Deposit

**Endpoint:** `POST /api/payments/deposit`

**Description:** Initiate a deposit using M-Pesa STK push. After a successful M-Pesa payment, the system automatically converts the KES amount to USDT and transfers it to the user's wallet address on the blockchain.

**Request Parameters:**
- `walletAddress` (query parameter): The wallet address of the user
- `phoneNumber` (query parameter): The phone number to send the STK push to
- `amount` (query parameter): The amount in KES

**Example Request:**
```
POST /api/payments/deposit?walletAddress=0x1234567890123456789012345678901234567890&phoneNumber=+254712345678&amount=1000
```

**Example Response:**
```json
{
  "merchantRequestID": "12345-67890-1",
  "checkoutRequestID": "ws_CO_123456789012345678",
  "responseCode": "0",
  "responseDescription": "Success. Request accepted for processing",
  "customerMessage": "Success. Request accepted for processing"
}
```

**Blockchain Integration:**
After the M-Pesa payment is confirmed via the STK callback, the system:
1. Converts the KES amount to USDT using current exchange rates
2. Transfers the USDT amount to the user's wallet address on the Ethereum blockchain
3. Records the blockchain transaction hash in the database

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/payments/deposit`
3. Go to the Params tab
4. Add key "walletAddress" with value "0x1234567890123456789012345678901234567890"
5. Add key "phoneNumber" with value "+254712345678"
6. Add key "amount" with value "1000"
7. Send the request
8. Verify the response matches the expected format

### 2. Initiate Withdrawal

**Endpoint:** `POST /api/payments/withdraw`

**Description:** Initiate a withdrawal using M-Pesa B2C.

**Request Parameters:**
- `walletAddress` (query parameter): The wallet address of the user
- `phoneNumber` (query parameter): The phone number to send the money to
- `amount` (query parameter): The amount in KES

**Example Request:**
```
POST /api/payments/withdraw?walletAddress=0x1234567890123456789012345678901234567890&phoneNumber=+254712345678&amount=500
```

**Example Response:**
```json
{
  "conversationID": "AG_20230616_12345678901234567",
  "originatorConversationID": "12345-67890-2",
  "responseCode": "0",
  "responseDescription": "Accept the service request successfully."
}
```

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/payments/withdraw`
3. Go to the Params tab
4. Add key "walletAddress" with value "0x1234567890123456789012345678901234567890"
5. Add key "phoneNumber" with value "+254712345678"
6. Add key "amount" with value "500"
7. Send the request
8. Verify the response matches the expected format

### 3. STK Callback

**Endpoint:** `POST /api/payments/mpesa/stk-callback`

**Description:** Callback endpoint for M-Pesa STK push (deposit). When this callback is received with a successful result code, the system automatically initiates a USDT transfer to the user's wallet on the blockchain.

**Request Body:**
```json
{
  "Body": {
    "stkCallback": {
      "MerchantRequestID": "12345-67890-1",
      "CheckoutRequestID": "ws_CO_123456789012345678",
      "ResultCode": 0,
      "ResultDesc": "The service request is processed successfully.",
      "CallbackMetadata": {
        "Item": [
          {
            "Name": "Amount",
            "Value": 1000
          },
          {
            "Name": "MpesaReceiptNumber",
            "Value": "PBL123456"
          },
          {
            "Name": "TransactionDate",
            "Value": 20230616112233
          },
          {
            "Name": "PhoneNumber",
            "Value": 254712345678
          }
        ]
      }
    }
  }
}
```

**Example Response:**
```
"Callback processed successfully"
```

**Blockchain Integration:**
When this callback is processed successfully:
1. The system finds the pending transaction associated with the phone number
2. Converts the KES amount to USDT using current exchange rates
3. Initiates a transfer of USDT to the user's wallet address on the Ethereum blockchain
4. Updates the transaction record with the blockchain transaction hash

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/payments/mpesa/stk-callback`
3. Go to the Body tab, select "raw" and "JSON"
4. Enter the request body JSON
5. Send the request
6. Verify the response matches the expected format

### 4. B2C Callback

**Endpoint:** `POST /api/payments/mpesa/b2c-callback`

**Description:** Callback endpoint for M-Pesa B2C (withdrawal).

**Request Body:**
```json
{
  "Result": {
    "ResultType": 0,
    "ResultCode": 0,
    "ResultDesc": "The service request is processed successfully.",
    "OriginatorConversationID": "12345-67890-2",
    "ConversationID": "AG_20230616_12345678901234567",
    "TransactionID": "PBL123457",
    "ResultParameters": {
      "ResultParameter": [
        {
          "Key": "TransactionAmount",
          "Value": 500
        },
        {
          "Key": "TransactionReceipt",
          "Value": "PBL123457"
        },
        {
          "Key": "TransactionCompletionDate",
          "Value": "20230616112233"
        },
        {
          "Key": "RecipientPhoneNumber",
          "Value": "254712345678"
        }
      ]
    }
  }
}
```

**Example Response:**
```
"Callback processed successfully"
```

**Postman Instructions:**
1. Create a new POST request
2. Enter the URL: `{{base_url}}/api/payments/mpesa/b2c-callback`
3. Go to the Body tab, select "raw" and "JSON"
4. Enter the request body JSON
5. Send the request
6. Verify the response matches the expected format
