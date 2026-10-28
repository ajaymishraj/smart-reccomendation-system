package com.recommendation.platform.ui;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based UI — drives the entire application.
 * Demonstrates all OOP features through real user interactions.
 */
public class ConsoleUI {

    private final UserService            userService;
    private final CatalogService         catalogService;
    private final InteractionService     interactionService;
    private final RecommendationService  recommendationService;
    private final ReportService          reportService;

    private User loggedInUser = null;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(UserService us, CatalogService cs,
                     InteractionService is, RecommendationService rs, ReportService rep) {
        this.userService           = us;
        this.catalogService        = cs;
        this.interactionService    = is;
        this.recommendationService = rs;
        this.reportService         = rep;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ENTRY
    // ══════════════════════════════════════════════════════════════════════════

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            if (loggedInUser == null) running = showLoginMenu();
            else                      running = showMainMenu();
        }
        System.out.println("\n👋 Thank you for using the Smart Recommendation Platform!");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  AUTH MENU
    // ══════════════════════════════════════════════════════════════════════════

    private boolean showLoginMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│         WELCOME — LOGIN         │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1. Login                       │");
        System.out.println("│  2. Register as Student         │");
        System.out.println("│  0. Exit                        │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Choice: ");
        int choice = readInt();
        switch (choice) {
            case 1 -> doLogin();
            case 2 -> doRegister();
            case 0 -> { return false; }
            default -> System.out.println("❌ Invalid option.");
        }
        return true;
    }

    private void doLogin() {
        System.out.print("Email   : "); String email = scanner.nextLine().trim();
        System.out.print("Password: "); String pass  = scanner.nextLine().trim();
        try {
            loggedInUser = userService.login(email, pass);
            System.out.println("\n✔ Welcome back, " + loggedInUser.getName() + "! [" + loggedInUser.getRole() + "]");
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void doRegister() {
        System.out.print("Name    : "); String name  = scanner.nextLine().trim();
        System.out.print("Email   : "); String email = scanner.nextLine().trim();
        System.out.print("Password: "); String pass  = scanner.nextLine().trim();
        try {
            User u = userService.registerRegularUser(name, email, pass);
            System.out.println("✔ Registration successful! Your ID: " + u.getUserId());
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN MENU (role-based)
    // ══════════════════════════════════════════════════════════════════════════

    private boolean showMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf ("║  Logged in: %-28s║%n", loggedInUser.getName());
        System.out.printf ("║  Role     : %-28s║%n", loggedInUser.getRole());
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Browse Courses                       ║");
        System.out.println("║  2. My Enrollments                       ║");
        System.out.println("║  3. Get Recommendations                  ║");
        System.out.println("║  4. Enroll in a Course                   ║");
        System.out.println("║  5. Rate a Course                        ║");
        System.out.println("║  6. My Dashboard                         ║");
        if (loggedInUser.getRole() == UserRole.ADMIN ||
            loggedInUser.getRole() == UserRole.CONTENT_MANAGER) {
            System.out.println("║  7. [ADMIN] Manage Courses               ║");
            System.out.println("║  8. [ADMIN] Manage Users                 ║");
            System.out.println("║  9. [ADMIN] Reports & Export             ║");
            System.out.println("║ 10. [ADMIN] Change Strategy              ║");
        }
        System.out.println("║  0. Logout                               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Choice: ");
        int choice = readInt();

        try {
            switch (choice) {
                case 1  -> browseCourses();
                case 2  -> myEnrollments();
                case 3  -> showRecommendations();
                case 4  -> enrollInCourse();
                case 5  -> rateCourse();
                case 6  -> System.out.println("\n" + loggedInUser.getDashboardInfo());
                case 7  -> manageCourses();
                case 8  -> manageUsers();
                case 9  -> reportsMenu();
                case 10 -> changeStrategy();
                case 0  -> { loggedInUser = null; System.out.println("✔ Logged out."); }
                default -> System.out.println("❌ Invalid option.");
            }
        } catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
        return true;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STUDENT FEATURES
    // ══════════════════════════════════════════════════════════════════════════

    private void browseCourses() {
        System.out.println("\n📚 ALL AVAILABLE COURSES");
        System.out.println("─".repeat(80));
        List<Item> items = catalogService.getAvailableItems();
        if (items.isEmpty()) { System.out.println("No courses available."); return; }
        items.forEach(i -> System.out.println("  " + i));
    }

    private void myEnrollments() {
        System.out.println("\n📋 MY ENROLLMENTS");
        System.out.println("─".repeat(60));
        List<Enrollment> enrs = interactionService.getEnrollmentsByUser(loggedInUser.getUserId());
        if (enrs.isEmpty()) { System.out.println("You have not enrolled in any courses yet."); return; }
        for (Enrollment e : enrs) {
            try {
                Item item = catalogService.findById(e.getItemId());
                System.out.printf("  %s | Status: %s | Progress: %d%%%n",
                        item.getTitle(), e.getStatus(), e.getProgressPercent());
            } catch (Exception ex) { System.out.println("  (Course removed)"); }
        }
    }

    private void showRecommendations() {
        System.out.println("\n🤖 RECOMMENDATIONS FOR YOU");
        System.out.println("Strategy: " + recommendationService.getCurrentStrategy());
        System.out.println("─".repeat(70));
        List<Item> recs = recommendationService.getRecommendations(loggedInUser);
        if (recs.isEmpty()) {
            System.out.println("Not enough data yet — try enrolling in a few courses first!");
        } else {
            recs.forEach(r -> System.out.println("  ★ " + r));
        }
    }

    private void enrollInCourse() {
        browseCourses();
        System.out.print("\nEnter Course ID to enroll: ");
        int id = readInt();
        Enrollment e = interactionService.enroll(loggedInUser.getUserId(), id);
        Item item = catalogService.findById(id);
        System.out.println("✔ Enrolled in: " + item.getTitle());
    }

    private void rateCourse() {
        myEnrollments();
        System.out.print("\nEnter Course ID to rate: ");
        int id = readInt();
        System.out.print("Your rating (1-5): ");
        double rating;
        try { rating = Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException ex) { System.out.println("❌ Invalid number."); return; }
        interactionService.rateItem(loggedInUser.getUserId(), id, rating);
        System.out.printf("✔ You rated Course #%d → %.1f ⭐%n", id, rating);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN FEATURES
    // ══════════════════════════════════════════════════════════════════════════

    private void manageCourses() {
        System.out.println("\n⚙ COURSE MANAGEMENT");
        System.out.println("  1. Add New Course");
        System.out.println("  2. View All Courses (with details)");
        System.out.println("  3. Remove Course");
        System.out.print("Choice: ");
        int c = readInt();
        switch (c) {
            case 1 -> addCourse();
            case 2 -> browseCourses();
            case 3 -> removeCourse();
            default -> System.out.println("Cancelled.");
        }
    }

    private void addCourse() {
        System.out.println("\n─ NEW COURSE ─");
        System.out.print("Title      : "); String title = scanner.nextLine().trim();
        System.out.print("Description: "); String desc  = scanner.nextLine().trim();
        System.out.println("Category (PROGRAMMING/DATA_SCIENCE/WEB_DEVELOPMENT/ARTIFICIAL_INTELLIGENCE/CLOUD_COMPUTING/CYBERSECURITY/DATABASE/NETWORKING):");
        System.out.print("Category   : ");
        String catStr = scanner.nextLine().trim().toUpperCase();
        ItemCategory cat;
        try { cat = ItemCategory.valueOf(catStr); }
        catch (Exception e) { System.out.println("❌ Invalid category."); return; }
        System.out.print("Price (₹)  : "); double price;
        try { price = Double.parseDouble(scanner.nextLine().trim()); }
        catch (Exception e) { System.out.println("❌ Invalid price."); return; }
        System.out.print("Instructor : "); String inst  = scanner.nextLine().trim();
        System.out.print("Hours      : "); int hours;
        try { hours = Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { System.out.println("❌ Invalid hours."); return; }
        System.out.print("Level (BEGINNER/INTERMEDIATE/ADVANCED): "); String level = scanner.nextLine().trim().toUpperCase();
        Course course = catalogService.addCourse(title, desc, cat, price, inst, hours, level);
        System.out.println("✔ Course added! ID: " + course.getItemId());
    }

    private void removeCourse() {
        System.out.print("Course ID to remove: ");
        int id = readInt();
        catalogService.removeItem(id);
        System.out.println("✔ Course #" + id + " removed.");
    }

    private void manageUsers() {
        System.out.println("\n👥 ALL USERS");
        System.out.println("─".repeat(70));
        userService.getAllUsers().forEach(u -> System.out.println("  " + u));

        System.out.println("\n  1. Deactivate User");
        System.out.println("  2. Activate User");
        System.out.println("  3. View User Report");
        System.out.println("  0. Back");
        System.out.print("Choice: ");
        int c = readInt();
        switch (c) {
            case 1 -> { System.out.print("User ID: "); int id = readInt(); userService.deactivateUser(id); System.out.println("✔ User deactivated."); }
            case 2 -> { System.out.print("User ID: "); int id = readInt(); userService.activateUser(id);   System.out.println("✔ User activated."); }
            case 3 -> { System.out.print("User ID: "); int id = readInt(); System.out.println(reportService.generateUserReport(id)); }
            default -> { /* back */ }
        }
    }

    private void reportsMenu() {
        System.out.println("\n📊 REPORTS");
        System.out.println("  1. Platform Analytics Summary");
        System.out.println("  2. User Report");
        System.out.println("  3. Export Users to CSV");
        System.out.println("  4. Export Courses to CSV");
        System.out.print("Choice: ");
        int c = readInt();
        switch (c) {
            case 1 -> System.out.println(reportService.generateSummaryReport());
            case 2 -> { System.out.print("User ID: "); int id = readInt(); System.out.println(reportService.generateUserReport(id)); }
            case 3 -> System.out.println(reportService.exportUsersToCsv("users_export.csv"));
            case 4 -> System.out.println(reportService.exportCoursesToCsv("courses_export.csv"));
            default -> System.out.println("Cancelled.");
        }
    }

    private void changeStrategy() {
        System.out.println("\n🔄 CHANGE RECOMMENDATION STRATEGY");
        System.out.println("Current: " + recommendationService.getCurrentStrategy());
        System.out.println("  1. Popularity-Based");
        System.out.println("  2. Content-Based Filtering");
        System.out.println("  3. Collaborative Filtering");
        System.out.print("Choice: ");
        int c = readInt();
        switch (c) {
            case 1 -> recommendationService.switchToPopularity();
            case 2 -> recommendationService.switchToContentBased();
            case 3 -> recommendationService.switchToCollaborative();
            default -> System.out.println("Cancelled.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private int readInt() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void printBanner() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  🎓 Smart Recommendation Service Platform  v1.0  ║");
        System.out.println("║     NIET Greater Noida | OOT Capstone #256       ║");
        System.out.println("║     SDG 9: Industry, Innovation & Infrastructure ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}
