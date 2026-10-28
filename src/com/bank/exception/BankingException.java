package com.bank.exception;

/**
 * Base checked exception for all banking system domain errors.
 * Demonstrates clean exception hierarchy and OOP error handling.
 */
public class BankingException extends Exception {
    private final String errorCode;

    public BankingException(String message) {
        super(message);
        this.errorCode = "BANK_ERR_GENERIC";
    }

    public BankingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BankingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
