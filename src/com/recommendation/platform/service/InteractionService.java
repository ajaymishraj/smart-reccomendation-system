package com.recommendation.platform.service;

import com.recommendation.platform.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class InteractionService {

    private final List<Interaction> interactions  = new ArrayList<>();
    private final List<Enrollment>  enrollments   = new ArrayList<>();
    private int nextInteractionId = 1;
    private int nextEnrollmentId  = 1;

    private final UserService    userService;
    private final CatalogService catalogService;

    public InteractionService(UserService userService, CatalogService catalogService) {
        this.userService    = userService;
        this.catalogService = catalogService;
    }

    // ── Enrollment ────────────────────────────────────────────────────────────

    public Enrollment enroll(int userId, int itemId) {
        User user = userService.findById(userId);
        Item item = catalogService.findById(itemId);

        // Check for duplicate
        boolean alreadyEnrolled = enrollments.stream()
                .anyMatch(e -> e.getUserId() == userId && e.getItemId() == itemId
                            && e.getStatus() == Enrollment.Status.ACTIVE);
        if (alreadyEnrolled) throw new IllegalStateException("Already enrolled in this course.");

        Enrollment enrollment = new Enrollment(nextEnrollmentId++, userId, itemId);
        enrollments.add(enrollment);

        if (item instanceof Course) ((Course) item).incrementEnrollment();
        if (user instanceof RegularUser) ((RegularUser) user).incrementEnrolled();

        logInteraction(userId, itemId, Interaction.InteractionType.ENROLL, 0);
        return enrollment;
    }

    public void markComplete(int userId, int itemId) {
        Enrollment e = enrollments.stream()
                .filter(en -> en.getUserId() == userId && en.getItemId() == itemId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active enrollment found."));
        e.complete();
        logInteraction(userId, itemId, Interaction.InteractionType.COMPLETE, 0);
    }

    // ── Rating ────────────────────────────────────────────────────────────────

    public void rateItem(int userId, int itemId, double rating) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1-5.");
        Item item = catalogService.findById(itemId);
        item.addRating(rating);

        User user = userService.findById(userId);
        if (user instanceof RegularUser) {
            RegularUser ru = (RegularUser) user;
            ru.setAverageRating(item.getAverageRating());
        }
        logInteraction(userId, itemId, Interaction.InteractionType.RATE, rating);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<Item> getEnrolledItems(int userId) {
        List<Integer> itemIds = enrollments.stream()
                .filter(e -> e.getUserId() == userId)
                .map(Enrollment::getItemId)
                .collect(Collectors.toList());
        return itemIds.stream()
                .map(id -> {
                    try { return catalogService.findById(id); }
                    catch (Exception ex) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getEnrollmentsByUser(int userId) {
        return enrollments.stream()
                .filter(e -> e.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public List<Interaction> getInteractionsByUser(int userId) {
        return interactions.stream()
                .filter(i -> i.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public Map<Integer, Set<Integer>> buildUserItemMatrix() {
        Map<Integer, Set<Integer>> matrix = new HashMap<>();
        for (Enrollment e : enrollments) {
            matrix.computeIfAbsent(e.getUserId(), k -> new HashSet<>()).add(e.getItemId());
        }
        return matrix;
    }

    public int getTotalEnrollments() { return enrollments.size(); }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void logInteraction(int userId, int itemId,
                                 Interaction.InteractionType type, double rating) {
        Interaction it = new Interaction(nextInteractionId++, userId, itemId, type);
        it.setRatingValue(rating);
        interactions.add(it);
        User user = userService.findById(userId);
        user.addInteraction(it);
    }
}
