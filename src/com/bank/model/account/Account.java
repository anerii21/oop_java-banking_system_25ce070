package com.bank.model.account;

import com.bank.exception.AccountSuspendedException;
import com.bank.exception.DailyLimitExceededException;
import com.bank.exception.InvalidAmountException;
import com.bank.model.transaction.Transaction;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract Base Class representing a financial account.
 * Demonstrates:
 *  - Encapsulation: private/protected state with validated mutators.
 *  - Abstraction: abstract methods enforcing polymorphic behavior across account types.
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final String customerId;
    protected double balance;
    private final AccountType accountType;
    private final LocalDateTime createdAt;
    private boolean active;

    // Daily limit tracking
    protected double dailyWithdrawalLimit;
    protected double dailyWithdrawnAmount;
    protected LocalDate lastWithdrawalDate;

    // Transaction audit history
    private final List<Transaction> transactions;

    public Account(String accountNumber, String customerId, double initialDeposit, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialDeposit;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.dailyWithdrawalLimit = 5000.00;
        this.dailyWithdrawnAmount = 0.0;
        this.lastWithdrawalDate = LocalDate.now();
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void setDailyWithdrawalLimit(double dailyWithdrawalLimit) {
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
    }

    public double getDailyWithdrawnAmount() {
        resetDailyLimitIfNeeded();
        return dailyWithdrawnAmount;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public synchronized void addTransaction(Transaction txn) {
        transactions.add(txn);
    }

    /**
     * Checks if current day has rolled over to reset daily withdrawal counter.
     */
    protected synchronized void resetDailyLimitIfNeeded() {
        LocalDate today = LocalDate.now();
        if (lastWithdrawalDate == null || !lastWithdrawalDate.equals(today)) {
            this.dailyWithdrawnAmount = 0.0;
            this.lastWithdrawalDate = today;
        }
    }

    /**
     * Validates that requested withdrawal amount does not exceed remaining daily limit.
     */
    protected synchronized void validateDailyLimit(double amount) throws DailyLimitExceededException {
        resetDailyLimitIfNeeded();
        if (dailyWithdrawnAmount + amount > dailyWithdrawalLimit) {
            double remaining = Math.max(0.0, dailyWithdrawalLimit - dailyWithdrawnAmount);
            throw new DailyLimitExceededException(amount, remaining);
        }
    }

    /**
     * Records withdrawal against daily quota.
     */
    protected synchronized void recordDailyWithdrawal(double amount) {
        resetDailyLimitIfNeeded();
        dailyWithdrawnAmount += amount;
    }

    /**
     * Validates account is active before performing financial operations.
     */
    protected void ensureActive() throws AccountSuspendedException {
        if (!active) {
            throw new AccountSuspendedException(accountNumber);
        }
    }

    /**
     * Polymorphic method to deposit funds into the account.
     * @param amount positive money amount to deposit
     * @return new balance after deposit
     */
    public abstract double deposit(double amount) throws Exception;

    /**
     * Polymorphic method to withdraw funds from the account.
     * @param amount positive money amount to withdraw
     * @return new balance after withdrawal
     */
    public abstract double withdraw(double amount) throws Exception;

    /**
     * Generates a descriptive string representation of account details.
     */
    public abstract String getAccountSummary();

    @Override
    public String toString() {
        return String.format("[%s] #%s | Bal: $%,.2f | Status: %s",
                accountType, accountNumber, balance, (active ? "ACTIVE" : "FROZEN"));
    }
}
