package com.recommendation.platform.model;

// Abstraction: abstract Item hierarchy
public abstract class Item {

    private int itemId;
    private String title;
    private String description;
    private ItemCategory category;
    private double price;
    private boolean available;
    private double averageRating;
    private int totalRatings;
    private int viewCount;

    public Item(int itemId, String title, String description, ItemCategory category, double price) {
        this.itemId       = itemId;
        this.title        = title;
        this.description  = description;
        this.category     = category;
        this.price        = price;
        this.available    = true;
        this.averageRating= 0.0;
        this.totalRatings = 0;
        this.viewCount    = 0;
    }

    // Abstraction: subclasses define item type label
    public abstract String getItemType();

    // Business logic: update running average
    public void addRating(double rating) {
        totalRatings++;
        averageRating = ((averageRating * (totalRatings - 1)) + rating) / totalRatings;
    }

    public void incrementViewCount() { viewCount++; }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int          getItemId()          { return itemId; }
    public String       getTitle()           { return title; }
    public String       getDescription()     { return description; }
    public ItemCategory getCategory()        { return category; }
    public double       getPrice()           { return price; }
    public boolean      isAvailable()        { return available; }
    public double       getAverageRating()   { return averageRating; }
    public int          getTotalRatings()    { return totalRatings; }
    public int          getViewCount()       { return viewCount; }

    public void setTitle(String t)           { this.title       = t; }
    public void setDescription(String d)     { this.description = d; }
    public void setPrice(double p)           { this.price       = p; }
    public void setAvailable(boolean a)      { this.available   = a; }
    public void setCategory(ItemCategory c)  { this.category    = c; }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s | ₹%.0f | ⭐%.1f (%d ratings) | 👁 %d views",
                itemId, title, getItemType(), category, price, averageRating, totalRatings, viewCount);
    }
}
