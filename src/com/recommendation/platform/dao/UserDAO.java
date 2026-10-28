package com.recommendation.platform.dao;

import com.recommendation.platform.exception.UserNotFoundException;
import com.recommendation.platform.model.User;

import java.util.*;

/**
 * UserDAO — Singleton Pattern.
 * Acts as an in-memory database for users.
 */
public class UserDAO {

    // Singleton instance
    private static UserDAO instance;
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    // Private constructor — Singleton
    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    public int generateId() { return nextId++; }

    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<User> findAll() { return new ArrayList<>(users.values()); }

    public void delete(int id) {
        if (!users.containsKey(id)) throw new UserNotFoundException("User ID " + id + " not found");
        users.remove(id);
    }

    public int count() { return users.size(); }
}
