package com.bank.model.account;

/**
 * Interface representing interest-earning accounts.
 * Demonstrates OOP Abstraction / Polymorphism.
 */
public interface InterestBearing {
    /**
     * Calculates the accrued interest without modifying balance.
     * @return calculated interest amount
     */
    double calculateInterest();

    /**
     * Applies calculated interest directly to account balance.
     * @return applied interest amount
     */
    double applyInterest();

    /**
     * Returns annual interest rate percentage (e.g. 4.5 for 4.5%).
     */
    double getInterestRate();
}
