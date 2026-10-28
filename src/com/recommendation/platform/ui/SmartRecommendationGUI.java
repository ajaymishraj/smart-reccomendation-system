package com.recommendation.platform.ui;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Swing GUI for Smart Recommendation Service Platform (BCSE301).
 * Features user switching, live catalog, algorithmic recommendations, and enrollment tracking.
 */
public class SmartRecommendationGUI extends JFrame {

    private final UserService userService;
    private final CatalogService catalogService;
    private final InteractionService interactionService;
    private final RecommendationService recommendationService;
    private final ReportService reportService;

    private User currentUser;
    private JComboBox<String> userCombo;
    private JTable catalogTable;
    private DefaultTableModel catalogModel;
    private JTable recTable;
    private DefaultTableModel recModel;
    private JTextArea analyticsArea;

    public SmartRecommendationGUI(UserService us, CatalogService cs,
                                  InteractionService is, RecommendationService rs, ReportService rep) {
        this.userService = us;
        this.catalogService = cs;
        this.interactionService = is;
        this.recommendationService = rs;
        this.reportService = rep;
        this.currentUser = us.findById(3);

        initUI();
        refreshData();
    }

    private void initUI() {
        setTitle("Smart Recommendation Platform | Capstone #256");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // ── Top Bar ──────────────────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topPanel.setBackground(new Color(30, 41, 59));

        JLabel titleLabel = new JLabel("⚡ Recommendation Engine");
        titleLabel.setForeground(new Color(56, 189, 248));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        topPanel.add(titleLabel);

        topPanel.add(new JLabel("Active User:"));
        userCombo = new JComboBox<>();
        for (User u : userService.getAllUsers()) {
            userCombo.addItem(u.getUserId() + ": " + u.getName() + " (" + u.getRole() + ")");
        }
        userCombo.addActionListener(e -> onUserChanged());
        topPanel.add(userCombo);

        JButton enrollBtn = new JButton("Enroll Selected Course");
        enrollBtn.addActionListener(e -> onEnroll());
        topPanel.add(enrollBtn);

        JButton refreshBtn = new JButton("Refresh Recommendations");
        refreshBtn.addActionListener(e -> refreshData());
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        // ── Main Tabs ────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();

        // Tab 1: Recommendations
        recModel = new DefaultTableModel(new String[]{"ID", "Recommended Course", "Category", "Price (₹)", "Rating"}, 0);
        recTable = new JTable(recModel);
        tabs.addTab("🎯 Recommendations", new JScrollPane(recTable));

        // Tab 2: Full Catalog
        catalogModel = new DefaultTableModel(new String[]{"ID", "Course Title", "Category", "Price (₹)", "Rating"}, 0);
        catalogTable = new JTable(catalogModel);
        tabs.addTab("📚 Course Catalog", new JScrollPane(catalogTable));

        // Tab 3: Analytics Summary
        analyticsArea = new JTextArea();
        analyticsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        analyticsArea.setEditable(false);
        tabs.addTab("📊 Platform Analytics", new JScrollPane(analyticsArea));

        add(tabs, BorderLayout.CENTER);
    }

    private void onUserChanged() {
        int idx = userCombo.getSelectedIndex() + 1;
        try {
            User u = userService.findById(idx);
            if (u != null) {
                currentUser = u;
                refreshData();
            }
        } catch (Exception ignored) {}
    }

    private void onEnroll() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course from the Catalog tab.", "Select Course", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int courseId = (int) catalogModel.getValueAt(selectedRow, 0);
        try {
            interactionService.enroll(currentUser.getUserId(), courseId);
            JOptionPane.showMessageDialog(this, "Enrolled " + currentUser.getName() + " in Course ID #" + courseId, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Enrollment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        // Refresh Catalog
        catalogModel.setRowCount(0);
        for (Item item : catalogService.getAvailableItems()) {
            catalogModel.addRow(new Object[]{item.getItemId(), item.getTitle(), item.getCategory(), item.getPrice(), item.getAverageRating()});
        }

        // Refresh Recommendations
        recModel.setRowCount(0);
        if (currentUser != null) {
            List<Item> recs = recommendationService.getRecommendations(currentUser);
            for (Item item : recs) {
                recModel.addRow(new Object[]{item.getItemId(), item.getTitle(), item.getCategory(), item.getPrice(), item.getAverageRating()});
            }
        }

        // Refresh Analytics
        analyticsArea.setText(reportService.generateSummaryReport());
    }
}
