package com.bank.model.account;

public enum AccountType {
    SAVINGS("Savings Account"),
    CHECKING("Checking Account"),
    FIXED_DEPOSIT("Fixed Deposit Account"),
    LOAN("Personal / Auto Loan Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
