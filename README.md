# KYC Service - InvestBuddy AI

The **KYC Service** manages the Know Your Customer (KYC) verification process for users in the **InvestBuddy AI** platform using **Veriff API**. It ensures regulatory compliance and validates user identities through asynchronous communication via **Kafka**.

---

## 📜 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
---

## ✨ Features

- **User Identity Verification**:
    - Processes user registration events to initiate KYC checks using Veriff API.
    - Validates documents, identity, and compliance.
- **Event-Driven Communication**:
    - Listens to user registration events from the `user-created` Kafka topic.
    - Sends KYC status updates to the `kyc-verification` Kafka topic.
- **Integration with Discovery Server**:
    - Registers itself for service discovery and communication.
- **KYC Status Management**:
    - Tracks and updates KYC statuses (e.g., PENDING, APPROVED, REJECTED) via webhooks.
---

## 🏗️ Architecture

The **KYC Service** is part of the **InvestBuddy AI** microservices ecosystem and uses an event-driven architecture with **Kafka**. Key technologies include:

- **Spring Boot**: For building RESTful APIs and business logic.
- **Kafka**: For message-driven communication with other services.
- **PostgreSQL**: For storing KYC status and user verification data.
- **Spring Cloud**: Integration with Eureka for service discovery.

---

## ✅ Prerequisites

Ensure the following are installed before setting up the **KYC Service**:

- **Java 21** or higher
- **Maven 3.8** or higher
- **PostgreSQL** (or access to a PostgreSQL instance)
- **Kafka** (running instance or cluster)
- **Discovery Server** (Eureka)

---

## 🛠️ Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/your-repo/kyc-service.git
   cd kyc-service
