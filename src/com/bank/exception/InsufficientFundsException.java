package com.bank.exception;

public class InsufficientFundsException extends BankingException {
    private final double availableBalance;
    private final double attemptedAmount;

    public InsufficientFundsException(double availableBalance, double attemptedAmount) {
        super("ERR_INSUFFICIENT_FUNDS", 
              String.format("Insufficient funds: Attempted to withdraw/transfer $%.2f, but available balance is only $%.2f", 
                            attemptedAmount, availableBalance));
        this.availableBalance = availableBalance;
        this.attemptedAmount = attemptedAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getAttemptedAmount() {
        return attemptedAmount;
    }
}
