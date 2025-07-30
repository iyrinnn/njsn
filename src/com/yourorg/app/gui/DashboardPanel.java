// ====================================================================================
// Package: com.yourorg.app.gui
// File: DashboardPanel.java
// Description: The main dashboard panel displaying summary information and quick actions.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.model.TimeLog; // <-- ADD THIS LINE
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;

    private JLabel welcomeLabel;
    private JLabel dueResourcesCountLabel;
    private JLabel totalCoursesCountLabel;
    private JLabel lastReviewDateLabel;
    private JPanel coursesListPanel; // For displaying a few recent/important courses
    private JPanel dueResourcesPanel; // For displaying a few immediate due resources

    public DashboardPanel(DataManager dataManager, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 248, 255)); // Light background
        setBorder(new EmptyBorder(30, 30, 30, 30));

        initComponents();
        refreshData(); // Initial data load
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        welcomeLabel = new JLabel("Welcome back, Robert!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton addCourseButton = new JButton("Add New Course");
        addCourseButton.setFont(new Font("Arial", Font.BOLD, 16));
        addCourseButton.setBackground(new Color(106, 90, 205)); // Purple
        addCourseButton.setForeground(Color.WHITE);
        addCourseButton.setFocusPainted(false);
        addCourseButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addCourseButton.addActionListener(e -> mainFrame.showAddCourseDialog());
        headerPanel.add(addCourseButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content (Summary Cards and Lists) ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS)); // Vertical stacking
        mainContent.setOpaque(false);

        // Summary Cards Panel
        JPanel summaryCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // 1 row, 3 columns, 20px horizontal gap
        summaryCardsPanel.setOpaque(false);
        summaryCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryCardsPanel.add(createSummaryCard("Resources Due Today", "0", new Color(255, 160, 122), true)); // Salmon
        summaryCardsPanel.add(createSummaryCard("Total Courses", "0", new Color(135, 206, 250), false)); // Light Sky Blue
        summaryCardsPanel.add(createSummaryCard("Last Review", "N/A", new Color(144, 238, 144), false)); // Light Green

        mainContent.add(summaryCardsPanel);
        mainContent.add(Box.createVerticalStrut(30)); // Spacer

        // --- Courses Section ---
        JPanel coursesSection = new JPanel(new BorderLayout());
        coursesSection.setOpaque(false);
        coursesSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel coursesHeader = new JLabel("My Courses");
        coursesHeader.setFont(new Font("Arial", Font.BOLD, 22));
        coursesSection.add(coursesHeader, BorderLayout.NORTH);

        coursesListPanel = new JPanel();
        coursesListPanel.setLayout(new BoxLayout(coursesListPanel, BoxLayout.Y_AXIS)); // Stack courses vertically
        coursesListPanel.setBackground(Color.WHITE);
        coursesListPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        JScrollPane coursesScrollPane = new JScrollPane(coursesListPanel);
        coursesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        coursesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        coursesScrollPane.setPreferredSize(new Dimension(800, 200)); // Fixed size for dashboard display
        coursesSection.add(coursesScrollPane, BorderLayout.CENTER);

        mainContent.add(coursesSection);
        mainContent.add(Box.createVerticalStrut(30)); // Spacer


        // --- Due Resources Section ---
        JPanel dueResourcesSection = new JPanel(new BorderLayout());
        dueResourcesSection.setOpaque(false);
        dueResourcesSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dueResourcesHeader = new JLabel("Resources Due Soon");
        dueResourcesHeader.setFont(new Font("Arial", Font.BOLD, 22));
        dueResourcesSection.add(dueResourcesHeader, BorderLayout.NORTH);

        dueResourcesPanel = new JPanel();
        dueResourcesPanel.setLayout(new BoxLayout(dueResourcesPanel, BoxLayout.Y_AXIS)); // Stack resources vertically
        dueResourcesPanel.setBackground(Color.WHITE);
        dueResourcesPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        JScrollPane dueResourcesScrollPane = new JScrollPane(dueResourcesPanel);
        dueResourcesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dueResourcesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dueResourcesScrollPane.setPreferredSize(new Dimension(800, 200)); // Fixed size for dashboard display
        dueResourcesSection.add(dueResourcesScrollPane, BorderLayout.CENTER);

        mainContent.add(dueResourcesSection);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String title, String value, Color bgColor, boolean isClickableForReview) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1), // Subtle border
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(isClickableForReview ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 40));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);

        // Store references to the labels that will be updated
        if (title.equals("Resources Due Today")) {
            this.dueResourcesCountLabel = valueLabel;
        } else if (title.equals("Total Courses")) {
            this.totalCoursesCountLabel = valueLabel;
        } else if (title.equals("Last Review")) {
            this.lastReviewDateLabel = valueLabel;
        }

        if (isClickableForReview) {
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (dataManager.getDueResourceCount() > 0) {
                        mainFrame.showReviewSessionPanel();
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "No resources currently due for review. Keep up the good work!", "No Reviews", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }
        return card;
    }


    public void refreshData() {
        // Update summary cards
        int dueCount = dataManager.getDueResourceCount();
        dueResourcesCountLabel.setText(String.valueOf(dueCount));

        totalCoursesCountLabel.setText(String.valueOf(dataManager.getAllCourses().size()));

        // Find last review date from all TimeLogs
        dataManager.getAllTimeLogs().stream()
                .map(TimeLog::getDate) // Use getDate() from TimeLog
                .max(LocalDate::compareTo)
                .ifPresentOrElse(
                        lastDate -> lastReviewDateLabel.setText(lastDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
                        () -> lastReviewDateLabel.setText("N/A")
                );

        // Populate Courses List (top 5 or 10 most recent/relevant)
        coursesListPanel.removeAll();
        List<Course> allCourses = dataManager.getAllCourses();
        // Sort courses if desired (e.g., by name, or by last modified, or by number of resources)
        allCourses.sort(Comparator.comparing(Course::getName)); // Example: sort by name
        int coursesToShow = Math.min(allCourses.size(), 5); // Show top 5 courses

        if (allCourses.isEmpty()) {
            coursesListPanel.add(createEmptyStateLabel("No courses added yet. Click 'Add New Course' to begin!"));
        } else {
            for (int i = 0; i < coursesToShow; i++) {
                Course course = allCourses.get(i);
                coursesListPanel.add(createCoursePanel(course));
                if (i < coursesToShow - 1) {
                    coursesListPanel.add(Box.createVerticalStrut(5)); // Spacer between courses
                }
            }
        }
        coursesListPanel.revalidate();
        coursesListPanel.repaint();

        // Populate Due Resources List (top 5 due soonest)
        dueResourcesPanel.removeAll();
        List<Resource> dueResources = dataManager.getDueResources(LocalDate.now());
        dueResources.sort(Comparator.comparing(Resource::getNextReviewDate)); // Sort by soonest due
        int resourcesToShow = Math.min(dueResources.size(), 5); // Show top 5 resources

        if (dueResources.isEmpty()) {
            dueResourcesPanel.add(createEmptyStateLabel("No resources due for review. Great job!"));
        } else {
            // Add a header for the list if there are items
            // Removed explicit header as it's handled by the section title now.

            for (int i = 0; i < resourcesToShow; i++) {
                Resource resource = dueResources.get(i);
                // Find parent course for display
                Course parentCourse = dataManager.getCourseForResource(resource);
                dueResourcesPanel.add(createDueResourcePanel(resource, parentCourse));
                if (i < resourcesToShow - 1) {
                    dueResourcesPanel.add(Box.createVerticalStrut(5)); // Spacer
                }
            }
        }
        dueResourcesPanel.revalidate();
        dueResourcesPanel.repaint();
    }

    private JLabel createEmptyStateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(50, 0, 50, 0)); // Padding
        return label;
    }

    private JPanel createCoursePanel(Course course) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Fixed height

        JLabel courseName = new JLabel("<html><b>" + course.getName() + "</b></html>");
        courseName.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(courseName, BorderLayout.WEST);

        JLabel resourceCount = new JLabel(course.getResources().size() + " resources");
        resourceCount.setFont(new Font("Arial", Font.PLAIN, 12));
        resourceCount.setForeground(Color.GRAY);
        panel.add(resourceCount, BorderLayout.CENTER); // Will align left in Center

        JButton viewButton = new JButton("View");
        viewButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewButton.setBackground(new Color(106, 90, 205));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        viewButton.addActionListener(e -> mainFrame.showCourseDetail(course));
        panel.add(viewButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createDueResourcePanel(Resource resource, Course parentCourse) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(255, 250, 240)); // Floral White
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Fixed height

        JLabel resourceTitle = new JLabel("<html><b>" + resource.getTitle() + "</b></html>");
        resourceTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(resourceTitle, BorderLayout.WEST);

        String courseName = (parentCourse != null) ? parentCourse.getName() : "Unknown Course";
        JLabel dueDateAndCourse = new JLabel(String.format("Due: %s | Course: %s",
                resource.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd")),
                courseName));
        dueDateAndCourse.setFont(new Font("Arial", Font.PLAIN, 12));
        dueDateAndCourse.setForeground(Color.DARK_GRAY);
        panel.add(dueDateAndCourse, BorderLayout.CENTER);

        JButton reviewButton = new JButton("Review");
        reviewButton.setFont(new Font("Arial", Font.BOLD, 12));
        reviewButton.setBackground(new Color(255, 160, 122)); // Salmon
        reviewButton.setForeground(Color.WHITE);
        reviewButton.setFocusPainted(false);
        reviewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        reviewButton.addActionListener(e -> {
            // Ideally, you'd start a review session for THIS specific resource
            // For now, starting the general review session as per MainApplicationFrame
            mainFrame.showReviewSessionPanel();
        });
        panel.add(reviewButton, BorderLayout.EAST);

        return panel;
    }
}