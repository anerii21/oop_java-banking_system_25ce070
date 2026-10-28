package com.bank.model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Customer class extending User.
 * Holds multiple linked bank accounts, address, and KYC verification status.
 */
public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private String address;
    private boolean kycVerified;
    private final List<String> accountNumbers;

    public Customer(String userId, String username, String passwordHash, String salt,
                    String fullName, String email, String phoneNumber, String address) {
        super(userId, username, passwordHash, salt, fullName, email, phoneNumber, Role.CUSTOMER);
        this.address = address;
        this.kycVerified = true;
        this.accountNumbers = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isKycVerified() {
        return kycVerified;
    }

    public void setKycVerified(boolean kycVerified) {
        this.kycVerified = kycVerified;
    }

    public List<String> getAccountNumbers() {
        return Collections.unmodifiableList(accountNumbers);
    }

    public void linkAccount(String accountNumber) {
        if (!accountNumbers.contains(accountNumber)) {
            accountNumbers.add(accountNumber);
        }
    }

    public void unlinkAccount(String accountNumber) {
        accountNumbers.remove(accountNumber);
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Customer [%s] %s | Email: %s | Phone: %s | Accounts: %d",
                getUserId(), getFullName(), getEmail(), getPhoneNumber(), accountNumbers.size());
    }
}
