package com.recommendation.platform.service;

import com.recommendation.platform.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final UserService        userService;
    private final CatalogService     catalogService;
    private final InteractionService interactionService;

    public ReportService(UserService userService,
                          CatalogService catalogService,
                          InteractionService interactionService) {
        this.userService        = userService;
        this.catalogService     = catalogService;
        this.interactionService = interactionService;
    }

    // ── Platform Summary ──────────────────────────────────────────────────────

    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════╗\n");
        sb.append("║     PLATFORM ANALYTICS SUMMARY           ║\n");
        sb.append("╚══════════════════════════════════════════╝\n");
        sb.append("Generated: ").append(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n\n");

        sb.append("📊 OVERVIEW\n");
        sb.append("  Total Users       : ").append(userService.getTotalUsers()).append("\n");
        sb.append("  Total Courses     : ").append(catalogService.getTotalItems()).append("\n");
        sb.append("  Total Enrollments : ").append(interactionService.getTotalEnrollments()).append("\n\n");

        sb.append("⭐ TOP 5 COURSES BY RATING\n");
        catalogService.getAvailableItems().stream()
                .filter(i -> i.getTotalRatings() > 0)
                .sorted(Comparator.comparingDouble(Item::getAverageRating).reversed())
                .limit(5)
                .forEach(i -> sb.append("  ").append(i.getTitle())
                        .append(" — ").append(String.format("%.1f", i.getAverageRating()))
                        .append(" ⭐ (").append(i.getTotalRatings()).append(" ratings)\n"));

        sb.append("\n👁 TOP 5 MOST VIEWED COURSES\n");
        catalogService.getAvailableItems().stream()
                .sorted(Comparator.comparingInt(Item::getViewCount).reversed())
                .limit(5)
                .forEach(i -> sb.append("  ").append(i.getTitle())
                        .append(" — ").append(i.getViewCount()).append(" views\n"));

        return sb.toString();
    }

    // ── User Activity Report ──────────────────────────────────────────────────

    public String generateUserReport(int userId) {
        User user = userService.findById(userId);
        List<Enrollment> enrollments = interactionService.getEnrollmentsByUser(userId);

        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════\n");
        sb.append("  USER REPORT: ").append(user.getName()).append("\n");
        sb.append("══════════════════════════════════════\n");
        sb.append("Email  : ").append(user.getEmail()).append("\n");
        sb.append("Role   : ").append(user.getRole()).append("\n");
        sb.append("Status : ").append(user.isActive() ? "Active" : "Inactive").append("\n\n");

        sb.append("📚 ENROLLMENTS (").append(enrollments.size()).append(")\n");
        if (enrollments.isEmpty()) {
            sb.append("  No enrollments yet.\n");
        } else {
            for (Enrollment e : enrollments) {
                try {
                    Item item = catalogService.findById(e.getItemId());
                    sb.append("  • ").append(item.getTitle())
                      .append(" | ").append(e.getStatus())
                      .append(" | ").append(e.getProgressPercent()).append("%\n");
                } catch (Exception ex) {
                    sb.append("  • Course ID ").append(e.getItemId())
                      .append(" (removed)\n");
                }
            }
        }
        return sb.toString();
    }

    // ── CSV Export (File I/O) ─────────────────────────────────────────────────

    public String exportUsersToCsv(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("UserID,Name,Email,Role,Active");
            for (User u : userService.getAllUsers()) {
                pw.printf("%d,%s,%s,%s,%b%n",
                        u.getUserId(), u.getName(), u.getEmail(), u.getRole(), u.isActive());
            }
            return "✔ Users exported to: " + filePath;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }

    public String exportCoursesToCsv(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("CourseID,Title,Category,Price,AvgRating,ViewCount");
            for (Item i : catalogService.getAllItems()) {
                pw.printf("%d,\"%s\",%s,%.2f,%.1f,%d%n",
                        i.getItemId(), i.getTitle(), i.getCategory(),
                        i.getPrice(), i.getAverageRating(), i.getViewCount());
            }
            return "✔ Courses exported to: " + filePath;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
}
