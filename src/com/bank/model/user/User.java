package com.bank.model.user;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract Base Class representing a User in the banking system.
 * Demonstrates OOP Encapsulation and Inheritance.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String userId;
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private String email;
    private String phoneNumber;
    private final Role role;
    private final LocalDateTime createdAt;
    private boolean active;

    public User(String userId, String username, String passwordHash, String salt,
                String fullName, String email, String phoneNumber, Role role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract String getDisplayInfo();

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s (%s) | Role: %s | Active: %s",
                role, userId, fullName, username, role, active);
    }
}
