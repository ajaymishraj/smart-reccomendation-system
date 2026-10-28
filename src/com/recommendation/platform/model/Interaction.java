package com.recommendation.platform.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Interaction {

    public enum InteractionType { VIEW, ENROLL, RATE, COMPLETE }

    private int interactionId;
    private int userId;
    private int itemId;
    private InteractionType type;
    private double ratingValue;   // 0 if not a RATE interaction
    private LocalDateTime timestamp;

    public Interaction(int interactionId, int userId, int itemId, InteractionType type) {
        this.interactionId = interactionId;
        this.userId        = userId;
        this.itemId        = itemId;
        this.type          = type;
        this.ratingValue   = 0.0;
        this.timestamp     = LocalDateTime.now();
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int             getInteractionId() { return interactionId; }
    public int             getUserId()        { return userId; }
    public int             getItemId()        { return itemId; }
    public InteractionType getType()          { return type; }
    public double          getRatingValue()   { return ratingValue; }
    public LocalDateTime   getTimestamp()     { return timestamp; }

    public void setRatingValue(double r)      { this.ratingValue = r; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Interaction[%d] User:%d -> Item:%d | %s | Rating:%.1f | %s",
                interactionId, userId, itemId, type, ratingValue, timestamp.format(fmt));
    }
}
