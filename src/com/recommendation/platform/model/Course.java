package com.recommendation.platform.model;

// Inheritance: extends abstract Item
public class Course extends Item {

    private String instructor;
    private int durationHours;
    private String level;       // BEGINNER, INTERMEDIATE, ADVANCED
    private int enrollmentCount;

    public Course(int itemId, String title, String description,
                  ItemCategory category, double price,
                  String instructor, int durationHours, String level) {
        super(itemId, title, description, category, price);
        this.instructor      = instructor;
        this.durationHours   = durationHours;
        this.level           = level;
        this.enrollmentCount = 0;
    }

    // Polymorphism: overrides abstract method
    @Override
    public String getItemType() { return "Course"; }

    public void incrementEnrollment() { enrollmentCount++; }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getInstructor()           { return instructor; }
    public int    getDurationHours()        { return durationHours; }
    public String getLevel()                { return level; }
    public int    getEnrollmentCount()      { return enrollmentCount; }

    public void setInstructor(String i)     { this.instructor    = i; }
    public void setDurationHours(int d)     { this.durationHours = d; }
    public void setLevel(String l)          { this.level         = l; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Instructor: %s | %dh | %s | Enrolled: %d",
                instructor, durationHours, level, enrollmentCount);
    }
}
