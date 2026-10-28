package com.bank.service;

import com.bank.exception.*;
import com.bank.model.account.*;
import com.bank.model.transaction.Transaction;
import com.bank.model.transaction.TransactionStatus;
import com.bank.model.transaction.TransactionType;
import com.bank.model.user.Customer;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core banking service implementing TransactionProcessor and domain logic.
 * Demonstrates:
 *  - Design pattern: Service facade & Transaction management
 *  - Atomic fund transfers with rollback on failure
 *  - Encapsulated account lifecycle
 */
public class BankService implements TransactionProcessor, Serializable {
    private static final long serialVersionUID = 1L;

    private final String bankName;
    private final Map<String, Account> accounts;
    private final List<Transaction> allTransactions;
    private final AtomicInteger accountSequence;

    public BankService(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
        this.allTransactions = new ArrayList<>();
        this.accountSequence = new AtomicInteger(1000);
    }

    public String getBankName() {
        return bankName;
    }

    public synchronized String generateAccountNumber(AccountType type) {
        String prefix;
        switch (type) {
            case SAVINGS: prefix = "SAV"; break;
            case CHECKING: prefix = "CHK"; break;
            case FIXED_DEPOSIT: prefix = "FD"; break;
            case LOAN: prefix = "LN"; break;
            default: prefix = "ACC";
        }
        return String.format("%s-%05d", prefix, accountSequence.incrementAndGet());
    }

    public synchronized void registerAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public synchronized Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public synchronized List<Account> getAccountsByCustomerId(String customerId) {
        List<Account> customerAccounts = new ArrayList<>();
        for (Account acc : accounts.values()) {
            if (acc.getCustomerId().equalsIgnoreCase(customerId)) {
                customerAccounts.add(acc);
            }
        }
        return customerAccounts;
    }

    public synchronized List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized List<Transaction> getAllTransactions() {
        return new ArrayList<>(allTransactions);
    }

    // Account Creation Methods
    public synchronized SavingsAccount openSavingsAccount(Customer customer, double initialDeposit, double interestRate, double minBalance)
            throws InvalidAmountException {
        if (initialDeposit < minBalance) {
            throw new InvalidAmountException(String.format("Initial deposit ($%.2f) must be at least the minimum balance ($%.2f).", initialDeposit, minBalance));
        }
        String accNum = generateAccountNumber(AccountType.SAVINGS);
        SavingsAccount acc = new SavingsAccount(accNum, customer.getUserId(), initialDeposit, interestRate, minBalance);
        
        Transaction openingTxn = new Transaction(
                "SYSTEM_DEPOSIT", accNum, initialDeposit, 0.0,
                TransactionType.DEPOSIT, TransactionStatus.SUCCESS,
                "Initial Account Opening Deposit", initialDeposit
        );
        acc.addTransaction(openingTxn);
        allTransactions.add(openingTxn);

        registerAccount(acc);
        customer.linkAccount(accNum);
        return acc;
    }

    public synchronized CheckingAccount openCheckingAccount(Customer customer, double initialDeposit, double overdraftLimit, double overdraftFee)
            throws InvalidAmountException {
        if (initialDeposit < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative.");
        }
        String accNum = generateAccountNumber(AccountType.CHECKING);
        CheckingAccount acc = new CheckingAccount(accNum, customer.getUserId(), initialDeposit, overdraftLimit, overdraftFee);
        
        if (initialDeposit > 0) {
            Transaction openingTxn = new Transaction(
                    "SYSTEM_DEPOSIT", accNum, initialDeposit, 0.0,
                    TransactionType.DEPOSIT, TransactionStatus.SUCCESS,
                    "Initial Account Opening Deposit", initialDeposit
            );
            acc.addTransaction(openingTxn);
            allTransactions.add(openingTxn);
        }

        registerAccount(acc);
        customer.linkAccount(accNum);
        return acc;
    }

    public synchronized FixedDepositAccount openFixedDeposit(Customer customer, double principal, int tenureMonths, double interestRate)
            throws InvalidAmountException {
        if (principal <= 0) {
            throw new InvalidAmountException(principal);
        }
        String accNum = generateAccountNumber(AccountType.FIXED_DEPOSIT);
        FixedDepositAccount fd = new FixedDepositAccount(accNum, customer.getUserId(), principal, tenureMonths, interestRate);

        Transaction txn = new Transaction(
                "FUNDING_SOURCE", accNum, principal, 0.0,
                TransactionType.FD_CREATION, TransactionStatus.SUCCESS,
                String.format("Fixed Deposit Placed: %d Months @ %.2f%% p.a.", tenureMonths, interestRate), principal
        );
        fd.addTransaction(txn);
        allTransactions.add(txn);

        registerAccount(fd);
        customer.linkAccount(accNum);
        return fd;
    }

