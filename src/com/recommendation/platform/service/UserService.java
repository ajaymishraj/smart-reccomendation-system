package com.recommendation.platform.service;

import com.recommendation.platform.dao.UserDAO;
import com.recommendation.platform.exception.UserNotFoundException;
import com.recommendation.platform.model.*;

import java.util.List;
import java.util.Optional;

public class UserService {

    // Factory Pattern: create correct User subclass based on role
    private final UserDAO userDAO = UserDAO.getInstance();

    public User registerRegularUser(String name, String email, String password) {
        validateEmailUnique(email);
        int id = userDAO.generateId();
        RegularUser user = new RegularUser(id, name, email, password);
        return userDAO.save(user);
    }

    public User registerAdmin(String name, String email, String password, String level) {
        validateEmailUnique(email);
        int id = userDAO.generateId();
        AdminUser admin = new AdminUser(id, name, email, password, level);
        return userDAO.save(admin);
    }

    public User registerContentManager(String name, String email, String password, String dept) {
        validateEmailUnique(email);
        int id = userDAO.generateId();
        ContentManager cm = new ContentManager(id, name, email, password, dept);
        return userDAO.save(cm);
    }

    public User login(String email, String password) {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No account found for: " + email));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        if (!user.isActive()) {
            throw new IllegalStateException("Account is deactivated.");
        }
        return user;
    }

    public User findById(int id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    public List<User> getAllUsers() { return userDAO.findAll(); }

    public void deactivateUser(int id) {
        User u = findById(id);
        u.setActive(false);
        userDAO.save(u);
    }

    public void activateUser(int id) {
        User u = findById(id);
        u.setActive(true);
        userDAO.save(u);
    }

    public int getTotalUsers() { return userDAO.count(); }

    private void validateEmailUnique(String email) {
        Optional<User> existing = userDAO.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
    }
}
