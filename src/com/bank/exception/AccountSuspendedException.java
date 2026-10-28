package com.bank.exception;

public class AccountSuspendedException extends BankingException {
    public AccountSuspendedException(String accountNumber) {
        super("ERR_ACCOUNT_SUSPENDED", "Account " + accountNumber + " is currently frozen or inactive. Please contact bank administration.");
    }
}
