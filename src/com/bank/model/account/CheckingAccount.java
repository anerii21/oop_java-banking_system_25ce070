package com.bank.model.account;

import com.bank.exception.BankingException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;
import com.bank.model.transaction.Transaction;
import com.bank.model.transaction.TransactionStatus;
import com.bank.model.transaction.TransactionType;

/**
 * Concrete Checking Account.
 * Supports overdraft protection and overdraft transaction fees.
 * Demonstrates OOP polymorphism by overriding withdraw() with overdraft handling.
 */
public class CheckingAccount extends Account {
    private static final long serialVersionUID = 1L;

    private double overdraftLimit;
    private double overdraftFee;

    public CheckingAccount(String accountNumber, String customerId, double initialDeposit, double overdraftLimit, double overdraftFee) {
        super(accountNumber, customerId, initialDeposit, AccountType.CHECKING);
        this.overdraftLimit = overdraftLimit;
        this.overdraftFee = overdraftFee;
    }

    public CheckingAccount(String accountNumber, String customerId, double initialDeposit) {
        this(accountNumber, customerId, initialDeposit, 1000.00, 25.00);
    }

    @Override
    public synchronized double deposit(double amount) throws InvalidAmountException, BankingException {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
        return balance;
    }

    @Override
    public synchronized double withdraw(double amount) throws Exception {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        validateDailyLimit(amount);

        double totalAvailable = balance + overdraftLimit;
        if (amount > totalAvailable) {
            throw new InsufficientFundsException(totalAvailable, amount);
        }

        boolean willIncurOverdraft = (balance - amount < 0);
        balance -= amount;

        if (willIncurOverdraft) {
            balance -= overdraftFee;
            Transaction feeTxn = new Transaction(
                    getAccountNumber(), "BANK_FEE", overdraftFee, 0.0,
                    TransactionType.FEE_DEBIT, TransactionStatus.SUCCESS,
                    "Overdraft protection fee applied", balance
            );
            addTransaction(feeTxn);
        }

        recordDailyWithdrawal(amount);
        return balance;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftFee() {
        return overdraftFee;
    }

    public void setOverdraftFee(double overdraftFee) {
        this.overdraftFee = overdraftFee;
    }

    public double getAvailableFunds() {
        return Math.max(0.0, balance + overdraftLimit);
    }

    @Override
    public String getAccountSummary() {
        return String.format(
            "Checking Account [%s]\n" +
            "  * Customer ID: %s\n" +
            "  * Current Balance: $%,.2f\n" +
            "  * Overdraft Limit: $%,.2f\n" +
            "  * Overdraft Fee: $%,.2f\n" +
            "  * Total Available Funds: $%,.2f\n" +
            "  * Status: %s",
            getAccountNumber(), getCustomerId(), balance, overdraftLimit, overdraftFee,
            getAvailableFunds(), (isActive() ? "ACTIVE" : "FROZEN")
        );
    }
}
