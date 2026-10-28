package com.recommendation.platform.ui;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Text-based interactive Command Line Interface for testing platform functions (BCSE301 Unit 1).
 */
public class ConsoleUI {

    private final UserService userService;
    private final CatalogService catalogService;
    private final InteractionService interactionService;
    private final RecommendationService recommendationService;
    private final ReportService reportService;

    private User loggedInUser = null;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(UserService us, CatalogService cs,
                     InteractionService is, RecommendationService rs, ReportService rep) {
        this.userService = us;
        this.catalogService = cs;
        this.interactionService = is;
        this.recommendationService = rs;
        this.reportService = rep;
    }

    public void start() {
        System.out.println("\n--- Smart Recommendation Platform CLI ---");
        boolean running = true;
        while (running) {
            if (loggedInUser == null) {
                System.out.println("\n1. Login | 2. Register Student | 0. Exit");
                System.out.print("Choice: ");
                int choice = readInt();
                if (choice == 1) doLogin();
                else if (choice == 2) doRegister();
                else if (choice == 0) running = false;
            } else {
                System.out.println("\nLogged in: " + loggedInUser.getName() + " [" + loggedInUser.getRole() + "]");
                System.out.println("1. View Recommendations | 2. Browse Catalog | 3. Enroll in Course | 4. Analytics | 0. Logout");
                System.out.print("Choice: ");
                int choice = readInt();
                switch (choice) {
                    case 1 -> showRecommendations();
                    case 2 -> showCatalog();
                    case 3 -> doEnroll();
                    case 4 -> System.out.println(reportService.generateSummaryReport());
                    case 0 -> loggedInUser = null;
                }
            }
        }
        System.out.println("Goodbye!");
    }

    private void doLogin() {
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        try {
            loggedInUser = userService.login(email, pass);
            System.out.println("✔ Welcome, " + loggedInUser.getName() + "!");
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void doRegister() {
        System.out.print("Name: "); String name = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        try {
            loggedInUser = userService.registerRegularUser(name, email, pass);
            System.out.println("✔ Registered successfully: ID #" + loggedInUser.getUserId());
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void showRecommendations() {
        System.out.println("\n--- Recommended Courses ---");
        List<Item> recs = recommendationService.getRecommendations(loggedInUser);
        for (Item item : recs) {
            System.out.printf("  [#%d] %s (₹%.0f) - Rating: %.1f★\n", item.getItemId(), item.getTitle(), item.getPrice(), item.getAverageRating());
        }
    }

    private void showCatalog() {
        System.out.println("\n--- Course Catalog ---");
        for (Item item : catalogService.getAvailableItems()) {
            System.out.printf("  [#%d] %-30s | %-12s | ₹%.0f\n", item.getItemId(), item.getTitle(), item.getCategory(), item.getPrice());
        }
    }

    private void doEnroll() {
        System.out.print("Enter Course ID to Enroll: ");
        int cid = readInt();
        try {
            interactionService.enroll(loggedInUser.getUserId(), cid);
            System.out.println("✔ Successfully enrolled in course #" + cid);
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) { return -1; }
    }
}
