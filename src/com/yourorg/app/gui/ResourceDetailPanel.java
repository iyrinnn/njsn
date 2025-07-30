// ====================================================================================
// Package: com.yourorg.app.gui
// File: ResourceDetailPanel.java
// Description: Displays the detailed information of a single resource.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.model.TimeLog;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceDetailPanel extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Resource currentResource;
    private Course parentCourse; // To know which course this resource belongs to

    // UI Components
    private JLabel titleLabel;
    private JLabel typeLabel;
    private JLabel courseLabel;
    private JTextArea descriptionArea;
    private JEditorPane contentPathPane;
    private JLabel nextReviewDateLabel;
    private JPanel reviewHistoryPanel;

    public ResourceDetailPanel(DataManager dataManager, MainApplicationFrame mainFrame, Resource resource, Course parentCourse) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.currentResource = resource;
        this.parentCourse = parentCourse;

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 248, 255));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton backButton = new JButton("← Back to Course");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(106, 90, 205));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.addActionListener(e -> {
            if (parentCourse != null) {
                mainFrame.showCourseDetail(parentCourse);
            } else {
                mainFrame.showDashboard();
            }
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton editButton = new JButton("Edit Resource");
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setBackground(new Color(60, 179, 113));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        editButton.addActionListener(e -> mainFrame.showAddEditResourceDialog(currentResource, parentCourse));
        actionButtons.add(editButton);

        JButton deleteButton = new JButton("Delete Resource");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        deleteButton.addActionListener(e -> deleteResource());
        actionButtons.add(deleteButton);

        headerPanel.add(actionButtons, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Resource Title");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        courseLabel = new JLabel("Course: Loading...");
        courseLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        courseLabel.setForeground(Color.DARK_GRAY);
        courseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(courseLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Details Panel (Type, Next Review Date)
        JPanel detailInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        detailInfoPanel.setOpaque(false);
        detailInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        typeLabel = new JLabel("Type: ");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        detailInfoPanel.add(typeLabel);

        nextReviewDateLabel = new JLabel("Next Review: ");
        nextReviewDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        detailInfoPanel.add(nextReviewDateLabel);
        contentPanel.add(detailInfoPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Description
        JLabel descHeader = new JLabel("Description:");
        descHeader.setFont(new Font("Arial", Font.BOLD, 16));
        descHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descHeader);
        contentPanel.add(Box.createVerticalStrut(5));

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBackground(new Color(250, 250, 255));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(600, 100));
        descScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        descScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descScrollPane);
        contentPanel.add(Box.createVerticalStrut(20));

        // Content Path/Link
        JLabel contentHeader = new JLabel("Content:");
        contentHeader.setFont(new Font("Arial", Font.BOLD, 16));
        contentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(contentHeader);
        contentPanel.add(Box.createVerticalStrut(5));

        contentPathPane = new JEditorPane();
        contentPathPane.setEditable(false);
        contentPathPane.setContentType("text/html");
        contentPathPane.setBackground(new Color(250, 250, 255));
        contentPathPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        contentPathPane.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not open link: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JScrollPane contentScrollPane = new JScrollPane(contentPathPane);
        contentScrollPane.setPreferredSize(new Dimension(600, 80));
        contentScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        contentScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(contentScrollPane);
        contentPanel.add(Box.createVerticalStrut(20));


        // Review History
        JLabel historyHeader = new JLabel("Review History:");
        historyHeader.setFont(new Font("Arial", Font.BOLD, 16));
        historyHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(historyHeader);
        contentPanel.add(Box.createVerticalStrut(5));

        reviewHistoryPanel = new JPanel();
        reviewHistoryPanel.setLayout(new BoxLayout(reviewHistoryPanel, BoxLayout.Y_AXIS));
        reviewHistoryPanel.setBackground(new Color(250, 250, 255));
        reviewHistoryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane historyScrollPane = new JScrollPane(reviewHistoryPanel);
        historyScrollPane.setPreferredSize(new Dimension(600, 120));
        historyScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        historyScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(historyScrollPane);


        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // --- Bottom Buttons (Start Review) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomPanel.setOpaque(false);

        JButton startReviewButton = new JButton("Start Review Session");
        startReviewButton.setFont(new Font("Arial", Font.BOLD, 16));
        startReviewButton.setBackground(new Color(106, 90, 205));
        startReviewButton.setForeground(Color.WHITE);
        startReviewButton.setFocusPainted(false);
        startReviewButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        startReviewButton.addActionListener(e -> mainFrame.showReviewSessionPanel());
        bottomPanel.add(startReviewButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        // Fetch the latest resource object to ensure it's up-to-date
        Resource updatedResource = dataManager.getResourceById(currentResource.getId());
        if (updatedResource == null) {
            JOptionPane.showMessageDialog(this, "Resource not found or has been deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            if (parentCourse != null) {
                mainFrame.showCourseDetail(parentCourse); // Go back to parent course
            } else {
                mainFrame.showDashboard(); // Fallback
            }
            return;
        }
        this.currentResource = updatedResource; // Update the reference

        titleLabel.setText(currentResource.getTitle());
        typeLabel.setText("Type: " + currentResource.getType().name());
        descriptionArea.setText(currentResource.getDescription());
        descriptionArea.setCaretPosition(0);

        String contentHtml = "";
        String resourceContent = currentResource.getContent(); // Use getContent()
        if (resourceContent != null && !resourceContent.trim().isEmpty()) {
            switch (currentResource.getType()) {
                case LINK:
                case VIDEO:
                    contentHtml = "<html><a href='" + resourceContent + "'>" + resourceContent + "</a></html>";
                    break;
                case PDF:
                    // For local PDF files, enable direct Browse
                    contentHtml = "<html><a href='file://" + resourceContent + "'>" + resourceContent + "</a><br><i>(Click to open locally)</i></html>";
                    break;
                case IMAGE:
                    // For images, embed it if it's a local file, or link it
                    contentHtml = "<html><img src='file://" + resourceContent + "' width='400'><br><i>(Path: " + resourceContent + ")</i></html>";
                    break;
                case NOTE:
                case OTHER:
                default:
                    contentHtml = "<html>" + resourceContent + "</html>";
                    break;
            }
        } else {
            contentHtml = "<i>No content path provided.</i>";
        }
        contentPathPane.setText(contentHtml);
        contentPathPane.setCaretPosition(0);

        String nextReviewDateText = currentResource.getNextReviewDate() != null ?
                currentResource.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A (Not yet reviewed)";
        nextReviewDateLabel.setText("Next Review: " + nextReviewDateText);

        // Update parent course label
        if (this.parentCourse == null) { // If parentCourse wasn't set on creation or got lost
            this.parentCourse = dataManager.getCourseForResource(currentResource);
        }
        if (parentCourse != null) {
            courseLabel.setText("Course: " + parentCourse.getName());
        } else {
            courseLabel.setText("Course: Unknown (Resource might be orphaned)");
        }

        populateReviewHistory();
    }

    private void populateReviewHistory() {
        reviewHistoryPanel.removeAll();

        List<TimeLog> resourceLogs = dataManager.getTimeLogsForResource(currentResource.getId());
        resourceLogs.sort(Comparator.comparing(TimeLog::getDate).reversed()); // Most recent first

        if (resourceLogs.isEmpty()) {
            JLabel noHistoryLabel = new JLabel("No review history for this resource.");
            noHistoryLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            noHistoryLabel.setForeground(Color.GRAY);
            reviewHistoryPanel.add(noHistoryLabel);
        } else {
            for (TimeLog log : resourceLogs) {
                JPanel logEntry = new JPanel(new FlowLayout(FlowLayout.LEFT));
                logEntry.setOpaque(false);
                JLabel dateLabel = new JLabel(log.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + ": ");
                dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
                JLabel durationLabel = new JLabel(String.format("%d mins", log.getDurationMinutes())); // Use getDurationMinutes
                durationLabel.setFont(new Font("Arial", Font.PLAIN, 12));

                logEntry.add(dateLabel);
                logEntry.add(durationLabel);
                reviewHistoryPanel.add(logEntry);
            }
        }
        reviewHistoryPanel.revalidate();
        reviewHistoryPanel.repaint();
    }

    private void deleteResource() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete resource '" + currentResource.getTitle() + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dataManager.deleteResource(currentResource.getId())) { // This handles removal from course and logs
                JOptionPane.showMessageDialog(this, "Resource deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (parentCourse != null) {
                    mainFrame.showCourseDetail(parentCourse); // Go back to the parent course detail
                } else {
                    mainFrame.showDashboard(); // Fallback if parent course is unknown
                }
                mainFrame.refreshCourseList(); // Refresh any lists that might display resources
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete resource.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to update the displayed resource if an edit happened in a dialog
    // This is called by MainApplicationFrame after AddEditResourceDialog closes
    public void setResource(Resource updatedResource) {
        this.currentResource = updatedResource;
        refreshData();
    }
}