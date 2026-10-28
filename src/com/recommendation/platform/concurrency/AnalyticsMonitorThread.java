package com.recommendation.platform.concurrency;

import com.recommendation.platform.service.CatalogService;
import com.recommendation.platform.service.InteractionService;
import com.recommendation.platform.service.UserService;

/**
 * Unit 4 Requirement: Daemon Threads, Synchronization, Thread Priorities
 * Background monitor thread that periodically calculates platform metrics.
 */
public class AnalyticsMonitorThread extends Thread {

    private final UserService userService;
    private final CatalogService catalogService;
    private final InteractionService interactionService;
    private volatile boolean running = true;

    // Synchronized metrics cache
    private static final Object lock = new Object();
    private static int cachedTotalUsers = 0;
    private static int cachedTotalCourses = 0;
    private static int cachedTotalEnrollments = 0;

    public AnalyticsMonitorThread(UserService us, CatalogService cs, InteractionService is) {
        super("Analytics-Monitor-Daemon");
        this.userService = us;
        this.catalogService = cs;
        this.interactionService = is;
        // Mark as daemon thread so JVM can exit cleanly
        setDaemon(true);
        // Set lower priority for background telemetry
        setPriority(Thread.NORM_PRIORITY - 1);
    }

    @Override
    public void run() {
        System.out.println("[Daemon: " + getName() + "] Started background analytics telemetry.");
        while (running) {
            try {
                updateMetrics();
                Thread.sleep(10000); // Poll every 10 seconds
            } catch (InterruptedException e) {
                System.out.println("[Daemon: " + getName() + "] Telemetry interrupted.");
                break;
            }
        }
    }

    private void updateMetrics() {
        int uCount = userService.getTotalUsers();
        int cCount = catalogService.getTotalItems();
        int eCount = interactionService.getTotalEnrollments();

        // Synchronization block to ensure thread-safe state publication
        synchronized (lock) {
            cachedTotalUsers = uCount;
            cachedTotalCourses = cCount;
            cachedTotalEnrollments = eCount;
        }
    }

    public static String getQuickStats() {
        synchronized (lock) {
            return String.format("Users: %d | Courses: %d | Enrollments: %d",
                    cachedTotalUsers, cachedTotalCourses, cachedTotalEnrollments);
        }
    }

    public void stopMonitoring() {
        this.running = false;
        interrupt();
    }
}
