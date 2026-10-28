package com.bank.service;

import com.bank.model.account.LoanAccount;
import com.bank.util.CurrencyFormatter;

/**
 * Service for banking loan simulations, interest computations, and amortization schedules.
 */
public final class LoanCalculator {

    private LoanCalculator() {}

    public static double computeEmi(double principal, double annualRate, int tenureMonths) {
        return LoanAccount.calculateEmi(principal, annualRate, tenureMonths);
    }

    public static double computeTotalInterest(double principal, double annualRate, int tenureMonths) {
        double emi = computeEmi(principal, annualRate, tenureMonths);
        double totalRepayment = emi * tenureMonths;
        return totalRepayment - principal;
    }

    public static String generateAmortizationPreview(double principal, double annualRate, int tenureMonths) {
        double emi = computeEmi(principal, annualRate, tenureMonths);
        double totalInterest = computeTotalInterest(principal, annualRate, tenureMonths);
        double totalPayment = principal + totalInterest;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Loan Principal        : %s\n", CurrencyFormatter.format(principal)));
        sb.append(String.format("Annual Interest Rate  : %.2f%%\n", annualRate));
        sb.append(String.format("Tenure                : %d months (%.1f years)\n", tenureMonths, tenureMonths / 12.0));
        sb.append(String.format("Monthly EMI           : %s\n", CurrencyFormatter.format(emi)));
        sb.append(String.format("Total Interest Payable: %s\n", CurrencyFormatter.format(totalInterest)));
        sb.append(String.format("Total Repayment Amount: %s\n", CurrencyFormatter.format(totalPayment)));
        return sb.toString();
    }
}
