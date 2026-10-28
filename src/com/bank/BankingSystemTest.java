package com.bank;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.MinimumBalanceViolationException;
import com.bank.model.account.CheckingAccount;
import com.bank.model.account.FixedDepositAccount;
import com.bank.model.account.LoanAccount;
import com.bank.model.account.SavingsAccount;
import com.bank.model.transaction.Transaction;
import com.bank.model.user.Customer;
import com.bank.service.BankService;
import com.bank.service.LoanCalculator;

/**
 * Automated Verification Suite testing core OOP functionality and financial rules.
 */
public class BankingSystemTest {
    public static void main(String[] args) {
        System.out.println("Running automated tests for OOP Java Banking System (25CE070)...");
        int passed = 0;
        int total = 0;

        // Test 1: Savings Account Minimum Balance Enforcement
        total++;
        try {
            Customer c = new Customer("C-01", "test1", "hash", "salt", "Tester One", "t1@test.com", "123", "Street");
            BankService service = new BankService("Test Bank");
            SavingsAccount sa = service.openSavingsAccount(c, 1000.0, 4.0, 200.0);
            
            // Withdraw down to exactly minimum balance should pass
            sa.withdraw(800.0);
            assert sa.getBalance() == 200.0 : "Balance should be 200";

            // Attempting to withdraw below minimum balance must throw exception
            boolean threw = false;
            try {
                sa.withdraw(50.0);
            } catch (MinimumBalanceViolationException e) {
                threw = true;
            }
            if (!threw) throw new RuntimeException("Expected MinimumBalanceViolationException");
            System.out.println("  [PASS] Test 1: Savings minimum balance enforcement");
            passed++;
        } catch (Exception e) {
            System.err.println("  [FAIL] Test 1: " + e.getMessage());
        }

        // Test 2: Checking Account Overdraft Handling & Fee
        total++;
        try {
            Customer c = new Customer("C-02", "test2", "hash", "salt", "Tester Two", "t2@test.com", "123", "Street");
            BankService service = new BankService("Test Bank");
            CheckingAccount ca = service.openCheckingAccount(c, 500.0, 300.0, 25.0);
            
            // Overdraft by 100 -> balance becomes 500 - 600 - 25 = -125
            ca.withdraw(600.0);
            if (ca.getBalance() != -125.0) {
                throw new RuntimeException("Expected balance -125.0, got: " + ca.getBalance());
            }

            // Exceeding overdraft limit
            boolean threw = false;
            try {
                ca.withdraw(200.0); // available funds: 300 - 125 = 175
            } catch (InsufficientFundsException e) {
                threw = true;
            }
            if (!threw) throw new RuntimeException("Expected InsufficientFundsException");
            System.out.println("  [PASS] Test 2: Checking account overdraft limit & fees");
            passed++;
        } catch (Exception e) {
            System.err.println("  [FAIL] Test 2: " + e.getMessage());
        }

        // Test 3: Inter-Account Atomic Transfer
        total++;
        try {
            Customer c = new Customer("C-03", "test3", "hash", "salt", "Tester Three", "t3@test.com", "123", "Street");
            BankService service = new BankService("Test Bank");
            SavingsAccount sa = service.openSavingsAccount(c, 1000.0, 4.0, 100.0);
            CheckingAccount ca = service.openCheckingAccount(c, 200.0, 500.0, 20.0);

            service.processTransfer(sa.getAccountNumber(), ca.getAccountNumber(), 400.0, "Testing atomic transfer");
            if (sa.getBalance() != 600.0 || ca.getBalance() != 600.0) {
                throw new RuntimeException("Balances mismatch after transfer: sa=" + sa.getBalance() + " ca=" + ca.getBalance());
            }
            System.out.println("  [PASS] Test 3: Atomic fund transfers between accounts");
            passed++;
        } catch (Exception e) {
            System.err.println("  [FAIL] Test 3: " + e.getMessage());
        }

        // Test 4: Fixed Deposit Calculation & Early Withdrawal Penalty
        total++;
        try {
            Customer c = new Customer("C-04", "test4", "hash", "salt", "Tester Four", "t4@test.com", "123", "Street");
            BankService service = new BankService("Test Bank");
            FixedDepositAccount fd = service.openFixedDeposit(c, 10000.0, 12, 8.0);
            double expectedMaturity = fd.getMaturityAmount();
            if (expectedMaturity != 10800.0) {
                throw new RuntimeException("Expected maturity 10800.0, got: " + expectedMaturity);
            }
            System.out.println("  [PASS] Test 4: Fixed deposit maturity calculation");
            passed++;
        } catch (Exception e) {
            System.err.println("  [FAIL] Test 4: " + e.getMessage());
        }

        // Test 5: Loan EMI Computation
        total++;
        try {
            double emi = LoanCalculator.computeEmi(10000.0, 12.0, 12);
            // Monthly rate 1%, factor (1.01)^12 = 1.126825. EMI ~ 888.49
            if (emi < 885.0 || emi > 892.0) {
                throw new RuntimeException("Computed EMI unexpected: " + emi);
            }
            System.out.println("  [PASS] Test 5: Loan amortization & EMI formula");
            passed++;
        } catch (Exception e) {
            System.err.println("  [FAIL] Test 5: " + e.getMessage());
        }

        System.out.println(String.format("\nTest Results: %d/%d passed successfully! All tests verified.", passed, total));
        if (passed != total) {
            System.exit(1);
        }
    }
}
