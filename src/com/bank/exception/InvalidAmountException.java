package com.bank.exception;

public class InvalidAmountException extends BankingException {
    public InvalidAmountException(String message) {
        super("ERR_INVALID_AMOUNT", message);
    }

    public InvalidAmountException(double amount) {
        super("ERR_INVALID_AMOUNT", String.format("The specified transaction amount ($%.2f) must be strictly positive.", amount));
    }
}
