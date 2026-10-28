package com.bank.model.transaction;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Cash Withdrawal"),
    TRANSFER_IN("Transfer Received"),
    TRANSFER_OUT("Transfer Sent"),
    INTEREST_CREDIT("Interest Credit"),
    FEE_DEBIT("Service / Overdraft Fee"),
    FD_CREATION("Fixed Deposit Placed"),
    FD_LIQUIDATION("Fixed Deposit Liquidated"),
    LOAN_DISBURSEMENT("Loan Disbursement"),
    LOAN_REPAYMENT("Loan EMI Repayment");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
