// ====================================================================================
// Package: com.yourorg.app.gui
// File: ReviewSessionPanel.java
// Description: A panel for conducting review sessions for due resources.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.model.TimeLog;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class ReviewSessionPanel extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;

    private List<Resource> resourcesToReview;
    private int currentIndex = 0;

    private JLabel resourceTitleLabel;
    private JEditorPane resourceContentArea;
    private JLabel resourceInfoLabel;

    private JButton prevButton;
    private JButton nextButton;
    private JButton markReviewedButton;
    private JButton exitReviewButton;

    // Components for quality of recall input
    private JPanel qualityPanel;
    private JRadioButton[] qualityButtons;
    private ButtonGroup qualityButtonGroup;

    public ReviewSessionPanel(DataManager dataManager, MainApplicationFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 248, 255));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initializeReviewSession(); // Method to fetch and prepare resources
    }

    private void initializeReviewSession() {
        List<Resource> dueResources = dataManager.getDueResources(LocalDate.now());
        this.resourcesToReview = new ArrayList<>(dueResources);
        Collections.sort(this.resourcesToReview, Comparator
                .comparing(Resource::getNextReviewDate)
                .thenComparing(Resource::getTitle));

        if (this.resourcesToReview.isEmpty()) {
            displayNoReviewsMessage();
        } else {
            removeAll(); // Clear existing "no reviews" message if any
            initComponents();
            displayCurrentResource();
        }
    }


    private void displayNoReviewsMessage() {
        removeAll();
        setLayout(new GridBagLayout());
        JLabel noReviewsLabel = new JLabel("No resources due for review today. Keep up the great work!");
        noReviewsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        noReviewsLabel.setForeground(new Color(100, 100, 100));
        add(noReviewsLabel, new GridBagConstraints());

        JButton backToDashboardButton = new JButton("Back to Dashboard");
        backToDashboardButton.setFont(new Font("Arial", Font.BOLD, 14));
        backToDashboardButton.setBackground(new Color(106, 90, 205));
        backToDashboardButton.setForeground(Color.WHITE);
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backToDashboardButton.addActionListener(e -> mainFrame.showDashboard());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        add(backToDashboardButton, gbc);

        revalidate();
        repaint();
    }


    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel panelTitle = new JLabel("Review Session");
        panelTitle.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(panelTitle, BorderLayout.WEST);

        exitReviewButton = new JButton("Exit Review");
        exitReviewButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitReviewButton.setBackground(new Color(220, 20, 60));
        exitReviewButton.setForeground(Color.WHITE);
        exitReviewButton.setFocusPainted(false);
        exitReviewButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        exitReviewButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the review session? (Progress for current session will be lost)",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.showDashboard();
            }
        });
        headerPanel.add(exitReviewButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area (Resource Display) ---
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        resourceTitleLabel = new JLabel("Resource Title");
        resourceTitleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        contentPanel.add(resourceTitleLabel, BorderLayout.NORTH);

        resourceContentArea = new JEditorPane();
        resourceContentArea.setContentType("text/html");
        resourceContentArea.setEditable(false);
        resourceContentArea.setBackground(new Color(250, 250, 255));
        resourceContentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        JScrollPane scrollPane = new JScrollPane(resourceContentArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        resourceInfoLabel = new JLabel("Type: | Course: | Due: ");
        resourceInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        resourceInfoLabel.setForeground(Color.GRAY);
        contentPanel.add(resourceInfoLabel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);


        // --- Navigation and Action Buttons (Bottom) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10)); // Use BorderLayout for overall bottom
        bottomPanel.setOpaque(false);

        // Quality of Recall Panel
        qualityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        qualityPanel.setOpaque(false);
        qualityPanel.setBorder(BorderFactory.createTitledBorder("How well did you recall this?"));
        String[] qualityLabels = {"0 (Blackout)", "1 (Incorrect, remembered after)", "2 (Incorrect, easy to remember)", "3 (Correct, with difficulty)", "4 (Correct, easy)", "5 (Perfect recall)"};
        qualityButtonGroup = new ButtonGroup();
        qualityButtons = new JRadioButton[6];
        for (int i = 0; i < qualityLabels.length; i++) {
            qualityButtons[i] = new JRadioButton(qualityLabels[i]);
            qualityButtons[i].setFont(new Font("Arial", Font.PLAIN, 12));
            qualityButtons[i].setOpaque(false);
            qualityButtons[i].setActionCommand(String.valueOf(i)); // Store the quality score
            qualityButtonGroup.add(qualityButtons[i]);
            qualityPanel.add(qualityButtons[i]);
        }
        bottomPanel.add(qualityPanel, BorderLayout.NORTH);

        // Navigation Buttons
        JPanel navigationButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        navigationButtonsPanel.setOpaque(false);

        prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Arial", Font.BOLD, 14));
        prevButton.setBackground(new Color(150, 150, 150));
        prevButton.setForeground(Color.WHITE);
        prevButton.setFocusPainted(false);
        prevButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        prevButton.addActionListener(e -> navigateResource(-1));
        navigationButtonsPanel.add(prevButton);

        markReviewedButton = new JButton("Mark as Reviewed"); // Text updated
        markReviewedButton.setFont(new Font("Arial", Font.BOLD, 14));
        markReviewedButton.setBackground(new Color(60, 179, 113));
        markReviewedButton.setForeground(Color.WHITE);
        markReviewedButton.setFocusPainted(false);
        markReviewedButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        markReviewedButton.addActionListener(e -> markCurrentResourceReviewed());
        navigationButtonsPanel.add(markReviewedButton);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(106, 90, 205));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        nextButton.addActionListener(e -> navigateResource(1));
        navigationButtonsPanel.add(nextButton);

        bottomPanel.add(navigationButtonsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void displayCurrentResource() {
        if (resourcesToReview.isEmpty()) {
            displayNoReviewsMessage();
            return;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        } else if (currentIndex >= resourcesToReview.size()) {
            currentIndex = resourcesToReview.size() - 1;
        }

        Resource currentResource = resourcesToReview.get(currentIndex);
        resourceTitleLabel.setText(currentResource.getTitle());

        String content = "<html><body>";
        String resourceContent = currentResource.getContent(); // Use getContent()
        if (resourceContent == null || resourceContent.trim().isEmpty()) {
            content += "<i>No content provided for this resource.</i><br>";
        } else {
            switch (currentResource.getType()) {
                case NOTE:
                    content += "<h3>Notes:</h3>" + resourceContent;
                    break;
                case VIDEO:
                    content += "<h3>Video Link:</h3> <a href='" + resourceContent + "'>" + resourceContent + "</a><br><i>(Open in browser to view)</i>";
                    break;
                case LINK:
                    content += "<h3>Web Link:</h3> <a href='" + resourceContent + "'>" + resourceContent + "</a><br><i>(Open in browser)</i>";
                    break;
                case PDF:
                    content += "<h3>PDF Document:</h3> <a href='file://" + resourceContent + "'>" + resourceContent + "</a><br><i>(Requires local PDF viewer)</i>";
                    break;
                case IMAGE:
                    content += "<h3>Image:</h3> <img src='file://" + resourceContent + "' width='400'><br><i>(Path: " + resourceContent + ")</i>";
                    break;
                default:
                    content += "<h3>Content:</h3>" + resourceContent;
                    break;
            }
        }
        content += "</body></html>";
        resourceContentArea.setText(content);
        resourceContentArea.setCaretPosition(0);

        // Find parent course for resource display
        String courseName = "N/A";
        Course parentCourse = dataManager.getCourseForResource(currentResource);
        if (parentCourse != null) {
            courseName = parentCourse.getName();
        }

        String nextReviewDate = currentResource.getNextReviewDate() != null ?
                currentResource.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A";

        resourceInfoLabel.setText(String.format("Type: %s | Course: %s | Current Due Date: %s | Resource %d of %d",
                currentResource.getType().name(), courseName, nextReviewDate, currentIndex + 1, resourcesToReview.size()));

        // Reset quality selection for the new resource
        qualityButtonGroup.clearSelection();

        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < resourcesToReview.size() - 1);
        markReviewedButton.setEnabled(!resourcesToReview.isEmpty());
        // Enable quality buttons only if there are resources
        for (JRadioButton button : qualityButtons) {
            button.setEnabled(!resourcesToReview.isEmpty());
        }
    }

    private void navigateResource(int direction) {
        currentIndex += direction;
        displayCurrentResource();
    }

    private void markCurrentResourceReviewed() {
        if (resourcesToReview.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No resource to mark as reviewed.", "No Resource", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get selected quality from radio buttons
        ButtonModel selectedModel = qualityButtonGroup.getSelection();
        if (selectedModel == null) {
            JOptionPane.showMessageDialog(this, "Please select how well you recalled the resource.", "Recall Quality Needed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qualityOfRecall = Integer.parseInt(selectedModel.getActionCommand());

        Resource currentResource = resourcesToReview.get(currentIndex);

        // FIX START: Use dataManager.markResourceAsReviewed
        dataManager.markResourceAsReviewed(currentResource.getId(), qualityOfRecall);
        // The dataManager.markResourceAsReviewed method already updates the resource's SRS properties
        // AND records a TimeLog, and calls saveDataInBackground().
        // So the two lines below are now redundant and should be removed.
        // dataManager.recordTimeLog(new TimeLog(currentResource.getId(), 5 * 60, LocalDate.now())); // REDUNDANT
        // dataManager.updateResource(currentResource); // REDUNDANT
        // FIX END

        JOptionPane.showMessageDialog(this,
                "'" + currentResource.getTitle() + "' marked as reviewed. Next review: " +
                        currentResource.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                "Review Complete", JOptionPane.INFORMATION_MESSAGE);

        // Remove the current resource from the list to review in this session
        resourcesToReview.remove(currentIndex);

        if (resourcesToReview.isEmpty()) {
            displayNoReviewsMessage();
        } else {
            // Adjust currentIndex if necessary, then display next resource
            if (currentIndex >= resourcesToReview.size()) {
                currentIndex = resourcesToReview.size() - 1;
            }
            displayCurrentResource();
        }

        mainFrame.refreshCourseList(); // Refresh dashboard/other views that might show due counts
    }

    /**
     * Call this method to refresh the panel if review data might have changed
     * (e.g., after loading new data).
     */
    public void refreshData() {
        // Re-initialize the session to get fresh due resources
        initializeReviewSession();
    }
}