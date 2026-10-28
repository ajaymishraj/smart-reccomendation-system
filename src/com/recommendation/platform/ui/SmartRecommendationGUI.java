package com.recommendation.platform.ui;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * Unit 5 Requirement: GUI Development using Swing
 * Modern, rich desktop Graphical User Interface for Smart Recommendation Platform.
 * Features dark theme, card layouts, real-time search, interactive tables,
 * recommendation engine testing, user switching, and analytics visualization.
 */
public class SmartRecommendationGUI extends JFrame {

    private final UserService userService;
    private final CatalogService catalogService;
    private final InteractionService interactionService;
    private final RecommendationService recommendationService;
    private final ReportService reportService;

    private User currentUser;

    // UI Components
    private JLabel userBadgeLabel;
    private JComboBox<String> userSwitcherCombo;
    private JTabbedPane tabbedPane;

    // Dashboard labels
    private JLabel statUsersLabel;
    private JLabel statCoursesLabel;
    private JLabel statEnrollmentsLabel;
    private JLabel statStrategyLabel;
    private JTextArea summaryArea;

    // Tables
    private JTable catalogTable;
    private DefaultTableModel catalogModel;
    private JTable enrollmentsTable;
    private DefaultTableModel enrollmentsModel;
    private JTable usersTable;
    private DefaultTableModel usersModel;

    // Recommendations
    private JPanel recommendationsContainer;
    private JComboBox<String> strategyCombo;

    // Theme Colors
    private static final Color BG_DARK     = new Color(24, 28, 36);
    private static final Color BG_CARD     = new Color(34, 40, 52);
    private static final Color BG_INPUT    = new Color(44, 52, 68);
    private static final Color ACCENT_BLUE = new Color(99, 102, 241);   // Indigo
    private static final Color ACCENT_GREEN= new Color(16, 185, 129);  // Emerald
    private static final Color ACCENT_WARN = new Color(245, 158, 11);  // Amber
    private static final Color TEXT_LIGHT  = new Color(243, 244, 246);
    private static final Color TEXT_MUTED  = new Color(156, 163, 175);
    private static final Color BORDER_COL  = new Color(55, 65, 81);

    public SmartRecommendationGUI(UserService us, CatalogService cs,
                                  InteractionService is, RecommendationService rs, ReportService rep) {
        this.userService           = us;
        this.catalogService        = cs;
        this.interactionService    = is;
        this.recommendationService = rs;
        this.reportService         = rep;

        // Default initial user: Student Raj (ID 3)
        this.currentUser = us.findById(3);

        initWindow();
        initComponents();
        refreshAllData();
    }

    private void initWindow() {
        setTitle("Smart Recommendation Service Management Platform | Capstone #256 [SDG 9]");
        setSize(1180, 780);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTabbedContent(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // ── Header Panel ──────────────────────────────────────────────────────────

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Brand title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("⚡ Smart Recommendation Service Platform");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_LIGHT);

        JLabel subLabel = new JLabel("Project #256 • OOT Capstone (Semester III) • UN SDG 9: Industry, Innovation & Infrastructure");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(ACCENT_GREEN);

        titlePanel.add(titleLabel);
        titlePanel.add(subLabel);
        header.add(titlePanel, BorderLayout.WEST);

        // User switcher controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JLabel switchLabel = new JLabel("Switch User:");
        switchLabel.setForeground(TEXT_MUTED);
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rightPanel.add(switchLabel);

        userSwitcherCombo = new JComboBox<>(new String[]{
                "Student Raj (Student)",
                "Student Priya (Student)",
                "Student Arjun (Student)",
                "Admin Alice (Administrator)",
                "Manager Bob (Content Manager)"
        });
        styleComboBox(userSwitcherCombo);
        userSwitcherCombo.addActionListener(e -> onUserSwitched());
        rightPanel.add(userSwitcherCombo);

        userBadgeLabel = new JLabel("Role: REGULAR");
        userBadgeLabel.setOpaque(true);
        userBadgeLabel.setBackground(ACCENT_BLUE);
        userBadgeLabel.setForeground(Color.WHITE);
        userBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userBadgeLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        rightPanel.add(userBadgeLabel);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private JTabbedPane createTabbedContent() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_LIGHT);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("  📊 Dashboard  ", createDashboardTab());
        tabbedPane.addTab("  🎯 Smart Recommendations  ", createRecommendationsTab());
        tabbedPane.addTab("  📚 Course Catalog  ", createCatalogTab());
        tabbedPane.addTab("  📋 My Enrollments  ", createEnrollmentsTab());
        tabbedPane.addTab("  ➕ Course Studio (Admin)  ", createCourseManagementTab());
        tabbedPane.addTab("  👥 User Directory (Admin)  ", createUserManagementTab());
        tabbedPane.addTab("  📈 Analytics & Reports  ", createReportsTab());

