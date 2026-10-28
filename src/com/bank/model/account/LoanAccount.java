package com.bank.model.account;

import com.bank.exception.BankingException;
import com.bank.exception.InvalidAmountException;
import com.bank.model.transaction.Transaction;
import com.bank.model.transaction.TransactionStatus;
import com.bank.model.transaction.TransactionType;

/**
 * Concrete Loan Account.
 * Represents customer personal / auto loans, EMI repayment tracking, and balance amortization.
 * Demonstrates OOP polymorphism: deposit() serves as loan installment repayment.
 */
public class LoanAccount extends Account {
    private static final long serialVersionUID = 1L;

    private final double initialLoanAmount;
    private final double annualInterestRate;
    private final int tenureMonths;
    private final double monthlyEmi;
    private double totalRepaid;

    public LoanAccount(String accountNumber, String customerId, double loanAmount,
                       double annualInterestRate, int tenureMonths) {
        // Balance in LoanAccount represents remaining debt (negative balance or principal owed)
        super(accountNumber, customerId, loanAmount, AccountType.LOAN);
        this.initialLoanAmount = loanAmount;
        this.annualInterestRate = annualInterestRate;
        this.tenureMonths = tenureMonths;
        this.monthlyEmi = calculateEmi(loanAmount, annualInterestRate, tenureMonths);
        this.totalRepaid = 0.0;
    }

    public static double calculateEmi(double principal, double annualRate, int months) {
        if (principal <= 0 || months <= 0) return 0.0;
        double monthlyRate = (annualRate / 100.0) / 12.0;
        if (monthlyRate == 0) return principal / months;
        double factor = Math.pow(1.0 + monthlyRate, months);
        return (principal * monthlyRate * factor) / (factor - 1.0);
    }

    public double getInitialLoanAmount() {
        return initialLoanAmount;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public double getMonthlyEmi() {
        return monthlyEmi;
    }

    public double getTotalRepaid() {
        return totalRepaid;
    }

    public boolean isFullySettled() {
        return balance <= 0.001;
    }

    @Override
    public synchronized double deposit(double amount) throws Exception {
        ensureActive();
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (isFullySettled()) {
            throw new BankingException("ERR_LOAN_ALREADY_SETTLED", "Loan is already fully repaid and settled.");
        }

        double payment = Math.min(amount, balance);
        balance -= payment;
        totalRepaid += payment;

        if (balance <= 0.001) {
            balance = 0.0;
            setActive(false); // loan closed upon full settlement
        }

        Transaction txn = new Transaction(
                "CUSTOMER_PAYMENT", getAccountNumber(), payment, 0.0,
                TransactionType.LOAN_REPAYMENT, TransactionStatus.SUCCESS,
                String.format("Loan EMI Repayment (Remaining: $%,.2f)", balance),
                balance
        );
        addTransaction(txn);

        return balance;
    }

    @Override
    public synchronized double withdraw(double amount) throws BankingException {
        throw new BankingException("ERR_LOAN_WITHDRAWAL_NOT_PERMITTED",
                "Cash withdrawal is not permitted from a Loan Account. Funds were disbursed to your deposit account.");
    }

    @Override
    public String getAccountSummary() {
        return String.format(
            "Loan Account [%s]\n" +
            "  * Customer ID: %s\n" +
            "  * Original Loan: $%,.2f\n" +
            "  * Outstanding Balance: $%,.2f\n" +
            "  * Interest Rate: %.2f%% p.a.\n" +
            "  * Tenure: %d months\n" +
            "  * Monthly EMI: $%,.2f\n" +
            "  * Total Repaid: $%,.2f\n" +
            "  * Status: %s",
            getAccountNumber(), getCustomerId(), initialLoanAmount, balance, annualInterestRate,
            tenureMonths, monthlyEmi, totalRepaid, (isFullySettled() ? "SETTLED" : (isActive() ? "ACTIVE" : "FROZEN"))
        );
    }
}
