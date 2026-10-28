package com.recommendation.platform.model;

// Inheritance: extends abstract User
public class AdminUser extends User {

    private String adminLevel;  // SUPER, STANDARD

    public AdminUser(int userId, String name, String email, String password, String adminLevel) {
        super(userId, name, email, password);
        this.adminLevel = adminLevel;
    }

    // Polymorphism: overrides abstract method
    @Override
    public UserRole getRole() { return UserRole.ADMIN; }

    @Override
    public String getDashboardInfo() {
        return "=== Admin Dashboard ===\n"
             + "Name  : " + getName() + "\n"
             + "Email : " + getEmail() + "\n"
             + "Level : " + adminLevel;
    }

    public String getAdminLevel()             { return adminLevel; }
    public void   setAdminLevel(String level) { this.adminLevel = level; }
}