    public synchronized LoanAccount applyAndDisburseLoan(Customer customer, Account targetDisbursementAccount,
                                                        double loanAmount, double annualRate, int tenureMonths)
            throws Exception {
        if (loanAmount <= 0) {
            throw new InvalidAmountException(loanAmount);
        }
        if (targetDisbursementAccount == null || !targetDisbursementAccount.isActive()) {
            throw new BankingException("ERR_INVALID_DISBURSE_ACCOUNT", "Valid active account required to disburse loan funds.");
        }

        String loanAccNum = generateAccountNumber(AccountType.LOAN);
        LoanAccount loan = new LoanAccount(loanAccNum, customer.getUserId(), loanAmount, annualRate, tenureMonths);

        // Disburse funds directly into target account
        targetDisbursementAccount.deposit(loanAmount);

        Transaction disbTxn = new Transaction(
                loanAccNum, targetDisbursementAccount.getAccountNumber(), loanAmount, 0.0,
                TransactionType.LOAN_DISBURSEMENT, TransactionStatus.SUCCESS,
                String.format("Disbursed Loan funds (%d months @ %.2f%%) into account %s", tenureMonths, annualRate, targetDisbursementAccount.getAccountNumber()),
                targetDisbursementAccount.getBalance()
        );
        loan.addTransaction(disbTxn);
        targetDisbursementAccount.addTransaction(disbTxn);
        allTransactions.add(disbTxn);

        registerAccount(loan);
        customer.linkAccount(loanAccNum);
        return loan;
    }

    // TransactionProcessor Implementations
    @Override
    public synchronized Transaction processDeposit(String accountNumber, double amount, String description) throws Exception {
        Account account = getAccount(accountNumber);
        double newBal = account.deposit(amount);
        Transaction txn = new Transaction(
                "EXTERNAL_DEPOSIT", accountNumber, amount, 0.0,
                TransactionType.DEPOSIT, TransactionStatus.SUCCESS,
                (description == null || description.isEmpty()) ? "Cash/Check Deposit" : description,
                newBal
        );
        account.addTransaction(txn);
        allTransactions.add(txn);
        return txn;
    }

    @Override
    public synchronized Transaction processWithdrawal(String accountNumber, double amount, String description) throws Exception {
        Account account = getAccount(accountNumber);
        double newBal = account.withdraw(amount);
        Transaction txn = new Transaction(
                accountNumber, "CASH_WITHDRAWAL", amount, 0.0,
                TransactionType.WITHDRAWAL, TransactionStatus.SUCCESS,
                (description == null || description.isEmpty()) ? "Cash Withdrawal via ATM/Branch" : description,
                newBal
        );
        account.addTransaction(txn);
        allTransactions.add(txn);
        return txn;
    }

    @Override
    public synchronized Transaction processTransfer(String fromAccountNumber, String toAccountNumber, double amount, String description)
            throws Exception {
        if (fromAccountNumber.equalsIgnoreCase(toAccountNumber)) {
            throw new BankingException("ERR_SAME_ACCOUNT", "Source and destination accounts must be different.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account source = getAccount(fromAccountNumber);
        Account target = getAccount(toAccountNumber);

        // Atomic transfer: withdraw from source first
        double sourceBalAfter = source.withdraw(amount);
        double targetBalAfter;
        try {
            targetBalAfter = target.deposit(amount);
        } catch (Exception e) {
            // Rollback withdrawal if deposit failed
            source.deposit(amount);
            throw new BankingException("ERR_TRANSFER_FAILED", "Transfer failed and funds were restored: " + e.getMessage(), e);
        }

        String note = (description == null || description.isEmpty()) ? "Fund Transfer" : description;
        Transaction outTxn = new Transaction(
                fromAccountNumber, toAccountNumber, amount, 0.0,
                TransactionType.TRANSFER_OUT, TransactionStatus.SUCCESS,
                note + " to " + toAccountNumber, sourceBalAfter
        );
        Transaction inTxn = new Transaction(
                fromAccountNumber, toAccountNumber, amount, 0.0,
                TransactionType.TRANSFER_IN, TransactionStatus.SUCCESS,
                note + " from " + fromAccountNumber, targetBalAfter
        );

        source.addTransaction(outTxn);
        target.addTransaction(inTxn);
        allTransactions.add(outTxn);

        return outTxn;
    }

    // Administrative Operations
    public synchronized void setAccountStatus(String accountNumber, boolean active) throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.setActive(active);
    }

    public synchronized double applyMonthlyInterestCycle() {
        double totalInterestDistributed = 0.0;
        for (Account acc : accounts.values()) {
            if (acc instanceof InterestBearing && acc.isActive()) {
                InterestBearing ib = (InterestBearing) acc;
                double interest = ib.applyInterest();
                totalInterestDistributed += interest;
            }
        }
        return totalInterestDistributed;
    }

    public synchronized double getTotalBankLiquidity() {
        double sum = 0.0;
        for (Account acc : accounts.values()) {
            if (acc.getAccountType() != AccountType.LOAN) {
                sum += acc.getBalance();
            }
        }
        return sum;
    }

    public synchronized double getTotalLoanOutstanding() {
        double sum = 0.0;
        for (Account acc : accounts.values()) {
            if (acc instanceof LoanAccount) {
                sum += acc.getBalance();
            }
        }
        return sum;
    }
}
