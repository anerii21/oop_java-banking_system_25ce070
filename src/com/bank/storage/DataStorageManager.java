package com.bank.storage;

import com.bank.model.account.AccountType;
import com.bank.model.account.SavingsAccount;
import com.bank.model.account.CheckingAccount;
import com.bank.model.user.Admin;
import com.bank.model.user.Customer;
import com.bank.service.AuthenticationService;
import com.bank.service.BankService;
import com.bank.util.PasswordUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles binary serialization and state restoration.
 * Ensures data persistence across terminal restarts and auto-seeds sample demo data on first launch.
 */
public class DataStorageManager {
    private static final String DATA_DIR = "data";
    private static final String STATE_FILE = "data/bank_state.dat";

    public static class BankState implements Serializable {
        private static final long serialVersionUID = 1L;
        public BankService bankService;
        public AuthenticationService authService;

        public BankState(BankService bankService, AuthenticationService authService) {
            this.bankService = bankService;
            this.authService = authService;
        }
    }

    public static void saveState(BankService bankService, AuthenticationService authService) {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE))) {
                oos.writeObject(new BankState(bankService, authService));
            }
        } catch (IOException e) {
            System.err.println("Warning: Unable to persist bank data to disk: " + e.getMessage());
        }
    }

    public static BankState loadOrCreateState() {
        File file = new File(STATE_FILE);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof BankState) {
                    return (BankState) obj;
                }
            } catch (Exception e) {
                System.out.println("Notice: Could not load existing state file (will initialize fresh sample database). Reason: " + e.getMessage());
            }
        }

        // Initialize fresh state with rich seed data
        return createSeededState();
    }

    private static BankState createSeededState() {
        BankService bankService = new BankService("Antigravity Reserve Bank (ID: 25CE070)");
        AuthenticationService authService = new AuthenticationService();

        try {
            // 1. Seed Admin
            String adminSalt = PasswordUtil.generateSalt();
            String adminHash = PasswordUtil.hashPassword("admin123", adminSalt);
            Admin admin = new Admin("ADM-001", "admin", adminHash, adminSalt,
                    "Chief System Administrator", "admin@antigravitybank.com", "+1-800-555-0199",
                    "EMP-9901", "Executive IT & Compliance", 3);
            authService.registerUser(admin);

            // 2. Seed Customer: Aneri Patel (Project Student Profile: 25CE070)
            String c1Salt = PasswordUtil.generateSalt();
            String c1Hash = PasswordUtil.hashPassword("password123", c1Salt);
            Customer c1 = new Customer("25CE070", "aneri25", c1Hash, c1Salt,
                    "Aneri Patel", "aneri25ce070@student.edu", "+1-555-014-2501", "42 Innovation Way, Tech Campus");
            authService.registerUser(c1);

            // Open Savings Account for Aneri
            SavingsAccount sa1 = bankService.openSavingsAccount(c1, 15000.00, 4.50, 500.00);
            // Open Checking Account for Aneri
            CheckingAccount ca1 = bankService.openCheckingAccount(c1, 3500.00, 1500.00, 20.00);

            // 3. Seed Customer: John Doe
            String c2Salt = PasswordUtil.generateSalt();
            String c2Hash = PasswordUtil.hashPassword("password123", c2Salt);
            Customer c2 = new Customer("CUST-1002", "john_doe", c2Hash, c2Salt,
                    "John Doe", "john.doe@example.com", "+1-555-019-3321", "742 Evergreen Terrace");
            authService.registerUser(c2);

            // Open Savings Account for John Doe
            bankService.openSavingsAccount(c2, 8250.00, 4.00, 200.00);

            // Perform a sample inter-account transfer to populate transaction histories
            bankService.processTransfer(sa1.getAccountNumber(), ca1.getAccountNumber(), 500.00, "Monthly living allowance transfer");

        } catch (Exception e) {
            System.err.println("Error initializing sample seed bank data: " + e.getMessage());
        }

        saveState(bankService, authService);
        return new BankState(bankService, authService);
    }
}
