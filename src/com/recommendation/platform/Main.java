package com.recommendation.platform;

import com.recommendation.platform.concurrency.AnalyticsMonitorThread;
import com.recommendation.platform.network.WebServer;
import com.recommendation.platform.service.*;
import com.recommendation.platform.ui.ConsoleUI;
import com.recommendation.platform.ui.SmartRecommendationGUI;
import com.recommendation.platform.util.DataSeeder;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.GraphicsEnvironment;

/**
 * ════════════════════════════════════════════════════════════
 *   Smart Recommendation Service Management Platform
 *   Project #256 | OOT Capstone | Sem III | NIET Greater Noida
 *   SDG 9: Industry, Innovation & Infrastructure
 * ════════════════════════════════════════════════════════════
 *
 *  Full Unit 1–5 Java Features Demonstrated:
 *  ✔ Unit 1: Classes, Objects, Constructors, Methods, Encapsulation, CLI
 *  ✔ Unit 2: Inheritance, Abstraction, Polymorphism, Interfaces, Streams & Lambdas
 *  ✔ Unit 3: Packages, Custom Exception Handling, String Handling (StringBuilder/Buffer)
 *  ✔ Unit 4: Multithreading (Daemon & Worker Threads, Synchronization, Priorities),
 *            File I/O (CSV export), Sockets & Networking (Embedded HTTP Server & REST API)
 *  ✔ Unit 5: Modern Swing GUI (Tabs, Real-time Search, Dialogs, Tables, Metrics),
 *            Collections (List, Map, Set, LinkedHashSet), Generics (Repository, ApiResponse)
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Wire up services ───────────────────────────────────────────────
        UserService        userService        = new UserService();
        CatalogService     catalogService     = new CatalogService();
        InteractionService interactionService = new InteractionService(userService, catalogService);
        RecommendationService recommendSvc    = new RecommendationService(catalogService, interactionService);
        ReportService      reportService      = new ReportService(userService, catalogService, interactionService);

        // ── 2. Seed demo data ─────────────────────────────────────────────────
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("   Smart Recommendation Service Management Platform (v2.0)  ");
        System.out.println("   NIET Greater Noida | Capstone Project #256 | SDG 9       ");
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("Initialising platform...");
        DataSeeder.seed(userService, catalogService, interactionService);

        // ── 3. Start Concurrency Daemon Thread (Unit 4) ───────────────────────
        AnalyticsMonitorThread monitorThread = new AnalyticsMonitorThread(userService, catalogService, interactionService);
        monitorThread.start();

        // ── 4. Start Embedded Web & Socket Server (Unit 4) ────────────────────
        WebServer webServer = new WebServer(8080, userService, catalogService,
                                            interactionService, recommendSvc, reportService);
        webServer.start();

        System.out.println("\nDemo Accounts Pre-loaded:");
        System.out.println("  • admin@platform.com  / admin123  (Platform Administrator)");
        System.out.println("  • bob@platform.com    / mgr123    (Content Manager)");
        System.out.println("  • raj@example.com     / pass123   (Student - Programming & AI)");
        System.out.println("  • priya@example.com   / pass123   (Student - Data Science)");
        System.out.println("  • arjun@example.com   / pass123   (Student - Web & Cloud)\n");

        // ── 5. Launch UI (Unit 5 GUI vs Console) ──────────────────────────────
        boolean forceConsole = false;
        for (String arg : args) {
            if ("--console".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                forceConsole = true;
                break;
            }
        }

        boolean isHeadless = GraphicsEnvironment.isHeadless();

        if (!forceConsole && !isHeadless) {
            System.out.println("🖥 Launching Modern Desktop GUI (Swing)...");
            try {
                // Set system look and feel if available
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                SmartRecommendationGUI gui = new SmartRecommendationGUI(
                        userService, catalogService, interactionService, recommendSvc, reportService
                );
                gui.setVisible(true);
            });
        } else {
            System.out.println("⌨ Launching Console UI (Text-based mode)...");
            ConsoleUI ui = new ConsoleUI(userService, catalogService,
                                         interactionService, recommendSvc, reportService);
            ui.start();
        }
    }
}
