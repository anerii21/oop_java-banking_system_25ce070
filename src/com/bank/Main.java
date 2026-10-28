package com.bank;

import com.bank.storage.DataStorageManager;
import com.bank.ui.ConsoleUI;

/**
 * Application Entry Point for the OOP Java Banking Management System.
 * Project / Course ID: 25CE070 - Aneri Patel
 */
public class Main {
    public static void main(String[] args) {
        // Load persisted state or auto-seed demo data
        DataStorageManager.BankState state = DataStorageManager.loadOrCreateState();

        // Register shutdown hook to guarantee persistence upon exit or SIGINT (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataStorageManager.saveState(state.bankService, state.authService);
        }));

        // Launch Console User Interface
        ConsoleUI ui = new ConsoleUI(state.bankService, state.authService);
        ui.start();
    }
}
