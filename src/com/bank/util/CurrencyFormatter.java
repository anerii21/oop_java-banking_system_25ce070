package com.bank.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Standardizes monetary formatting across all UI screens and receipts.
 */
public final class CurrencyFormatter {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    private CurrencyFormatter() {}

    public static String format(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
}
