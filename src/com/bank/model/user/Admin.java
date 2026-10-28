package com.bank.model.user;

/**
 * Concrete Admin class extending User.
 * Has administrative privileges for bank-wide operations and audits.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String department;
    private int accessLevel; // 1: Staff, 2: Manager, 3: SuperAdmin

    public Admin(String userId, String username, String passwordHash, String salt,
                 String fullName, String email, String phoneNumber, String employeeId,
                 String department, int accessLevel) {
        super(userId, username, passwordHash, salt, fullName, email, phoneNumber, Role.ADMIN);
        this.employeeId = employeeId;
        this.department = department;
        this.accessLevel = accessLevel;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Admin [%s] %s | EmpID: %s | Dept: %s | Level: %d",
                getUserId(), getFullName(), employeeId, department, accessLevel);
    }
}
