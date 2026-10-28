package com.bank.exception;

public class MinimumBalanceViolationException extends BankingException {
    public MinimumBalanceViolationException(double attemptedWithdrawal, double currentBalance, double minimumRequired) {
        super("ERR_MIN_BALANCE_VIOLATION",
              String.format("Withdrawal of $%.2f denied: Account balance ($%.2f) would drop below required minimum balance ($%.2f).",
                            attemptedWithdrawal, currentBalance, minimumRequired));
    }
}
