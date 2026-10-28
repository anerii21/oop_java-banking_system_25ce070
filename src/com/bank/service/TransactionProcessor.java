package com.bank.service;

import com.bank.exception.BankingException;
import com.bank.model.transaction.Transaction;

/**
 * Interface defining financial operation contracts.
 * Demonstrates Abstraction & Interface-driven design.
 */
public interface TransactionProcessor {
    Transaction processDeposit(String accountNumber, double amount, String description) throws Exception;
    Transaction processWithdrawal(String accountNumber, double amount, String description) throws Exception;
    Transaction processTransfer(String fromAccountNumber, String toAccountNumber, double amount, String description) throws Exception;
}