        return tabbedPane;
    }

    // ── Tab 1: Dashboard ──────────────────────────────────────────────────────

    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top KPI Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);

        statUsersLabel       = new JLabel("0", SwingConstants.CENTER);
        statCoursesLabel     = new JLabel("0", SwingConstants.CENTER);
        statEnrollmentsLabel = new JLabel("0", SwingConstants.CENTER);
        statStrategyLabel    = new JLabel("Popularity", SwingConstants.CENTER);

        statsGrid.add(createKpiCard("👥 Total Users", statUsersLabel, ACCENT_BLUE));
        statsGrid.add(createKpiCard("📚 Catalog Courses", statCoursesLabel, ACCENT_GREEN));
        statsGrid.add(createKpiCard("🎓 Active Enrollments", statEnrollmentsLabel, ACCENT_WARN));
        statsGrid.add(createKpiCard("🤖 Active Strategy", statStrategyLabel, new Color(168, 85, 247)));

        panel.add(statsGrid, BorderLayout.NORTH);

        // Center: Analytics overview & User Profile
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setOpaque(false);

        // Platform Summary card
        JPanel summaryCard = createCardPanel("📈 Platform Real-Time Telemetry");
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setBackground(BG_INPUT);
        summaryArea.setForeground(TEXT_LIGHT);
        summaryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        summaryArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        summaryCard.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        // Capstone & Architecture info card
        JPanel infoCard = createCardPanel("🏛 Architecture & SDG 9 Alignment");
        JTextArea infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setBackground(BG_INPUT);
        infoText.setForeground(TEXT_LIGHT);
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoText.setText(
                "★ SDG 9: Industry, Innovation & Infrastructure\n" +
                "This platform optimizes digital educational service delivery by bridging user interactions with multi-strategy recommendation engines.\n\n" +
                "★ Core Architecture Features:\n" +
                "• Strategy Pattern: Runtime swapping of Popularity, Content-Based, and Collaborative Filtering.\n" +
                "• Singleton Pattern: UserDAO and ItemDAO thread-safe persistence.\n" +
                "• Concurrency: Background telemetry daemon & asynchronous recommendation worker.\n" +
                "• Client-Server Sockets: Embedded HTTP Server on port 8080 with REST API.\n" +
                "• Robust Exception Handling: ItemNotFoundException and UserNotFoundException.\n" +
                "• Generics: Type-safe Repository<T, ID> and ApiResponse<T> wrapper."
        );
        infoCard.add(new JScrollPane(infoText), BorderLayout.CENTER);

        centerPanel.add(summaryCard);
        centerPanel.add(infoCard);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // ── Tab 2: Smart Recommendations ──────────────────────────────────────────

    private JPanel createRecommendationsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Control header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_CARD);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JPanel leftControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftControl.setOpaque(false);
        JLabel stratLabel = new JLabel("Recommendation Engine Strategy:");
        stratLabel.setForeground(TEXT_LIGHT);
        stratLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        leftControl.add(stratLabel);

        strategyCombo = new JComboBox<>(new String[]{
                "Popularity-Based (Views + Enrollments + Ratings)",
                "Content-Based Filtering (Category Matching & Preferences)",
                "Collaborative Filtering (User-Item Overlap & Similarity)"
        });
        styleComboBox(strategyCombo);
        leftControl.add(strategyCombo);

        JButton applyStratBtn = createStyledButton("Apply & Recalculate", ACCENT_BLUE);
        applyStratBtn.addActionListener(e -> onApplyStrategy());
        leftControl.add(applyStratBtn);

        topBar.add(leftControl, BorderLayout.WEST);

        panel.add(topBar, BorderLayout.NORTH);

        // Center card container
        recommendationsContainer = new JPanel();
        recommendationsContainer.setBackground(BG_DARK);
        recommendationsContainer.setLayout(new BoxLayout(recommendationsContainer, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(recommendationsContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Tab 3: Catalog & Search ───────────────────────────────────────────────

    private JPanel createCatalogTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search & Filter Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchBar.setBackground(BG_CARD);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel searchLbl = new JLabel("🔍 Search:");
        searchLbl.setForeground(TEXT_LIGHT);
        searchBar.add(searchLbl);

        JTextField searchField = new JTextField(18);
        styleTextField(searchField);
        searchBar.add(searchField);

        JLabel catLbl = new JLabel("Category:");
        catLbl.setForeground(TEXT_LIGHT);
        searchBar.add(catLbl);

        JComboBox<String> catFilter = new JComboBox<>();
        catFilter.addItem("ALL CATEGORIES");
        for (ItemCategory ic : ItemCategory.values()) catFilter.addItem(ic.name());
        styleComboBox(catFilter);
        searchBar.add(catFilter);

        JButton enrollSelectedBtn = createStyledButton("🎓 Enroll In Selected Course", ACCENT_GREEN);
        enrollSelectedBtn.addActionListener(e -> onEnrollSelected());
        searchBar.add(enrollSelectedBtn);

        panel.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Title", "Category", "Instructor", "Hours", "Level", "Price", "Rating", "Views", "Enrolled"};
        catalogModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        catalogTable = createStyledTable(catalogModel);

        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        // Dynamic search listener
        KeyAdapter searchFilterAction = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCatalog(searchField.getText().trim(), (String) catFilter.getSelectedItem());
            }
        };
        searchField.addKeyListener(searchFilterAction);
        catFilter.addActionListener(e -> filterCatalog(searchField.getText().trim(), (String) catFilter.getSelectedItem()));

        return panel;
    }

    // ── Tab 4: Enrollments ────────────────────────────────────────────────────

    private JPanel createEnrollmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolbar.setBackground(BG_CARD);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JButton rateBtn = createStyledButton("⭐ Rate Selected Course", ACCENT_WARN);
        rateBtn.addActionListener(e -> onRateSelected());
        toolbar.add(rateBtn);

        JButton completeBtn = createStyledButton("✔ Mark As Completed", ACCENT_GREEN);
        completeBtn.addActionListener(e -> onMarkCompleted());
        toolbar.add(completeBtn);

        panel.add(toolbar, BorderLayout.NORTH);

        // Enrollments Table
        String[] cols = {"Enrollment ID", "Course ID", "Course Title", "Category", "Status", "Progress", "Enrolled Date"};
        enrollmentsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        enrollmentsTable = createStyledTable(enrollmentsModel);

        panel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Tab 5: Course Management Studio ───────────────────────────────────────

    private JPanel createCourseManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form Card
        JPanel formCard = createCardPanel("➕ Add New Course to Catalog (Admin & Content Manager)");
        JPanel formGrid = new JPanel(new GridLayout(4, 4, 12, 10));
        formGrid.setOpaque(false);
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField();
        styleTextField(titleField);
        JTextField descField = new JTextField();
        styleTextField(descField);
        JTextField instField = new JTextField();
        styleTextField(instField);
        JTextField priceField = new JTextField("699");
        styleTextField(priceField);
        JTextField hoursField = new JTextField("30");
        styleTextField(hoursField);

        JComboBox<ItemCategory> catCombo = new JComboBox<>(ItemCategory.values());
        styleComboBox(catCombo);
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"BEGINNER", "INTERMEDIATE", "ADVANCED"});
        styleComboBox(levelCombo);

        formGrid.add(createFieldLabel("Course Title:"));
        formGrid.add(titleField);
        formGrid.add(createFieldLabel("Category:"));
        formGrid.add(catCombo);

        formGrid.add(createFieldLabel("Description:"));
        formGrid.add(descField);
        formGrid.add(createFieldLabel("Instructor Name:"));
        formGrid.add(instField);

        formGrid.add(createFieldLabel("Price (₹):"));
        formGrid.add(priceField);
        formGrid.add(createFieldLabel("Duration (Hours):"));
        formGrid.add(hoursField);

        formGrid.add(createFieldLabel("Proficiency Level:"));
        formGrid.add(levelCombo);

        JButton addBtn = createStyledButton("Save Course", ACCENT_GREEN);
        addBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String desc  = descField.getText().trim();
                String inst  = instField.getText().trim();
                if (title.isEmpty() || inst.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Instructor are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double price = Double.parseDouble(priceField.getText().trim());
                int hours    = Integer.parseInt(hoursField.getText().trim());
                ItemCategory cat = (ItemCategory) catCombo.getSelectedItem();
                String lvl   = (String) levelCombo.getSelectedItem();

                catalogService.addCourse(title, desc, cat, price, inst, hours, lvl);
                JOptionPane.showMessageDialog(this, "✔ Course successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                titleField.setText("");
                descField.setText("");
                instField.setText("");
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formGrid.add(addBtn);

        formCard.add(formGrid, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.NORTH);

        // Delete action bar
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomAction.setOpaque(false);
        JButton delBtn = createStyledButton("🗑 Remove Course by ID", new Color(239, 68, 68));
        delBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter Course ID to remove:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(input.trim());
                    catalogService.removeItem(id);
                    JOptionPane.showMessageDialog(this, "Course #" + id + " removed.");
                    refreshAllData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to remove course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        bottomAction.add(delBtn);
        panel.add(bottomAction, BorderLayout.SOUTH);

        return panel;
    }

    // ── Tab 6: User Management ────────────────────────────────────────────────

    private JPanel createUserManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolbar.setBackground(BG_CARD);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JButton toggleActiveBtn = createStyledButton("🔄 Toggle Active / Inactive", ACCENT_BLUE);
        toggleActiveBtn.addActionListener(e -> onToggleUserActive());
        toolbar.add(toggleActiveBtn);

        JButton viewReportBtn = createStyledButton("📄 View User Report", ACCENT_GREEN);
        viewReportBtn.addActionListener(e -> onViewUserReport());
        toolbar.add(viewReportBtn);

        panel.add(toolbar, BorderLayout.NORTH);

        // Users Table
        String[] cols = {"User ID", "Full Name", "Email Address", "Role", "Status", "Interactions"};
        usersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = createStyledTable(usersModel);

        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Tab 7: Reports & Analytics ────────────────────────────────────────────

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top bar for exports
        JPanel exportBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        exportBar.setBackground(BG_CARD);
        exportBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JButton expUsersBtn = createStyledButton("💾 Export Users to CSV", ACCENT_BLUE);
        expUsersBtn.addActionListener(e -> {
            String msg = reportService.exportUsersToCsv("users_export.csv");
            JOptionPane.showMessageDialog(this, msg, "Export Result", JOptionPane.INFORMATION_MESSAGE);
        });
        exportBar.add(expUsersBtn);

        JButton expCoursesBtn = createStyledButton("💾 Export Courses to CSV", ACCENT_GREEN);
        expCoursesBtn.addActionListener(e -> {
            String msg = reportService.exportCoursesToCsv("courses_export.csv");
            JOptionPane.showMessageDialog(this, msg, "Export Result", JOptionPane.INFORMATION_MESSAGE);
        });
        exportBar.add(expCoursesBtn);

        JButton refreshBtn = createStyledButton("🔄 Refresh Analytics", ACCENT_WARN);
        refreshBtn.addActionListener(e -> refreshAllData());
        exportBar.add(refreshBtn);

        panel.add(exportBar, BorderLayout.NORTH);

        // Center preview
        JTextArea repArea = new JTextArea();
        repArea.setEditable(false);
        repArea.setBackground(BG_INPUT);
        repArea.setForeground(TEXT_LIGHT);
        repArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        repArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(new JScrollPane(repArea), BorderLayout.CENTER);

        // Fill report text when shown
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 6) {
                repArea.setText(reportService.generateSummaryReport());
            }
        });

        return panel;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(8, 20, 8, 20)
        ));

        JLabel left = new JLabel("🌐 Web Server Active at: http://localhost:8080/  |  REST API & Web UI ready");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        left.setForeground(ACCENT_GREEN);

        JLabel right = new JLabel("NIET Greater Noida • Capstone Evaluation Ready");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        right.setForeground(TEXT_MUTED);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    // ── Actions & Event Handlers ──────────────────────────────────────────────

    private void onUserSwitched() {
        int idx = userSwitcherCombo.getSelectedIndex();
        int targetUserId = switch (idx) {
            case 0 -> 3; // Raj
            case 1 -> 4; // Priya
            case 2 -> 5; // Arjun
            case 3 -> 1; // Alice (Admin)
            case 4 -> 2; // Bob (Manager)
            default -> 3;
        };

        try {
            currentUser = userService.findById(targetUserId);
            userBadgeLabel.setText("Role: " + currentUser.getRole().name());
            if (currentUser.getRole() == UserRole.ADMIN) {
                userBadgeLabel.setBackground(new Color(239, 68, 68)); // Red
            } else if (currentUser.getRole() == UserRole.CONTENT_MANAGER) {
                userBadgeLabel.setBackground(ACCENT_WARN);
            } else {
                userBadgeLabel.setBackground(ACCENT_BLUE);
            }
            refreshAllData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to switch user: " + e.getMessage());
        }
    }

    private void onApplyStrategy() {
        int idx = strategyCombo.getSelectedIndex();
        switch (idx) {
            case 0 -> recommendationService.switchToPopularity();
            case 1 -> recommendationService.switchToContentBased();
            case 2 -> recommendationService.switchToCollaborative();
        }
        refreshRecommendations();
        statStrategyLabel.setText(recommendationService.getCurrentStrategy());
        JOptionPane.showMessageDialog(this, "Switched recommendation strategy to: " + recommendationService.getCurrentStrategy());
    }

    private void onEnrollSelected() {
        int row = catalogTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course row from the catalog table first.");
            return;
        }
        int courseId = (int) catalogModel.getValueAt(row, 0);
        String courseTitle = (String) catalogModel.getValueAt(row, 1);
        try {
            interactionService.enroll(currentUser.getUserId(), courseId);
            JOptionPane.showMessageDialog(this, "✔ Enrolled successfully in: " + courseTitle);
            refreshAllData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enrollment notice: " + ex.getMessage(), "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onRateSelected() {
        int row = enrollmentsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled course to rate.");
            return;
        }
        int courseId = (int) enrollmentsModel.getValueAt(row, 1);
        String courseTitle = (String) enrollmentsModel.getValueAt(row, 2);

        String[] ratings = {"5 - ⭐⭐⭐⭐⭐ Outstanding", "4 - ⭐⭐⭐⭐ Very Good", "3 - ⭐⭐⭐ Good", "2 - ⭐⭐ Fair", "1 - ⭐ Poor"};
        String choice = (String) JOptionPane.showInputDialog(this,
                "Select your star rating for:\n" + courseTitle,
                "Rate Course",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ratings,
                ratings[0]);

        if (choice != null) {
            double r = Double.parseDouble(choice.substring(0, 1));
            interactionService.rateItem(currentUser.getUserId(), courseId, r);
            JOptionPane.showMessageDialog(this, "Thank you! You rated " + courseTitle + " with " + r + " stars.");
            refreshAllData();
        }
    }

    private void onMarkCompleted() {
        int row = enrollmentsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment row.");
            return;
        }
        int courseId = (int) enrollmentsModel.getValueAt(row, 1);
        try {
            interactionService.markComplete(currentUser.getUserId(), courseId);
            JOptionPane.showMessageDialog(this, "Course marked as completed (100% progress)!");
            refreshAllData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onToggleUserActive() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user row first.");
            return;
        }
        int uid = (int) usersModel.getValueAt(row, 0);
        User u = userService.findById(uid);
        if (u.isActive()) {
            userService.deactivateUser(uid);
            JOptionPane.showMessageDialog(this, "User " + u.getName() + " is now DEACTIVATED.");
        } else {
            userService.activateUser(uid);
            JOptionPane.showMessageDialog(this, "User " + u.getName() + " is now ACTIVATED.");
        }
        refreshAllData();
    }

    private void onViewUserReport() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user row first.");
            return;
        }
        int uid = (int) usersModel.getValueAt(row, 0);
        String rep = reportService.generateUserReport(uid);
        JTextArea area = new JTextArea(rep);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "User Activity Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Refresh UI ────────────────────────────────────────────────────────────

    private void refreshAllData() {
        // KPI Counters
        statUsersLabel.setText(String.valueOf(userService.getTotalUsers()));
        statCoursesLabel.setText(String.valueOf(catalogService.getTotalItems()));
        statEnrollmentsLabel.setText(String.valueOf(interactionService.getTotalEnrollments()));
        statStrategyLabel.setText(recommendationService.getCurrentStrategy());

        // Platform Summary Area
        summaryArea.setText(reportService.generateSummaryReport());

        // Catalog Table
        refreshCatalogTable(catalogService.getAvailableItems());

        // Enrollments Table
        refreshEnrollmentsTable();

        // Users Table
        refreshUsersTable();

        // Recommendations
        refreshRecommendations();
    }

    private void refreshCatalogTable(List<Item> items) {
        catalogModel.setRowCount(0);
        for (Item item : items) {
            String inst = "Expert Faculty";
            int hours = 20;
            String level = "BEGINNER";
            int enr = 0;
            if (item instanceof Course) {
                Course c = (Course) item;
                inst = c.getInstructor();
                hours = c.getDurationHours();
                level = c.getLevel();
                enr = c.getEnrollmentCount();
            }
            catalogModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getTitle(),
                    item.getCategory().name(),
                    inst,
                    hours + "h",
                    level,
                    String.format("₹%.0f", item.getPrice()),
                    String.format("%.1f ⭐ (%d)", item.getAverageRating(), item.getTotalRatings()),
                    item.getViewCount(),
                    enr
            });
        }
    }

    private void filterCatalog(String query, String category) {
        String q = query.toLowerCase();
        boolean filterCat = category != null && !category.equals("ALL CATEGORIES");

        List<Item> filtered = catalogService.getAvailableItems().stream()
                .filter(i -> {
                    boolean matchQ = q.isEmpty() || i.getTitle().toLowerCase().contains(q) ||
                            (i instanceof Course && ((Course) i).getInstructor().toLowerCase().contains(q));
                    boolean matchCat = !filterCat || i.getCategory().name().equalsIgnoreCase(category);
                    return matchQ && matchCat;
                })
                .toList();

        refreshCatalogTable(filtered);
    }

    private void refreshEnrollmentsTable() {
        enrollmentsModel.setRowCount(0);
        List<Enrollment> enrs = interactionService.getEnrollmentsByUser(currentUser.getUserId());
        for (Enrollment e : enrs) {
            String title = "(Removed)";
            String cat = "N/A";
            try {
                Item item = catalogService.findById(e.getItemId());
                title = item.getTitle();
                cat = item.getCategory().name();
            } catch (Exception ignored) {}

            enrollmentsModel.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getItemId(),
                    title,
                    cat,
                    e.getStatus().name(),
                    e.getProgressPercent() + "%",
                    e.getEnrollDate() != null ? e.getEnrollDate().toString() : "Recent"
            });
        }
    }

    private void refreshUsersTable() {
        usersModel.setRowCount(0);
        for (User u : userService.getAllUsers()) {
            usersModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getName(),
                    u.getEmail(),
                    u.getRole().name(),
                    u.isActive() ? "ACTIVE" : "INACTIVE",
                    u.getInteractionHistory().size() + " events"
            });
        }
    }

    private void refreshRecommendations() {
        recommendationsContainer.removeAll();
        List<Item> recs = recommendationService.getRecommendations(currentUser);

        if (recs.isEmpty()) {
            JLabel emptyLbl = new JLabel("No recommendations yet. Try enrolling in courses to generate preferences!");
            emptyLbl.setForeground(TEXT_MUTED);
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setBorder(new EmptyBorder(30, 20, 20, 20));
            recommendationsContainer.add(emptyLbl);
        } else {
            for (Item item : recs) {
                recommendationsContainer.add(createRecommendationCard(item));
                recommendationsContainer.add(Box.createVerticalStrut(12));
            }
        }
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
    }

    private JPanel createRecommendationCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Left info
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setOpaque(false);

        JLabel titleLbl = new JLabel(item.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_LIGHT);

        String instructor = "Dr. Sharma";
        String level = "INTERMEDIATE";
        if (item instanceof Course) {
            instructor = ((Course) item).getInstructor();
            level = ((Course) item).getLevel();
        }

        JLabel metaLbl = new JLabel(String.format("📂 %s  •  👤 %s  •  🏷 %s  •  ⭐ %.1f (%d reviews)  •  👁 %d views",
                item.getCategory().name(), instructor, level, item.getAverageRating(), item.getTotalRatings(), item.getViewCount()));
        metaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        metaLbl.setForeground(TEXT_MUTED);

        info.add(titleLbl);
        info.add(metaLbl);
        card.add(info, BorderLayout.CENTER);

        // Right Action
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setOpaque(false);

        JLabel priceLbl = new JLabel(String.format("₹%.0f", item.getPrice()));
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLbl.setForeground(ACCENT_GREEN);
        right.add(priceLbl);

        JButton enrollBtn = createStyledButton("⚡ Quick Enroll", ACCENT_BLUE);
        enrollBtn.addActionListener(e -> {
            try {
                interactionService.enroll(currentUser.getUserId(), item.getItemId());
                JOptionPane.showMessageDialog(this, "✔ Enrolled in " + item.getTitle());
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        right.add(enrollBtn);

        card.add(right, BorderLayout.EAST);
        return card;
    }

    // ── UI Factory & Styling Helpers ──────────────────────────────────────────

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accent),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_LIGHT);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_LIGHT);
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private <T> void styleComboBox(JComboBox<T> cb) {
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_LIGHT);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBorder(new LineBorder(BORDER_COL, 1));
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(BORDER_COL);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(28, 34, 46));
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(new LineBorder(BORDER_COL, 1));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                if (!isSel) {
                    comp.setBackground(r % 2 == 0 ? BG_CARD : new Color(30, 36, 48));
                }
                setForeground(TEXT_LIGHT);
                return comp;
            }
        });

        return table;
    }
}
