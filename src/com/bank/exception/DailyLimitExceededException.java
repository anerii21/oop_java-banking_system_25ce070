package com.bank.exception;

public class DailyLimitExceededException extends BankingException {
    public DailyLimitExceededException(double requestedAmount, double remainingLimit) {
        super("ERR_DAILY_LIMIT_EXCEEDED", 
              String.format("Transaction of $%.2f exceeds your daily withdrawal limit. Remaining limit for today is $%.2f", 
                            requestedAmount, remainingLimit));
    }
}
