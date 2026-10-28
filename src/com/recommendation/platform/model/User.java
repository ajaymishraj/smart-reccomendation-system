package com.recommendation.platform.model;

import java.util.ArrayList;
import java.util.List;

// Abstraction: abstract base class
public abstract class User {

    // Encapsulation: all fields private
    private int userId;
    private String name;
    private String email;
    private String password;
    private boolean active;
    private List<Interaction> interactionHistory;

    public User(int userId, String name, String email, String password) {
        this.userId      = userId;
        this.name        = name;
        this.email       = email;
        this.password    = password;
        this.active      = true;
        this.interactionHistory = new ArrayList<>();
    }

    // Abstraction: subclasses must define their role
    public abstract UserRole getRole();

    // Abstraction: subclasses may override dashboard info
    public abstract String getDashboardInfo();

    // ── Encapsulation: getters / setters ─────────────────────────────────────

    public int getUserId()    { return userId; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public boolean isActive() { return active; }

    public List<Interaction> getInteractionHistory() {
        return new ArrayList<>(interactionHistory); // defensive copy
    }

    public void setName(String name)         { this.name     = name; }
    public void setEmail(String email)       { this.email    = email; }
    public void setPassword(String password) { this.password = password; }
    public void setActive(boolean active)    { this.active   = active; }

    public boolean checkPassword(String pwd) { return this.password.equals(pwd); }

    public void addInteraction(Interaction i) { interactionHistory.add(i); }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) | Role: %s | Active: %s",
                userId, name, email, getRole(), active);
    }
}
