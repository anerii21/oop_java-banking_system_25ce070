package com.bank.ui;

import com.bank.exception.BankingException;
import com.bank.model.account.*;
import com.bank.model.transaction.Transaction;
import com.bank.model.user.Admin;
import com.bank.model.user.Customer;
import com.bank.model.user.Role;
import com.bank.model.user.User;
import com.bank.service.AuthenticationService;
import com.bank.service.BankService;
import com.bank.service.LoanCalculator;
import com.bank.storage.DataStorageManager;
import com.bank.util.AnsiColor;
import com.bank.util.CurrencyFormatter;
import com.bank.util.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console UI with rich ANSI formatting and menu navigation.
 */
public class ConsoleUI {
    private final BankService bankService;
    private final AuthenticationService authService;
    private final Scanner scanner;
    private User currentUser;

    public ConsoleUI(BankService bankService, AuthenticationService authService) {
        this.bankService = bankService;
        this.authService = authService;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = handlePublicMenu();
            } else if (currentUser.getRole() == Role.CUSTOMER) {
                handleCustomerMenu((Customer) currentUser);
            } else if (currentUser.getRole() == Role.ADMIN) {
                handleAdminMenu((Admin) currentUser);
            }
        }
        System.out.println(AnsiColor.CYAN + "\nSaving bank state to disk..." + AnsiColor.RESET);
        DataStorageManager.saveState(bankService, authService);
        System.out.println(AnsiColor.GREEN + "Thank you for using OOP Java Banking System. Goodbye!" + AnsiColor.RESET);
    }

    private void printBanner() {
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + AnsiColor.YELLOW + "               ADVANCED OOP JAVA BANKING MANAGEMENT SYSTEM                      " + AnsiColor.RESET);
        System.out.println(AnsiColor.GREEN + "             Course Project / Practical ID: 25CE070 - Aneri Patel               " + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
    }

    private boolean handlePublicMenu() {
        System.out.println("\n" + AnsiColor.BOLD + "=== MAIN GATEWAY ===" + AnsiColor.RESET);
        System.out.println("  [1] Sign In to Existing Account");
        System.out.println("  [2] Register New Customer");
        System.out.println("  [3] View Demo Credentials & System Architecture");
        System.out.println("  [4] Exit Application");

        int choice = InputValidator.readInt(scanner, "Select an option (1-4): ", 1, 4);
        switch (choice) {
            case 1:
                handleLogin();
                return true;
            case 2:
                handleRegistration();
                return true;
            case 3:
                showCredentialsAndArchitecture();
                return true;
            case 4:
                return false;
            default:
                return true;
        }
    }

    private void handleLogin() {
        System.out.println("\n" + AnsiColor.BOLD + "--- SECURE USER LOGIN ---" + AnsiColor.RESET);
        String username = InputValidator.readNonEmptyString(scanner, "Enter Username: ");
        String password = InputValidator.readPinOrPassword(scanner, "Enter Password: ");

        try {
            currentUser = authService.authenticate(username, password);
            System.out.println(AnsiColor.GREEN + "\nAuthentication successful! Welcome, " + currentUser.getFullName() + "." + AnsiColor.RESET);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Login Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleRegistration() {
        System.out.println("\n" + AnsiColor.BOLD + "--- REGISTER NEW CUSTOMER ONBOARDING ---" + AnsiColor.RESET);
        String fullName = InputValidator.readNonEmptyString(scanner, "Full Legal Name: ");
        String username = InputValidator.readNonEmptyString(scanner, "Preferred Username: ");
        String password = InputValidator.readPinOrPassword(scanner, "Create Secure Password: ");
        String email = InputValidator.readNonEmptyString(scanner, "Email Address: ");
        String phone = InputValidator.readNonEmptyString(scanner, "Contact Phone: ");
        String address = InputValidator.readNonEmptyString(scanner, "Residential Address: ");

        String generatedId = "CUST-" + (1000 + authService.getAllUsers().size() + 1);
        try {
            Customer newCustomer = authService.registerNewCustomer(generatedId, username, password, fullName, email, phone, address);
            double initialDeposit = InputValidator.readPositiveDouble(scanner, "Opening Deposit for Initial Savings Account ($): ");
            bankService.openSavingsAccount(newCustomer, initialDeposit, 4.25, 100.0);
            DataStorageManager.saveState(bankService, authService);

            System.out.println(AnsiColor.GREEN + "\nRegistration successful! Your Customer ID is: " + generatedId + AnsiColor.RESET);
            System.out.println(AnsiColor.YELLOW + "You can now log in using your username and password." + AnsiColor.RESET);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Registration Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void showCredentialsAndArchitecture() {
        System.out.println("\n" + AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + AnsiColor.YELLOW + " DEMO CREDENTIALS FOR INSTANT TESTING" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);
        System.out.println("  1. Customer (Default Student Project Profile):");
        System.out.println("     * Username : " + AnsiColor.BOLD + "aneri25" + AnsiColor.RESET);
        System.out.println("     * Password : " + AnsiColor.BOLD + "password123" + AnsiColor.RESET);
        System.out.println("     * Accounts : Pre-linked Savings & Checking accounts with live balances.");
        System.out.println();
        System.out.println("  2. System Administrator:");
        System.out.println("     * Username : " + AnsiColor.BOLD + "admin" + AnsiColor.RESET);
        System.out.println("     * Password : " + AnsiColor.BOLD + "admin123" + AnsiColor.RESET);
        System.out.println("     * Powers   : Bank-wide liquidity monitor, freeze/unfreeze, audit logs, monthly batch.");
        System.out.println();
        System.out.println("  3. Second Customer (for fund transfers):");
        System.out.println("     * Username : " + AnsiColor.BOLD + "john_doe" + AnsiColor.RESET);
        System.out.println("     * Password : " + AnsiColor.BOLD + "password123" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + " OOP PRINCIPLES DEMONSTRATED IN THIS REPO:" + AnsiColor.RESET);
        System.out.println("  - " + AnsiColor.GREEN + "Encapsulation:" + AnsiColor.RESET + " Private fields, defensive copies, SHA-256 salted hashes.");
        System.out.println("  - " + AnsiColor.GREEN + "Inheritance:" + AnsiColor.RESET + " Account base class -> SavingsAccount, CheckingAccount, FixedDeposit, Loan.");
        System.out.println("  - " + AnsiColor.GREEN + "Polymorphism:" + AnsiColor.RESET + " Dynamic dispatch on withdraw(), deposit(), applyInterest().");
        System.out.println("  - " + AnsiColor.GREEN + "Abstraction:" + AnsiColor.RESET + " TransactionProcessor & InterestBearing interfaces.");
        System.out.println("  - " + AnsiColor.GREEN + "Custom Exceptions:" + AnsiColor.RESET + " Domain hierarchy handling insufficient funds, overdrafts, limits.");
        System.out.println(AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);
    }

    // ================= CUSTOMER PORTAL =================
    private void handleCustomerMenu(Customer customer) {
        System.out.println("\n" + AnsiColor.BOLD + "=== CUSTOMER DASHBOARD [" + customer.getFullName() + "] ===" + AnsiColor.RESET);
        System.out.println("  [1] View Account Balances & Portfolio Summary");
        System.out.println("  [2] Deposit Money");
        System.out.println("  [3] Withdraw Cash");
        System.out.println("  [4] Inter-Account Transfer");
        System.out.println("  [5] Open New Sub-Account (Savings, Checking, FD, Loan)");
        System.out.println("  [6] View Transaction Statement & Receipt");
        System.out.println("  [7] Loan EMI Calculator & Simulator");
        System.out.println("  [8] Security & Change Password");
        System.out.println("  [9] Sign Out");

        int choice = InputValidator.readInt(scanner, "Choose an operation (1-9): ", 1, 9);
        switch (choice) {
            case 1:
                displayCustomerAccounts(customer);
                break;
            case 2:
                handleCustomerDeposit(customer);
                break;
            case 3:
                handleCustomerWithdrawal(customer);
                break;
            case 4:
                handleCustomerTransfer(customer);
                break;
            case 5:
                handleOpenSubAccount(customer);
                break;
            case 6:
                handleViewStatements(customer);
                break;
            case 7:
                handleEmiCalculator();
                break;
            case 8:
                handleChangePassword(customer);
                break;
            case 9:
                currentUser = null;
                DataStorageManager.saveState(bankService, authService);
                System.out.println(AnsiColor.GREEN + "Logged out successfully." + AnsiColor.RESET);
                break;
        }
    }

    private void displayCustomerAccounts(Customer customer) {
        List<Account> accounts = bankService.getAccountsByCustomerId(customer.getUserId());
        System.out.println("\n" + AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + " PORTFOLIO OVERVIEW FOR " + customer.getFullName() + " (ID: " + customer.getUserId() + ")" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);

        if (accounts.isEmpty()) {
            System.out.println("  No active accounts linked to this profile.");
            return;
        }

        double totalNetWorth = 0.0;
        double totalDebt = 0.0;

        for (Account acc : accounts) {
            System.out.println(acc.getAccountSummary());
            System.out.println(AnsiColor.CYAN + "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - " + AnsiColor.RESET);
            if (acc instanceof LoanAccount) {
                totalDebt += acc.getBalance();
            } else {
                totalNetWorth += acc.getBalance();
            }
        }

        System.out.printf("  %sTotal Deposit Assets:%s %s | %sTotal Loan Debt:%s %s\n",
                AnsiColor.BOLD + AnsiColor.GREEN, AnsiColor.RESET, CurrencyFormatter.format(totalNetWorth),
                AnsiColor.BOLD + AnsiColor.RED, AnsiColor.RESET, CurrencyFormatter.format(totalDebt));
        System.out.println(AnsiColor.CYAN + "--------------------------------------------------------------------------------" + AnsiColor.RESET);
    }

    private Account selectCustomerAccount(Customer customer, String actionLabel) {
        List<Account> accounts = bankService.getAccountsByCustomerId(customer.getUserId());
        if (accounts.isEmpty()) {
            System.out.println(AnsiColor.RED + "You have no accounts available to " + actionLabel + "." + AnsiColor.RESET);
            return null;
        }
        System.out.println("\nSelect Account for " + actionLabel + ":");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.printf("  [%d] %-12s | #%-10s | Balance: %s\n",
                    (i + 1), acc.getAccountType(), acc.getAccountNumber(), CurrencyFormatter.format(acc.getBalance()));
        }
        int index = InputValidator.readInt(scanner, "Choose account (1-" + accounts.size() + "): ", 1, accounts.size());
        return accounts.get(index - 1);
    }

    private void handleCustomerDeposit(Customer customer) {
        Account acc = selectCustomerAccount(customer, "Deposit");
        if (acc == null) return;

        double amount = InputValidator.readPositiveDouble(scanner, "Enter deposit amount ($): ");
        String note = InputValidator.readNonEmptyString(scanner, "Deposit memo / description: ");

        try {
            Transaction txn = bankService.processDeposit(acc.getAccountNumber(), amount, note);
            DataStorageManager.saveState(bankService, authService);
            System.out.println(AnsiColor.GREEN + "\nDeposit processed successfully!" + AnsiColor.RESET);
            ReceiptPrinter.printReceipt(txn);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Deposit Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleCustomerWithdrawal(Customer customer) {
        Account acc = selectCustomerAccount(customer, "Withdrawal");
        if (acc == null) return;

        double amount = InputValidator.readPositiveDouble(scanner, "Enter withdrawal amount ($): ");
        String note = InputValidator.readNonEmptyString(scanner, "Withdrawal memo / reason: ");

        try {
            Transaction txn = bankService.processWithdrawal(acc.getAccountNumber(), amount, note);
            DataStorageManager.saveState(bankService, authService);
            System.out.println(AnsiColor.GREEN + "\nWithdrawal completed successfully!" + AnsiColor.RESET);
            ReceiptPrinter.printReceipt(txn);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Withdrawal Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleCustomerTransfer(Customer customer) {
        Account fromAcc = selectCustomerAccount(customer, "Transfer Source");
        if (fromAcc == null) return;

        String targetAccNum = InputValidator.readNonEmptyString(scanner, "Enter Destination Account Number (e.g. SAV-01001 or CHK-01002): ");
        double amount = InputValidator.readPositiveDouble(scanner, "Enter transfer amount ($): ");
        String note = InputValidator.readNonEmptyString(scanner, "Transfer memo / purpose: ");

        try {
            Transaction txn = bankService.processTransfer(fromAcc.getAccountNumber(), targetAccNum, amount, note);
            DataStorageManager.saveState(bankService, authService);
            System.out.println(AnsiColor.GREEN + "\nInter-account transfer processed successfully!" + AnsiColor.RESET);
            ReceiptPrinter.printReceipt(txn);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Transfer Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleOpenSubAccount(Customer customer) {
        System.out.println("\n" + AnsiColor.BOLD + "--- OPEN NEW ACCOUNT PRODUCT ---" + AnsiColor.RESET);
        System.out.println("  [1] Savings Account (4.50% APY, Min Balance $100)");
        System.out.println("  [2] Checking Account (Overdraft Protection up to $1,000)");
        System.out.println("  [3] Fixed Deposit (FD - 7.50% Guaranteed APY)");
        System.out.println("  [4] Personal / Auto Loan Application");
        System.out.println("  [5] Cancel");

        int choice = InputValidator.readInt(scanner, "Select product (1-5): ", 1, 5);
        try {
            switch (choice) {
                case 1: {
                    double deposit = InputValidator.readPositiveDouble(scanner, "Initial Deposit ($ >= 100): ");
                    SavingsAccount sa = bankService.openSavingsAccount(customer, deposit, 4.50, 100.0);
                    System.out.println(AnsiColor.GREEN + "Savings Account created successfully! Account No: " + sa.getAccountNumber() + AnsiColor.RESET);
                    break;
                }
                case 2: {
                    double deposit = InputValidator.readPositiveDouble(scanner, "Initial Deposit ($): ");
                    CheckingAccount ca = bankService.openCheckingAccount(customer, deposit, 1000.0, 25.0);
                    System.out.println(AnsiColor.GREEN + "Checking Account opened successfully! Account No: " + ca.getAccountNumber() + AnsiColor.RESET);
                    break;
                }
                case 3: {
                    double principal = InputValidator.readPositiveDouble(scanner, "Principal Investment Amount ($): ");
                    int months = InputValidator.readInt(scanner, "Tenure in months (6, 12, 24, 36): ", 6, 60);
                    FixedDepositAccount fd = bankService.openFixedDeposit(customer, principal, months, 7.50);
                    System.out.println(AnsiColor.GREEN + "Fixed Deposit activated! Account No: " + fd.getAccountNumber() + AnsiColor.RESET);
                    System.out.printf("  Expected Maturity Amount: %s\n", CurrencyFormatter.format(fd.getMaturityAmount()));
                    break;
                }
                case 4: {
                    double loanAmt = InputValidator.readPositiveDouble(scanner, "Requested Loan Amount ($): ");
                    int tenureMonths = InputValidator.readInt(scanner, "Loan Tenure in Months (6-60): ", 6, 60);
                    double rate = 9.5; // 9.5% p.a.
                    double emi = LoanCalculator.computeEmi(loanAmt, rate, tenureMonths);
                    System.out.printf(AnsiColor.YELLOW + "  Computed Monthly EMI: %s for %d months.\n" + AnsiColor.RESET, CurrencyFormatter.format(emi), tenureMonths);
                    
                    System.out.println("Select account to disburse funds to:");
                    Account disburseAcc = selectCustomerAccount(customer, "Loan Disbursement");
                    if (disburseAcc == null) return;

                    LoanAccount loan = bankService.applyAndDisburseLoan(customer, disburseAcc, loanAmt, rate, tenureMonths);
                    System.out.println(AnsiColor.GREEN + "Loan approved & disbursed! Loan Account No: " + loan.getAccountNumber() + AnsiColor.RESET);
                    break;
                }
                case 5:
                    return;
            }
            DataStorageManager.saveState(bankService, authService);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Account Creation Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleViewStatements(Customer customer) {
        Account acc = selectCustomerAccount(customer, "View Statement");
        if (acc == null) return;

        List<Transaction> txns = acc.getTransactions();
        System.out.println("\n" + AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
        System.out.printf(AnsiColor.BOLD + " TRANSACTION STATEMENT FOR ACCOUNT: %s (Type: %s)\n" + AnsiColor.RESET, acc.getAccountNumber(), acc.getAccountType());
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);

        if (txns.isEmpty()) {
            System.out.println("  No transactions recorded yet.");
        } else {
            for (Transaction txn : txns) {
                System.out.println(txn);
            }
        }
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
    }

    private void handleEmiCalculator() {
        System.out.println("\n" + AnsiColor.BOLD + "--- LOAN EMI & AMORTIZATION CALCULATOR ---" + AnsiColor.RESET);
        double principal = InputValidator.readPositiveDouble(scanner, "Enter Loan Amount ($): ");
        System.out.print(AnsiColor.CYAN + "Enter Annual Interest Rate % (e.g. 8.5): " + AnsiColor.RESET);
        double rate = Double.parseDouble(scanner.nextLine().trim());
        int months = InputValidator.readInt(scanner, "Enter Loan Tenure in Months (e.g. 12, 24, 36, 60): ", 1, 360);

        System.out.println("\n" + AnsiColor.CYAN + "----------------------------------------------------------" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + "              LOAN FINANCIAL PROJECTION                   " + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "----------------------------------------------------------" + AnsiColor.RESET);
        System.out.print(LoanCalculator.generateAmortizationPreview(principal, rate, months));
        System.out.println(AnsiColor.CYAN + "----------------------------------------------------------" + AnsiColor.RESET);
    }

    private void handleChangePassword(User user) {
        System.out.println("\n" + AnsiColor.BOLD + "--- UPDATE ACCOUNT PASSWORD ---" + AnsiColor.RESET);
        String oldPass = InputValidator.readPinOrPassword(scanner, "Enter Current Password: ");
        String newPass = InputValidator.readPinOrPassword(scanner, "Enter New Password: ");
        try {
            authService.changePassword(user, oldPass, newPass);
            DataStorageManager.saveState(bankService, authService);
            System.out.println(AnsiColor.GREEN + "Password updated securely." + AnsiColor.RESET);
        } catch (Exception e) {
            System.out.println(AnsiColor.RED + "Error: " + e.getMessage() + AnsiColor.RESET);
        }
    }

    // ================= ADMIN PORTAL =================
    private void handleAdminMenu(Admin admin) {
        System.out.println("\n" + AnsiColor.BOLD + "=== CENTRAL BANK ADMINISTRATION PORTAL [" + admin.getFullName() + "] ===" + AnsiColor.RESET);
        System.out.println("  [1] View All Accounts Across the Institution");
        System.out.println("  [2] Bank Financial Liquidity & Reserve Overview");
        System.out.println("  [3] Search Account / Customer By ID");
        System.out.println("  [4] Freeze / Unfreeze Customer Account");
        System.out.println("  [5] Run Monthly Batch Processing (Interest Accrual)");
        System.out.println("  [6] Global Banking Audit Log");
        System.out.println("  [7] Security & Change Admin Password");
        System.out.println("  [8] Sign Out");

        int choice = InputValidator.readInt(scanner, "Select admin action (1-8): ", 1, 8);
        switch (choice) {
            case 1:
                displayAllAccounts();
                break;
            case 2:
                displayLiquidityOverview();
                break;
            case 3:
                handleSearchAccount();
                break;
            case 4:
                handleFreezeToggle();
                break;
            case 5:
                handleMonthlyBatch();
                break;
            case 6:
                displayGlobalAuditLog();
                break;
            case 7:
                handleChangePassword(admin);
                break;
            case 8:
                currentUser = null;
                DataStorageManager.saveState(bankService, authService);
                System.out.println(AnsiColor.GREEN + "Admin logged out successfully." + AnsiColor.RESET);
                break;
        }
    }

    private void displayAllAccounts() {
        List<Account> accounts = bankService.getAllAccounts();
        System.out.println("\n" + AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + " ALL REGISTERED BANK ACCOUNTS (" + accounts.size() + " total)" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
        for (Account acc : accounts) {
            System.out.printf("  #%-10s | %-14s | CustID: %-10s | Bal: %12s | Status: %s\n",
                    acc.getAccountNumber(), acc.getAccountType(), acc.getCustomerId(),
                    CurrencyFormatter.format(acc.getBalance()),
                    (acc.isActive() ? AnsiColor.GREEN + "ACTIVE" + AnsiColor.RESET : AnsiColor.RED + "FROZEN" + AnsiColor.RESET));
        }
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
    }

    private void displayLiquidityOverview() {
        double liquidity = bankService.getTotalBankLiquidity();
        double loans = bankService.getTotalLoanOutstanding();
        int totalAccounts = bankService.getAllAccounts().size();
        int totalTransactions = bankService.getAllTransactions().size();

        System.out.println("\n" + AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + AnsiColor.YELLOW + "            CENTRAL BANK LIQUIDITY & METRICS              " + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
        System.out.printf("  Total Customer Deposit Liquidity : %s%s%s\n", AnsiColor.BOLD + AnsiColor.GREEN, CurrencyFormatter.format(liquidity), AnsiColor.RESET);
        System.out.printf("  Total Active Loan Assets         : %s%s%s\n", AnsiColor.BOLD + AnsiColor.YELLOW, CurrencyFormatter.format(loans), AnsiColor.RESET);
        System.out.printf("  Net Bank Balance Sheet           : %s%s%s\n", AnsiColor.BOLD, CurrencyFormatter.format(liquidity + loans), AnsiColor.RESET);
        System.out.printf("  Total Monitored Accounts         : %d\n", totalAccounts);
        System.out.printf("  Total Settled Transactions       : %d\n", totalTransactions);
        System.out.println(AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
    }

    private void handleSearchAccount() {
        String accNum = InputValidator.readNonEmptyString(scanner, "Enter Account Number to search: ");
        try {
            Account acc = bankService.getAccount(accNum);
            System.out.println("\n" + AnsiColor.GREEN + "Account Found:" + AnsiColor.RESET);
            System.out.println(acc.getAccountSummary());
        } catch (BankingException e) {
            System.out.println(AnsiColor.RED + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleFreezeToggle() {
        String accNum = InputValidator.readNonEmptyString(scanner, "Enter Account Number to toggle freeze status: ");
        try {
            Account acc = bankService.getAccount(accNum);
            boolean newStatus = !acc.isActive();
            bankService.setAccountStatus(accNum, newStatus);
            DataStorageManager.saveState(bankService, authService);
            System.out.println(AnsiColor.GREEN + "Account " + accNum + " status changed to: " + (newStatus ? "ACTIVE" : "FROZEN") + AnsiColor.RESET);
        } catch (BankingException e) {
            System.out.println(AnsiColor.RED + e.getMessage() + AnsiColor.RESET);
        }
    }

    private void handleMonthlyBatch() {
        System.out.println(AnsiColor.YELLOW + "\nExecuting monthly interest batch cycle across all active accounts..." + AnsiColor.RESET);
        double totalInterest = bankService.applyMonthlyInterestCycle();
        DataStorageManager.saveState(bankService, authService);
        System.out.printf(AnsiColor.GREEN + "Monthly cycle complete! Total interest credited: %s\n" + AnsiColor.RESET, CurrencyFormatter.format(totalInterest));
    }

    private void displayGlobalAuditLog() {
        List<Transaction> txns = bankService.getAllTransactions();
        System.out.println("\n" + AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + " GLOBAL BANK TRANSACTION AUDIT LOG (" + txns.size() + " records)" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
        for (Transaction txn : txns) {
            System.out.println(txn);
        }
        System.out.println(AnsiColor.CYAN + "================================================================================" + AnsiColor.RESET);
    }
}
