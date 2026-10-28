package com.recommendation.platform.dao;

import com.recommendation.platform.exception.ItemNotFoundException;
import com.recommendation.platform.model.Item;
import com.recommendation.platform.model.ItemCategory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ItemDAO — Singleton Pattern.
 * Acts as an in-memory database for items/courses.
 */
public class ItemDAO {

    private static ItemDAO instance;
    private final Map<Integer, Item> items = new LinkedHashMap<>();
    private int nextId = 1;

    private ItemDAO() {}

    public static ItemDAO getInstance() {
        if (instance == null) instance = new ItemDAO();
        return instance;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public Item save(Item item) {
        items.put(item.getItemId(), item);
        return item;
    }

    public int generateId() { return nextId++; }

    public Optional<Item> findById(int id) { return Optional.ofNullable(items.get(id)); }

    public List<Item> findAll() { return new ArrayList<>(items.values()); }

    public List<Item> findByCategory(ItemCategory category) {
        return items.values().stream()
                .filter(i -> i.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Item> findAvailable() {
        return items.values().stream()
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }

    public void delete(int id) {
        if (!items.containsKey(id)) throw new ItemNotFoundException("Item ID " + id + " not found");
        items.remove(id);
    }

    public int count() { return items.size(); }
}
