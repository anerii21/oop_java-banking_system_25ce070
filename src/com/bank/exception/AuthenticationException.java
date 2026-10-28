package com.bank.exception;

public class AuthenticationException extends BankingException {
    public AuthenticationException(String message) {
        super("ERR_AUTHENTICATION_FAILED", message);
    }
}
