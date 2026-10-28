package com.bank.model.account;

import com.bank.exception.BankingException;
import com.bank.exception.InvalidAmountException;
import com.bank.exception.MinimumBalanceViolationException;
import com.bank.model.transaction.Transaction;
import com.bank.model.transaction.TransactionStatus;
import com.bank.model.transaction.TransactionType;

/**
 * Concrete Savings Account.
 * Demonstrates:
 *  - Inheritance: inherits from Account.
 *  - Interface realization: implements InterestBearing.
 *  - Polymorphism: overrides deposit(), withdraw(), and applies interest.
 */
public class SavingsAccount extends Account implements InterestBearing {
    private static final long serialVersionUID = 1L;

    private double interestRate; // Annual % e.g. 4.25
    private double minimumBalance; // e.g. 100.00
    private int monthlyWithdrawalLimit; // e.g. 6 free withdrawals
    private int monthlyWithdrawalCount;

    public SavingsAccount(String accountNumber, String customerId, double initialDeposit, double interestRate, double minimumBalance) {
        super(accountNumber, customerId, initialDeposit, AccountType.SAVINGS);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
        this.monthlyWithdrawalLimit = 6;
        this.monthlyWithdrawalCount = 0;
    }

    public SavingsAccount(String accountNumber, String customerId, double initialDeposit) {
        this(accountNumber, customerId, initialDeposit, 4.25, 100.00);
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

        if (monthlyWithdrawalCount >= monthlyWithdrawalLimit) {
            throw new BankingException("ERR_MONTHLY_LIMIT", 
                String.format("Monthly withdrawal limit of %d has been reached for this Savings Account.", monthlyWithdrawalLimit));
        }

        if (balance - amount < minimumBalance) {
            throw new MinimumBalanceViolationException(amount, balance, minimumBalance);
        }

        balance -= amount;
        recordDailyWithdrawal(amount);
        monthlyWithdrawalCount++;
        return balance;
    }

    @Override
    public double calculateInterest() {
        // Monthly interest = Balance * (annualRate / 100) / 12
        return (balance * (interestRate / 100.0)) / 12.0;
    }

    @Override
    public synchronized double applyInterest() {
        double interest = calculateInterest();
        if (interest > 0 && isActive()) {
            balance += interest;
            Transaction txn = new Transaction(
                    "BANK_INTEREST", getAccountNumber(), interest, 0.0,
                    TransactionType.INTEREST_CREDIT, TransactionStatus.SUCCESS,
                    String.format("Monthly interest credit (%.2f%% p.a.)", interestRate),
                    balance
            );
            addTransaction(txn);
        }
        return interest;
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public int getMonthlyWithdrawalCount() {
        return monthlyWithdrawalCount;
    }

    public void resetMonthlyWithdrawalCount() {
        this.monthlyWithdrawalCount = 0;
    }

    @Override
    public String getAccountSummary() {
        return String.format(
            "Savings Account [%s]\n" +
            "  * Customer ID: %s\n" +
            "  * Balance: $%,.2f\n" +
            "  * Interest Rate: %.2f%% p.a.\n" +
            "  * Minimum Balance Required: $%,.2f\n" +
            "  * Monthly Withdrawals: %d / %d\n" +
            "  * Status: %s",
            getAccountNumber(), getCustomerId(), balance, interestRate, minimumBalance,
            monthlyWithdrawalCount, monthlyWithdrawalLimit, (isActive() ? "ACTIVE" : "FROZEN")
        );
    }
}
