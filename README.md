# 🏦 Advanced Object-Oriented Java Banking Management System

[![Java Version](https://img.shields.io/badge/Java-11%2B%20%7C%2025%20LTS-orange.svg)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![OOP Coverage](https://img.shields.io/badge/OOP%20Principles-100%25-success.svg)]()

> **Course / Academic Practical Project**  
> **Student ID / Roll No:** `25CE070`  
> **Developer:** Aneri Patel  
> **Repository:** [https://github.com/anerii21/oop_java-banking_system_25ce070.git](https://github.com/anerii21/oop_java-banking_system_25ce070.git)

---

## 📖 Overview

This repository contains a full-featured, enterprise-grade **Banking Management System** developed in Java, demonstrating core and advanced **Object-Oriented Programming (OOP)** paradigms.

It includes dual role-based portals (**Customer Portal** and **Central Bank Admin Portal**), support for multiple account types, atomic inter-account fund transfers, loan EMI computation, fixed deposit management, cryptographic password security, automatic data persistence, and formatted ANSI terminal receipts.

---

## 🚀 Cloning & Quick Start (Terminal Guide)

Anyone can clone and run this project immediately using standard terminal commands without needing third-party libraries or Maven/Gradle configurations.

### 1. Clone the Repository
Open your terminal and run:
```bash
git clone https://github.com/anerii21/oop_java-banking_system_25ce070.git
cd oop_java-banking_system_25ce070
```

### 2. Run the Application

#### **On macOS / Linux:**
Using the included executable runner:
```bash
./run.sh
```
Or using `make`:
```bash
make run
```
Or manual compile and run:
```bash
mkdir -p bin
javac -d bin -sourcepath src $(find src -name "*.java")
java -cp bin com.bank.Main
```

#### **On Windows:**
Double-click `run.bat` or run in Command Prompt / PowerShell:
```cmd
run.bat
```

---

## 🔑 Pre-Seeded Demo Credentials

The system automatically initializes realistic demo accounts on first launch so you can log in and test immediately:

| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Customer (Student Profile)** | `aneri25` | `password123` | ID: `25CE070` (Holds linked Savings & Checking accounts) |
| **Customer (Second User)** | `john_doe` | `password123` | Ideal for testing inter-account transfers |
| **Bank Administrator** | `admin` | `admin123` | Access to bank liquidity, audit logs & account freeze |

---

## ✨ Key Features & Extra Functionalities

### 1. Multi-Account Portfolio Management
- A single customer can own and manage multiple accounts simultaneously:
  - **Savings Account:** Earns annual interest (4.25% p.a.), enforces minimum balance ($100), and monthly withdrawal transaction quotas.
  - **Checking Account:** Overdraft protection (up to $1,000) with automatic overdraft fee deduction.
  - **Fixed Deposit (FD):** Time-locked investment with guaranteed maturity yield (7.50% p.a.) and premature withdrawal penalty protection.
  - **Loan Account:** Debt tracking with monthly EMI repayment engine.

### 2. Atomic Inter-Account Fund Transfers
- Safe fund transfers between any two accounts with automatic rollback if either debit or credit fails.

### 3. Loan EMI & Amortization Calculator
- Computes Equated Monthly Installment (EMI) using the banking formula:
  $$E = P \cdot r \cdot \frac{(1+r)^n}{(1+r)^n - 1}$$
- Disburses approved loan proceeds directly into customer checking/savings accounts.

### 4. Enterprise Security & Cryptography
- Passwords and PINs are protected using **SHA-256 with 16-byte cryptographic salt** generation (`PasswordUtil`). Raw passwords are never stored in memory or disk.

### 5. Automated Data Persistence
- Binary serialization (`DataStorageManager`) ensures customer accounts, transaction histories, and balance updates persist across terminal restarts in `data/bank_state.dat`.
- Integrated JVM Shutdown Hook guarantees data saves even on abrupt termination (`Ctrl+C`).

### 6. Central Bank Administration
- Real-time liquidity tracking across customer deposits vs. outstanding loan assets.
- Compliance control: Ability to **Freeze** and **Unfreeze** suspicious accounts.
- Monthly batch runner: Accrues and credits interest across all accounts in a single batch operation.
- Global audit log tracking every transaction with UUIDs and timestamps.

### 7. Formatted Terminal Receipts
- ASCII/ANSI formatted official transaction slips with transaction ID, timestamps, fees, and closing balance snapshots.

---

## 🏛️ OOP Principles Architecture Mapping

| OOP Concept | Code Implementation | Description |
| :--- | :--- | :--- |
| **Encapsulation** | `Account`, `User`, `Transaction` | All internal fields are `private` or `protected`; accessed and mutated solely through validated getters/setters. |
| **Inheritance** | `SavingsAccount`, `CheckingAccount`, `FixedDepositAccount`, `LoanAccount` extends `Account`; `Customer`, `Admin` extends `User` | Code reuse and structured hierarchical relationships. |
| **Polymorphism** | `deposit()`, `withdraw()`, `applyInterest()`, `getAccountSummary()` | Dynamic method dispatch: each account type exhibits unique behavior for withdrawals and deposits. |
| **Abstraction** | `TransactionProcessor`, `InterestBearing`, `Account`, `User` | Interfaces and abstract classes decouple service logic from concrete financial product implementations. |
| **Exception Handling** | `BankingException` hierarchy in `com.bank.exception.*` | Typed domain exceptions (`InsufficientFundsException`, `DailyLimitExceededException`, `MinimumBalanceViolationException`, etc.). |

---

## 📁 Project Directory Structure

```text
oop_java-banking_system_25ce070/
├── README.md               # Complete project documentation & guide
├── LICENSE                 # Open-source MIT License
├── Makefile                # Build, run, test, and clean automation
├── run.sh                  # One-click execution script for macOS / Linux
├── run.bat                 # One-click execution script for Windows
├── .gitignore              # Ignores compiled class files and artifacts
├── data/                   # Persistent data directory
│   └── bank_state.dat      # Serialized bank database state
└── src/
    └── com/
        └── bank/
            ├── Main.java               # Application bootstrap & shutdown hooks
            ├── BankingSystemTest.java  # Automated verification test suite
            ├── exception/              # Custom checked domain exceptions
            │   ├── BankingException.java
            │   ├── InsufficientFundsException.java
            │   ├── AccountNotFoundException.java
            │   ├── InvalidAmountException.java
            │   ├── DailyLimitExceededException.java
            │   ├── AuthenticationException.java
            │   ├── AccountSuspendedException.java
            │   └── MinimumBalanceViolationException.java
            ├── model/
            │   ├── account/            # Account domain hierarchy
            │   │   ├── Account.java
            │   │   ├── AccountType.java
            │   │   ├── SavingsAccount.java
            │   │   ├── CheckingAccount.java
            │   │   ├── FixedDepositAccount.java
            │   │   ├── LoanAccount.java
            │   │   └── InterestBearing.java
            │   ├── transaction/        # Transaction audit records
            │   │   ├── Transaction.java
            │   │   ├── TransactionType.java
            │   │   └── TransactionStatus.java
            │   └── user/               # User profiles and authentication
            │       ├── User.java
            │       ├── Customer.java
            │       ├── Admin.java
            │       └── Role.java
            ├── service/                # Business services & processors
            │   ├── BankService.java
            │   ├── AuthenticationService.java
            │   ├── TransactionProcessor.java
            │   └── LoanCalculator.java
            ├── storage/                # Persistence & seed database manager
            │   └── DataStorageManager.java
            ├── ui/                     # Interactive CLI presentation
            │   ├── ConsoleUI.java
            │   └── ReceiptPrinter.java
            └── util/                   # Formatting & security helpers
                ├── AnsiColor.java
                ├── PasswordUtil.java
                ├── CurrencyFormatter.java
                └── InputValidator.java
```

---

## 🧪 Automated Testing

To run the automated verification test suite:

```bash
make test
```
Or directly:
```bash
java -cp bin com.bank.BankingSystemTest
```

**Output:**
```text
Running automated tests for OOP Java Banking System (25CE070)...
  [PASS] Test 1: Savings minimum balance enforcement
  [PASS] Test 2: Checking account overdraft limit & fees
  [PASS] Test 3: Atomic fund transfers between accounts
  [PASS] Test 4: Fixed deposit maturity calculation
  [PASS] Test 5: Loan amortization & EMI formula

Test Results: 5/5 passed successfully! All tests verified.
```

---

## 🤝 Contributing & License

Distributed under the MIT License. See [LICENSE](LICENSE) for more details.  
Developed by **Aneri Patel (`25CE070`)**.
