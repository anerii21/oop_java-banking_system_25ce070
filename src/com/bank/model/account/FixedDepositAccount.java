package com.bank.model.account;

import com.bank.exception.BankingException;
import com.bank.exception.InvalidAmountException;
import com.bank.model.transaction.Transaction;
import com.bank.model.transaction.TransactionStatus;
import com.bank.model.transaction.TransactionType;

import java.time.LocalDate;

/**
 * Concrete Fixed Deposit (FD) Account.
 * Demonstrates:
 *  - Inheritance from Account
 *  - Implementation of InterestBearing
 *  - Time-locked financial contracts with maturity dates and premature liquidation penalties.
 */
public class FixedDepositAccount extends Account implements InterestBearing {
    private static final long serialVersionUID = 1L;

    private final double principalAmount;
    private final int tenureMonths;
    private final LocalDate depositDate;
    private final LocalDate maturityDate;
    private double interestRate; // Annual % e.g. 7.5
    private double penaltyRate;  // Penalty on premature withdrawal e.g. 1.5%
    private boolean liquidated;

    public FixedDepositAccount(String accountNumber, String customerId, double principalAmount,
                               int tenureMonths, double interestRate) {
        super(accountNumber, customerId, principalAmount, AccountType.FIXED_DEPOSIT);
        this.principalAmount = principalAmount;
        this.tenureMonths = tenureMonths;
        this.interestRate = interestRate;
        this.penaltyRate = 1.5;
        this.depositDate = LocalDate.now();
        this.maturityDate = depositDate.plusMonths(tenureMonths);
        this.liquidated = false;
    }

    public double getPrincipalAmount() {
        return principalAmount;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public boolean isLiquidated() {
        return liquidated;
    }

    public boolean isMatured() {
        return !LocalDate.now().isBefore(maturityDate);
    }

    public double getMaturityAmount() {
        // Simple compound or periodic formula: Principal * (1 + (r * t / 1200))
        double interest = principalAmount * (interestRate / 100.0) * (tenureMonths / 12.0);
        return principalAmount + interest;
    }

    @Override
    public synchronized double deposit(double amount) throws BankingException {
        throw new BankingException("ERR_FD_LOCKED", 
            "Fixed Deposit accounts are locked contracts and do not allow ad-hoc deposits. Please open a new FD account.");
    }

    @Override
    public synchronized double withdraw(double amount) throws Exception {
        ensureActive();
        if (liquidated) {
            throw new BankingException("ERR_FD_ALREADY_LIQUIDATED", "This Fixed Deposit has already been liquidated.");
        }

        boolean mature = isMatured();
        double payout;
        double penalty = 0.0;

        if (mature) {
            payout = getMaturityAmount();
        } else {
            // Premature liquidation: principal + prorated interest minus penalty
            double proratedInterest = principalAmount * (interestRate / 100.0) * (Math.max(1, tenureMonths / 2.0) / 12.0);
            penalty = principalAmount * (penaltyRate / 100.0);
            payout = principalAmount + proratedInterest - penalty;
        }

        this.balance = 0.0;
        this.liquidated = true;
        this.setActive(false);

        Transaction txn = new Transaction(
                getAccountNumber(), "CUSTOMER_PAYOUT", payout, penalty,
                TransactionType.FD_LIQUIDATION, TransactionStatus.SUCCESS,
                mature ? "Maturity liquidation payout" : "Premature FD liquidation with penalty deduction",
                0.0
        );
        addTransaction(txn);

        return payout;
    }

    @Override
    public double calculateInterest() {
        return (principalAmount * (interestRate / 100.0)) / 12.0;
    }

    @Override
    public synchronized double applyInterest() {
        if (!isLiquidated()) {
            double monthlyInterest = calculateInterest();
            balance += monthlyInterest;
            return monthlyInterest;
        }
        return 0.0;
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public String getAccountSummary() {
        return String.format(
            "Fixed Deposit Account [%s]\n" +
            "  * Customer ID: %s\n" +
            "  * Principal: $%,.2f\n" +
            "  * Current Value: $%,.2f\n" +
            "  * Interest Rate: %.2f%% p.a.\n" +
            "  * Tenure: %d months\n" +
            "  * Deposit Date: %s | Maturity Date: %s\n" +
            "  * Expected Maturity Amount: $%,.2f\n" +
            "  * Status: %s",
            getAccountNumber(), getCustomerId(), principalAmount, balance, interestRate,
            tenureMonths, depositDate, maturityDate, getMaturityAmount(),
            liquidated ? "LIQUIDATED" : (isMatured() ? "MATURED" : "LOCKED")
        );
    }
}
