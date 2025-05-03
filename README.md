## Overview
ChamaDAO is a blockchain-based platform that enables chama groups (community-based savings groups) in Kenya to operate more efficiently through decentralized governance, mobile money integration, and AI assistance. This repository contains the server-side component of the ChamaDAO application.
The server handles critical backend operations after users have created their accounts or imported wallets on the frontend using account abstraction and wallet SDKs.
## Key Features
### 1. User and Identity Management
- **Profile Management**: Associates wallet addresses with user profile data including:
    - Full names
    - Mobile numbers (for m-Pesa integration)
    - Email addresses (optional)

- **KYC/AML Compliance**: Handles collection, storage, and verification of identity documents for regulatory compliance.

### 2. Financial Operations
- **m-Pesa Integration**: Facilitates deposits and withdrawals between Kenyan Shillings (KES) and USDT.
- **Currency Conversion**: Manages on-ramping (KES to USDT) and off-ramping (USDT to KES).
- **Balance Display**: Converts on-chain USDT balances to KES for familiar currency presentation.

### 3. Data Management
- **Off-Chain Storage**: Securely stores sensitive and supplementary data:
    - User profiles
    - Proposal texts and metadata
    - Media assets

- **Database System**: Utilizes efficient database technology for data storage and retrieval.

### 4. Communication and Notifications
- **Alert System**: Sends automated notifications for:
    - Loan repayment deadlines
    - Voting deadlines
    - Proposal updates
    - Loan extension reminders

### 5. Governance Support
- **Proposal Management**: Enables chama admins to create and store proposal details off-chain.
- **Voting Support**: Provides data and interfaces for proposal review within the mobile app.

### 6. Reputation and Credit System
- **Score Calculation**: Computes user reputation based on:
    - Loan repayment history
    - Chama contributions
    - Meeting attendance

- **Creditworthiness API**: Exposes reputation scores to enable loan guarantor assessments.

### 7. AI and Automation
- **Intelligent Features**:
    - Proposal summarization
    - Voting assistance
    - Governance chatbot
    - Treasury optimization recommendations
    - Natural language processing support

### 8. Analytics and Reporting
- **Data Insights**: Analyzes on-chain and off-chain data for performance tracking.
- **Report Generation**: Creates detailed reports for transparency and decision-making.

### 9. Blockchain Interaction
- **Data Retrieval**: Reads on-chain data from Base Layer 2 blockchain.
- **Transaction Execution**: Submits blockchain transactions when required.

### 10. Security and Compliance
- **Access Control**: Uses wallet addresses for authentication with role-based permissions.
- **Data Protection**: Encrypts sensitive information and ensures secure storage.
- **Regulatory Compliance**: Adheres to Kenyan financial regulations and data protection laws.

## Technical Architecture
- **API Layer**: RESTful/GraphQL APIs for mobile app communication
- **Database Layer**: Secure data storage and retrieval system
- **Blockchain Interface**: Communication with Base Layer 2 blockchain
- **m-Pesa Integration**: Secure mobile money transaction handling
- **AI Services**: Machine learning models for intelligent features
- **Security Layer**: Encryption, authentication, and authorization

## Getting Started
### Prerequisites
- Java SDK 23
- Jakarta EE
- Spring Data JPA
- Spring MVC
- Database (PostgreSQL/MongoDB recommended)
- Access to m-Pesa API credentials
- Access to Base Layer 2 node

### Installation
1. Clone the repository:
``` bash
   git clone https://github.com/Chama-DAO/chamadao-server
   cd chamadao-server
```
1. Configure environment variables:
``` bash
   cp .env.example .env
   # Edit .env with your specific configuration
```
1. Build the project:
``` bash
   ./mvnw clean install
```
1. Run the server:
``` bash
   ./mvnw spring-boot:run
```
## API Documentation
The server exposes APIs for the mobile application to interact with. Documentation can be accessed at:
``` 
http://localhost:8080/swagger-ui.html
```
Key API endpoints include:
- `/api/profiles` - User profile management
- `/api/financial` - m-Pesa transactions and currency conversions
- `/api/governance` - Proposal and voting operations
- `/api/reputation` - Credit scoring and reputation management

## Deployment
### Production Deployment
1. Build the production package:
``` bash
   ./mvnw clean package -Pprod
```
1. Deploy using Docker:
``` bash
   docker-compose up -d
```
### Scaling Considerations
- Implement load balancing for increased user traffic
- Consider database sharding for large data volumes
- Utilize caching for frequently accessed data
- Monitor blockchain interaction for bottlenecks

## Security Considerations
- All sensitive data must be encrypted at rest and in transit
- KYC documents require special handling for compliance
- API endpoints must validate wallet signatures
- Regular security audits should be conducted

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the [MIT License](LICENSE)
## Contact
ChamaDAO Team - contact@chamadao.com
Project Link: [https://github.com/Chama-DAO/chamadao-server](https://github.com/Chama-DAO/chamadao-server)
