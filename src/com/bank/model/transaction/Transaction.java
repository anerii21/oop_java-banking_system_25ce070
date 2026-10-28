package com.bank.model.transaction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Encapsulates immutable record of a financial transaction.
 * Supports balance auditing and mini-statement receipts.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String transactionId;
    private final String sourceAccountNumber;
    private final String targetAccountNumber;
    private final double amount;
    private final double fee;
    private final TransactionType type;
    private final TransactionStatus status;
    private final LocalDateTime timestamp;
    private final String description;
    private final double balanceAfter;

    public Transaction(String sourceAccountNumber, String targetAccountNumber, double amount,
                       double fee, TransactionType type, TransactionStatus status,
                       String description, double balanceAfter) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.sourceAccountNumber = sourceAccountNumber;
        this.targetAccountNumber = targetAccountNumber;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.description = description;
        this.balanceAfter = balanceAfter;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() {
        return fee;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    public String getDescription() {
        return description;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Type: %-15s | Amt: $%9.2f | Fee: $%5.2f | Post-Bal: $%9.2f | %s",
                getFormattedTimestamp(), transactionId, type, amount, fee, balanceAfter, description);
    }
}
