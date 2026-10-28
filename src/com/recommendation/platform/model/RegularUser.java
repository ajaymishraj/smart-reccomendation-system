package com.recommendation.platform.model;

import java.util.ArrayList;
import java.util.List;

// Inheritance: extends abstract User
public class RegularUser extends User {

    private List<String> preferredCategories;
    private int totalCoursesEnrolled;
    private double averageRating;

    public RegularUser(int userId, String name, String email, String password) {
        super(userId, name, email, password);
        this.preferredCategories  = new ArrayList<>();
        this.totalCoursesEnrolled = 0;
        this.averageRating        = 0.0;
    }

    // Polymorphism: overrides abstract method
    @Override
    public UserRole getRole() { return UserRole.REGULAR_USER; }

    @Override
    public String getDashboardInfo() {
        return "=== User Dashboard ===\n"
             + "Name    : " + getName() + "\n"
             + "Email   : " + getEmail() + "\n"
             + "Enrolled: " + totalCoursesEnrolled + " courses\n"
             + "Avg Rating Given: " + String.format("%.1f", averageRating);
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public List<String> getPreferredCategories()       { return preferredCategories; }
    public int   getTotalCoursesEnrolled()             { return totalCoursesEnrolled; }
    public double getAverageRating()                   { return averageRating; }

    public void addPreferredCategory(String cat)       { preferredCategories.add(cat); }
    public void incrementEnrolled()                    { totalCoursesEnrolled++; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}
