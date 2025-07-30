// ====================================================================================
// Package: com.yourorg.app.gui
// File: CoursesListPage.java
// Description: A panel displaying a list of all courses, allowing navigation to course details.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class CoursesListPage extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private JPanel coursesContainerPanel; // Panel to hold individual course cards

    public CoursesListPage(DataManager dataManager, MainApplicationFrame mainFrame) {
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
        JLabel panelTitle = new JLabel("All Courses");
        panelTitle.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(panelTitle, BorderLayout.WEST);

        JButton addCourseButton = new JButton("Add New Course");
        addCourseButton.setFont(new Font("Arial", Font.BOLD, 16));
        addCourseButton.setBackground(new Color(106, 90, 205)); // Purple
        addCourseButton.setForeground(Color.WHITE);
        addCourseButton.setFocusPainted(false);
        addCourseButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addCourseButton.addActionListener(e -> mainFrame.showAddCourseDialog());
        headerPanel.add(addCourseButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Courses List Area ---
        coursesContainerPanel = new JPanel();
        coursesContainerPanel.setLayout(new BoxLayout(coursesContainerPanel, BoxLayout.Y_AXIS)); // Stack courses vertically
        coursesContainerPanel.setOpaque(false); // Let the scroll pane background show

        JScrollPane scrollPane = new JScrollPane(coursesContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Border for the scroll pane itself
        scrollPane.getViewport().setBackground(Color.WHITE); // Background for the viewport

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        coursesContainerPanel.removeAll(); // Clear existing course cards

        List<Course> allCourses = dataManager.getAllCourses();
        allCourses.sort(Comparator.comparing(Course::getName)); // Sort alphabetically by name

        if (allCourses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("No courses added yet. Click 'Add New Course' to get started!", SwingConstants.CENTER);
            noCoursesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noCoursesLabel.setForeground(Color.GRAY);
            noCoursesLabel.setBorder(new EmptyBorder(100, 0, 100, 0));
            coursesContainerPanel.add(noCoursesLabel);
        } else {
            for (Course course : allCourses) {
                coursesContainerPanel.add(createCourseCard(course));
                coursesContainerPanel.add(Box.createVerticalStrut(10)); // Space between cards
            }
        }
        coursesContainerPanel.revalidate();
        coursesContainerPanel.repaint();
    }

    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Fixed height
        card.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left in BoxLayout

        // Left section: Course Name and Description
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel courseNameLabel = new JLabel("<html><b>" + course.getName() + "</b></html>");
        courseNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        courseNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(courseNameLabel);

        JLabel courseDescriptionLabel = new JLabel("<html><p style='width: 400px;'>" +
                (course.getDescription().isEmpty() ? "No description provided." : course.getDescription()) +
                "</p></html>");
        courseDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        courseDescriptionLabel.setForeground(Color.DARK_GRAY);
        courseDescriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(courseDescriptionLabel);

        card.add(textPanel, BorderLayout.WEST);

        // Right section: Resource Count and View Button
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        infoPanel.setOpaque(false);

        JLabel resourceCountLabel = new JLabel(course.getResources().size() + " Resources");
        resourceCountLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        resourceCountLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(resourceCountLabel);

        JButton viewButton = new JButton("View Course");
        viewButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewButton.setBackground(new Color(106, 90, 205)); // Purple
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        viewButton.addActionListener(e -> mainFrame.showCourseDetail(course));
        infoPanel.add(viewButton);

        card.add(infoPanel, BorderLayout.EAST);

        return card;
    }
}