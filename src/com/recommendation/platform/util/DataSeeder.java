package com.recommendation.platform.util;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;

/**
 * Seeds the platform with demo users, courses, and interactions so
 * all features can be explored immediately without manual entry.
 */
public class DataSeeder {

    public static void seed(UserService us, CatalogService cs, InteractionService is) {

        // ── USERS ─────────────────────────────────────────────────────────────
        us.registerAdmin("Admin Alice",  "admin@platform.com",  "admin123", "SUPER");
        us.registerContentManager("Manager Bob",  "bob@platform.com",   "mgr123", "Engineering");
        us.registerRegularUser("Student Raj",   "raj@example.com",    "pass123");
        us.registerRegularUser("Student Priya", "priya@example.com",  "pass123");
        us.registerRegularUser("Student Arjun", "arjun@example.com",  "pass123");

        // ── COURSES ───────────────────────────────────────────────────────────
        cs.addCourse("Java OOP Fundamentals",      "Master OOP in Java",
                     ItemCategory.PROGRAMMING,      499,  "Dr. Sharma",  30, "BEGINNER");
        cs.addCourse("Python for Data Science",    "Data analysis with Python",
                     ItemCategory.DATA_SCIENCE,     799,  "Prof. Gupta",  40, "INTERMEDIATE");
        cs.addCourse("React & Node Full Stack",    "Build modern web apps",
                     ItemCategory.WEB_DEVELOPMENT,  999,  "Ms. Singh",   50, "INTERMEDIATE");
        cs.addCourse("Machine Learning A-Z",       "Intro to ML algorithms",
                     ItemCategory.ARTIFICIAL_INTELLIGENCE, 1299, "Dr. Patel", 60, "ADVANCED");
        cs.addCourse("AWS Cloud Practitioner",     "Become cloud certified",
                     ItemCategory.CLOUD_COMPUTING,  899,  "Mr. Kumar",   25, "BEGINNER");
        cs.addCourse("Ethical Hacking Basics",     "Cybersecurity fundamentals",
                     ItemCategory.CYBERSECURITY,    799,  "Ms. Rao",     35, "BEGINNER");
        cs.addCourse("SQL & Database Design",      "Relational DB mastery",
                     ItemCategory.DATABASE,          599,  "Dr. Verma",   20, "BEGINNER");
        cs.addCourse("Deep Learning with TensorFlow","Neural networks deep dive",
                     ItemCategory.ARTIFICIAL_INTELLIGENCE, 1499, "Dr. Kapoor", 70, "ADVANCED");

        // ── INTERACTIONS (views, enrollments, ratings) ─────────────────────────
        // Raj (userId=3) — prefers Programming and AI
        safeEnroll(is, 3, 1); safeRate(is, 3, 1, 4.5);
        safeEnroll(is, 3, 4); safeRate(is, 3, 4, 5.0);
        cs.recordView(2, 3); cs.recordView(8, 3);

        // Priya (userId=4) — prefers Data Science and AI
        safeEnroll(is, 4, 2); safeRate(is, 4, 2, 4.0);
        safeEnroll(is, 4, 4); safeRate(is, 4, 4, 4.5);
        cs.recordView(7, 4); cs.recordView(1, 4);

        // Arjun (userId=5) — prefers Web Dev and Cloud
        safeEnroll(is, 5, 3); safeRate(is, 5, 3, 3.5);
        safeEnroll(is, 5, 5); safeRate(is, 5, 5, 4.0);
        cs.recordView(6, 5); cs.recordView(2, 5);

        System.out.println("✔ Demo data seeded successfully.\n");
    }

    private static void safeEnroll(InteractionService is, int uid, int iid) {
        try { is.enroll(uid, iid); } catch (Exception ignored) {}
    }

    private static void safeRate(InteractionService is, int uid, int iid, double r) {
        try { is.rateItem(uid, iid, r); } catch (Exception ignored) {}
    }
}
