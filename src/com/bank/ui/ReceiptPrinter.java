package com.bank.ui;

import com.bank.model.transaction.Transaction;
import com.bank.util.AnsiColor;
import com.bank.util.CurrencyFormatter;

/**
 * Generates formatted terminal receipts for banking transactions.
 */
public final class ReceiptPrinter {

    private ReceiptPrinter() {}

    public static void printReceipt(Transaction txn) {
        System.out.println(AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + AnsiColor.YELLOW + "                OFFICIAL BANK TRANSACTION RECEIPT         " + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
        System.out.printf("  Transaction ID : %s%s%s\n", AnsiColor.BOLD, txn.getTransactionId(), AnsiColor.RESET);
        System.out.printf("  Date & Time    : %s\n", txn.getFormattedTimestamp());
        System.out.printf("  Type           : %s%s%s\n", AnsiColor.GREEN, txn.getType().getDescription(), AnsiColor.RESET);
        System.out.printf("  Status         : %s%s%s\n", AnsiColor.BOLD, txn.getStatus(), AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "----------------------------------------------------------" + AnsiColor.RESET);
        System.out.printf("  Source Account : %s\n", txn.getSourceAccountNumber());
        if (txn.getTargetAccountNumber() != null && !txn.getTargetAccountNumber().isEmpty()) {
            System.out.printf("  Target Account : %s\n", txn.getTargetAccountNumber());
        }
        System.out.printf("  Amount         : %s%s%s\n", AnsiColor.BOLD + AnsiColor.GREEN, CurrencyFormatter.format(txn.getAmount()), AnsiColor.RESET);
        if (txn.getFee() > 0.0) {
            System.out.printf("  Processing Fee : %s%s%s\n", AnsiColor.RED, CurrencyFormatter.format(txn.getFee()), AnsiColor.RESET);
        }
        System.out.printf("  Closing Balance: %s%s%s\n", AnsiColor.BOLD, CurrencyFormatter.format(txn.getBalanceAfter()), AnsiColor.RESET);
        System.out.printf("  Description    : %s\n", txn.getDescription());
        System.out.println(AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
        System.out.println("         Thank you for banking with Antigravity Bank!     ");
        System.out.println(AnsiColor.CYAN + "==========================================================" + AnsiColor.RESET);
    }
}
