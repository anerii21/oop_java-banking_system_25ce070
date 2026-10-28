package com.bank.exception;

public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String accountNumber) {
        super("ERR_ACCOUNT_NOT_FOUND", "Account with number '" + accountNumber + "' was not found in the banking system.");
    }
}
