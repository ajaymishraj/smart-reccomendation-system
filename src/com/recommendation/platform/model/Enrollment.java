package com.recommendation.platform.model;

import java.time.LocalDate;

public class Enrollment {

    public enum Status { ACTIVE, COMPLETED, DROPPED }

    private int enrollmentId;
    private int userId;
    private int itemId;
    private LocalDate enrollDate;
    private LocalDate completionDate;
    private Status status;
    private int progressPercent;

    public Enrollment(int enrollmentId, int userId, int itemId) {
        this.enrollmentId   = enrollmentId;
        this.userId         = userId;
        this.itemId         = itemId;
        this.enrollDate     = LocalDate.now();
        this.completionDate = null;
        this.status         = Status.ACTIVE;
        this.progressPercent= 0;
    }

    public void complete() {
        this.status          = Status.COMPLETED;
        this.completionDate  = LocalDate.now();
        this.progressPercent = 100;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int       getEnrollmentId()           { return enrollmentId; }
    public int       getUserId()                 { return userId; }
    public int       getItemId()                 { return itemId; }
    public LocalDate getEnrollDate()             { return enrollDate; }
    public LocalDate getCompletionDate()         { return completionDate; }
    public Status    getStatus()                 { return status; }
    public int       getProgressPercent()        { return progressPercent; }

    public void setStatus(Status s)              { this.status          = s; }
    public void setProgressPercent(int p)        { this.progressPercent = p; }

    @Override
    public String toString() {
        return String.format("Enrollment[%d] User:%d | Course:%d | Status:%s | Progress:%d%%",
                enrollmentId, userId, itemId, status, progressPercent);
    }
}
