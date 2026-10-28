package com.recommendation.platform.service;

import com.recommendation.platform.dao.ItemDAO;
import com.recommendation.platform.exception.ItemNotFoundException;
import com.recommendation.platform.model.*;

import java.util.List;

public class CatalogService {

    private final ItemDAO itemDAO = ItemDAO.getInstance();

    public Course addCourse(String title, String description,
                            ItemCategory category, double price,
                            String instructor, int durationHours, String level) {
        int id = itemDAO.generateId();
        Course course = new Course(id, title, description, category, price,
                                   instructor, durationHours, level);
        itemDAO.save(course);
        return course;
    }

    public Item findById(int id) {
        return itemDAO.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found: " + id));
    }

    public List<Item> getAllItems()             { return itemDAO.findAll(); }
    public List<Item> getAvailableItems()      { return itemDAO.findAvailable(); }

    public List<Item> getByCategory(ItemCategory cat) { return itemDAO.findByCategory(cat); }

    public void removeItem(int id) { itemDAO.delete(id); }

    public void recordView(int itemId, int userId) {
        Item item = findById(itemId);
        item.incrementViewCount();
    }

    public void updateCourse(int id, String title, String description,
                              double price, String level) {
        Item item = findById(id);
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        if (item instanceof Course) ((Course) item).setLevel(level);
    }

    public int getTotalItems() { return itemDAO.count(); }
}
