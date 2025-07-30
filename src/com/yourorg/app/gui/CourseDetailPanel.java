// ====================================================================================
// Package: com.yourorg.app.gui
// File: CourseDetailPanel.java
// Description: Displays the detailed information of a single course, including its resources.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class CourseDetailPanel extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course currentCourse;

    // UI Components
    private JLabel courseNameLabel;
    private JTextArea courseDescriptionArea;
    private JPanel resourcesListPanel; // Panel to hold resource cards

    public CourseDetailPanel(DataManager dataManager, MainApplicationFrame mainFrame, Course course) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentCourse = course;

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

        JButton backButton = new JButton("← Back to Courses");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(106, 90, 205));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.addActionListener(e -> mainFrame.showCoursesList());
        headerPanel.add(backButton, BorderLayout.WEST);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton editCourseButton = new JButton("Edit Course");
        editCourseButton.setFont(new Font("Arial", Font.BOLD, 14));
        editCourseButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        editCourseButton.setForeground(Color.WHITE);
        editCourseButton.setFocusPainted(false);
        editCourseButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        editCourseButton.addActionListener(e -> showEditCourseDialog());
        actionButtons.add(editCourseButton);

        JButton deleteCourseButton = new JButton("Delete Course");
        deleteCourseButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteCourseButton.setBackground(new Color(220, 20, 60)); // Crimson
        deleteCourseButton.setForeground(Color.WHITE);
        deleteCourseButton.setFocusPainted(false);
        deleteCourseButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        deleteCourseButton.addActionListener(e -> deleteCourse());
        actionButtons.add(deleteCourseButton);

        headerPanel.add(actionButtons, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        // --- Main Content Area (Course Details & Resources) ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        courseNameLabel = new JLabel("Course Name");
        courseNameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        courseNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(courseNameLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel descHeader = new JLabel("Description:");
        descHeader.setFont(new Font("Arial", Font.BOLD, 16));
        descHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descHeader);
        contentPanel.add(Box.createVerticalStrut(5));

        courseDescriptionArea = new JTextArea();
        courseDescriptionArea.setEditable(false);
        courseDescriptionArea.setWrapStyleWord(true);
        courseDescriptionArea.setLineWrap(true);
        courseDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        courseDescriptionArea.setBackground(new Color(250, 250, 255));
        courseDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane descScrollPane = new JScrollPane(courseDescriptionArea);
        descScrollPane.setPreferredSize(new Dimension(600, 80));
        descScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        descScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descScrollPane);
        contentPanel.add(Box.createVerticalStrut(20));

        // Resources Section Header with Add Button
        JPanel resourcesHeaderPanel = new JPanel(new BorderLayout());
        resourcesHeaderPanel.setOpaque(false);
        resourcesHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel resourcesHeader = new JLabel("Resources in This Course:");
        resourcesHeader.setFont(new Font("Arial", Font.BOLD, 22));
        resourcesHeaderPanel.add(resourcesHeader, BorderLayout.WEST);

        JButton addResourceButton = new JButton("Add New Resource");
        addResourceButton.setFont(new Font("Arial", Font.BOLD, 14));
        addResourceButton.setBackground(new Color(106, 90, 205));
        addResourceButton.setForeground(Color.WHITE);
        addResourceButton.setFocusPainted(false);
        addResourceButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        addResourceButton.addActionListener(e -> mainFrame.showAddEditResourceDialog(null, currentCourse));
        resourcesHeaderPanel.add(addResourceButton, BorderLayout.EAST);
        contentPanel.add(resourcesHeaderPanel);
        contentPanel.add(Box.createVerticalStrut(10));


        // Resources List Panel
        resourcesListPanel = new JPanel();
        resourcesListPanel.setLayout(new BoxLayout(resourcesListPanel, BoxLayout.Y_AXIS)); // Stack resources vertically
        resourcesListPanel.setBackground(Color.WHITE); // Background for the list itself
        resourcesListPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane resourcesScrollPane = new JScrollPane(resourcesListPanel);
        resourcesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resourcesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resourcesScrollPane.setPreferredSize(new Dimension(600, 300)); // Allocate space
        resourcesScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(resourcesScrollPane);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER); // Wrap main content in scroll pane
    }

    public void refreshData() {
        // Fetch the latest course object from DataManager to ensure it's up-to-date
        Course updatedCourse = dataManager.getCourseById(currentCourse.getId());
        if (updatedCourse == null) {
            JOptionPane.showMessageDialog(this, "Course not found or has been deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showCoursesList(); // Go back to the courses list if current course is gone
            return;
        }
        this.currentCourse = updatedCourse; // Update the reference

        courseNameLabel.setText(currentCourse.getName());
        courseDescriptionArea.setText(currentCourse.getDescription().isEmpty() ? "No description provided." : currentCourse.getDescription());
        courseDescriptionArea.setCaretPosition(0); // Scroll to top

        populateResourcesList();
    }

    private void populateResourcesList() {
        resourcesListPanel.removeAll(); // Clear existing resource cards

        List<Resource> resources = dataManager.getResourcesForCourse(currentCourse.getId());
        resources.sort(Comparator.comparing(Resource::getTitle)); // Sort alphabetically by title

        if (resources.isEmpty()) {
            JLabel noResourcesLabel = new JLabel("No resources added to this course yet. Click 'Add New Resource' to add one.", SwingConstants.CENTER);
            noResourcesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noResourcesLabel.setForeground(Color.GRAY);
            noResourcesLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            resourcesListPanel.add(noResourcesLabel);
        } else {
            for (Resource resource : resources) {
                resourcesListPanel.add(createResourceCard(resource));
                resourcesListPanel.add(Box.createVerticalStrut(8)); // Space between cards
            }
        }
        resourcesListPanel.revalidate();
        resourcesListPanel.repaint();
    }

    private JPanel createResourceCard(Resource resource) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(new Color(240, 248, 255)); // Alice Blue
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Fixed height
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left section: Title, Type
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("<html><b>" + resource.getTitle() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);

        JLabel typeLabel = new JLabel("Type: " + resource.getType().name());
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(Color.DARK_GRAY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(typeLabel);
        card.add(textPanel, BorderLayout.WEST);

        // Center section: Description (truncated)
        JLabel descriptionLabel = new JLabel("<html><p style='width: 250px;'>" +
                (resource.getDescription().isEmpty() ? "No description." : resource.getDescription()) +
                "</p></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(Color.GRAY);
        card.add(descriptionLabel, BorderLayout.CENTER);


        // Right section: Next Review Date, Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        String nextReviewText = resource.getNextReviewDate() != null ?
                resource.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A";
        JLabel nextReviewLabel = new JLabel("Review: " + nextReviewText);
        nextReviewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nextReviewLabel.setForeground(resource.getNextReviewDate() != null && resource.getNextReviewDate().isBefore(java.time.LocalDate.now()) ?
                Color.RED.darker() : Color.BLUE.darker()); // Highlight if overdue
        buttonPanel.add(nextReviewLabel);


        JButton viewButton = new JButton("View");
        viewButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewButton.setBackground(new Color(106, 90, 205));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        viewButton.addActionListener(e -> mainFrame.showResourceDetail(resource, currentCourse));
        buttonPanel.add(viewButton);

        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void showEditCourseDialog() {
        AddEditCourseDialog dialog = new AddEditCourseDialog(mainFrame, dataManager, currentCourse);
        dialog.setVisible(true);
        if (dialog.isCourseSaved()) {
            refreshData(); // Refresh this panel after edit
            mainFrame.refreshCourseList(); // Refresh dashboard/courses list
        }
    }

    private void deleteCourse() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course '" + currentCourse.getName() + "'? This will also delete all its resources.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dataManager.deleteCourse(currentCourse.getId())) {
                JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showCoursesList(); // Go back to the main courses list
                mainFrame.refreshCourseList(); // Refresh all relevant lists
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}