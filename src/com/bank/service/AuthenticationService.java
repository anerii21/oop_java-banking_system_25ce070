package com.bank.service;

import com.bank.exception.AuthenticationException;
import com.bank.model.user.Customer;
import com.bank.model.user.User;
import com.bank.util.PasswordUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Service managing user authentication, credential security, and session validation.
 */
public class AuthenticationService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, User> usersByUsername;
    private final Map<String, User> usersById;

    public AuthenticationService() {
        this.usersByUsername = new HashMap<>();
        this.usersById = new HashMap<>();
    }

    public synchronized void registerUser(User user) {
        usersByUsername.put(user.getUsername().toLowerCase(), user);
        usersById.put(user.getUserId(), user);
    }

    public synchronized User authenticate(String username, String rawPassword) throws AuthenticationException {
        User user = usersByUsername.get(username.toLowerCase().trim());
        if (user == null) {
            throw new AuthenticationException("Invalid username or password.");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("This account has been deactivated. Please contact support.");
        }

        boolean valid = PasswordUtil.verifyPassword(rawPassword, user.getPasswordHash(), user.getSalt());
        if (!valid) {
            throw new AuthenticationException("Invalid username or password.");
        }

        return user;
    }

    public synchronized Customer registerNewCustomer(String userId, String username, String rawPassword,
                                                     String fullName, String email, String phoneNumber,
                                                     String address) throws AuthenticationException {
        if (usersByUsername.containsKey(username.toLowerCase().trim())) {
            throw new AuthenticationException("Username '" + username + "' is already taken. Please choose another.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(rawPassword, salt);
        Customer customer = new Customer(userId, username, hash, salt, fullName, email, phoneNumber, address);
        registerUser(customer);
        return customer;
    }

    public synchronized void changePassword(User user, String oldPassword, String newPassword) throws AuthenticationException {
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash(), user.getSalt())) {
            throw new AuthenticationException("Current password verification failed.");
        }
        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, newSalt);
        user.setSalt(newSalt);
        user.setPasswordHash(newHash);
    }

    public synchronized User getUserById(String userId) {
        return usersById.get(userId);
    }

    public synchronized User getUserByUsername(String username) {
        return usersByUsername.get(username.toLowerCase().trim());
    }

    public synchronized Map<String, User> getAllUsers() {
        return new HashMap<>(usersByUsername);
    }
}
