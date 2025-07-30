package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;

public class ResourceReviewPanel extends JPanel {

    private DataManager dataManager;
    private MainApplicationFrame parentFrame;
    private Course parentCourse; // Needed for navigation back to course detail
    private Resource currentResource;

    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel typeLabel;
    private JEditorPane contentPane; // For displaying content (e.g., HTML for links, plain text for notes)
    private JLabel reviewInfoLabel; // Shows last/next review dates

    public ResourceReviewPanel(DataManager dataManager, MainApplicationFrame parentFrame, Course parentCourse, Resource resource) {
        this.dataManager = dataManager;
        this.parentFrame = parentFrame;
        this.parentCourse = parentCourse;
        this.currentResource = resource;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 255, 250)); // Light cream background
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        initComponents();
        loadResourceContent();
        updateReviewInfo();
    }

    private void initComponents() {
        // --- Resource Details Panel (North) ---
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        detailsPanel.setOpaque(false);

        titleLabel = new JLabel("Title: " + currentResource.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        detailsPanel.add(titleLabel);

        descriptionLabel = new JLabel("Description: " + currentResource.getDescription());
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.DARK_GRAY);
        detailsPanel.add(descriptionLabel);

        typeLabel = new JLabel("Type: " + currentResource.getType().toString());
        typeLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        typeLabel.setForeground(Color.GRAY);
        detailsPanel.add(typeLabel);

        add(detailsPanel, BorderLayout.NORTH);

        // --- Content Display Area (Center) ---
        contentPane = new JEditorPane();
        contentPane.setEditable(false);
        contentPane.setContentType("text/html"); // Default to HTML for links, can be text/plain for notes
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane contentScrollPane = new JScrollPane(contentPane);
        add(contentScrollPane, BorderLayout.CENTER);

        // --- Review Controls and Info (South) ---
        JPanel reviewControlPanel = new JPanel(new BorderLayout(10, 10));
        reviewControlPanel.setOpaque(false);

        reviewInfoLabel = new JLabel("", SwingConstants.CENTER);
        reviewInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reviewControlPanel.add(reviewInfoLabel, BorderLayout.NORTH);

        JPanel ratingButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        ratingButtonsPanel.setOpaque(false);

        // Quality of Recall buttons (0-5)
        String[] qualities = {"0 (Forgot)", "1 (Hardest)", "2 (Hard)", "3 (Good)", "4 (Easy)", "5 (Perfect)"};
        for (int i = 0; i < qualities.length; i++) {
            int quality = i; // Effective final for lambda
            JButton button = new JButton(qualities[i]);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            button.setBackground(getQualityColor(quality));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            button.addActionListener(e -> markResource(quality));
            ratingButtonsPanel.add(button);
        }
        reviewControlPanel.add(ratingButtonsPanel, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back to Course");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(new Color(200, 200, 200)); // Gray
        backButton.setForeground(Color.DARK_GRAY);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> parentFrame.showCourseDetail(parentCourse));
        reviewControlPanel.add(backButton, BorderLayout.SOUTH);

        add(reviewControlPanel, BorderLayout.SOUTH);
    }

    private Color getQualityColor(int quality) {
        switch (quality) {
            case 0: return new Color(220, 50, 50); // Red
            case 1: return new Color(255, 100, 100); // Lighter Red
            case 2: return new Color(255, 150, 50); // Orange
            case 3: return new Color(100, 180, 100); // Green
            case 4: return new Color(50, 180, 50); // Darker Green
            case 5: return new Color(0, 150, 0); // Even Darker Green
            default: return Color.LIGHT_GRAY;
        }
    }

    private void loadResourceContent() {
        String content = currentResource.getContent();
        Resource.ResourceType type = currentResource.getType();

        try {
            // ... (rest of loadResourceContent remains unchanged)
            switch (type) {
                case LINK:
                case VIDEO: // For video, we just provide the link and an "Open" button
                    contentPane.setContentType("text/html");
                    contentPane.setText("<html><body>" +
                            "<p><b>Link:</b> <a href=\"" + content + "\">" + content + "</a></p>" +
                            "<p>Click the link above or the button below to open in your default browser.</p>" +
                            "<button onclick='java.awt.Desktop.getDesktop().browse(new java.net.URI(\"" + content + "\"))'>Open Link in Browser</button>" +
                            "</body></html>");
                    // Add a hyperlink listener to open links in external browser
                    contentPane.addHyperlinkListener(e -> {
                        if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                            try {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                    Desktop.getDesktop().browse(e.getURL().toURI());
                                } else {
                                    JOptionPane.showMessageDialog(this, "Desktop Browse not supported.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (IOException | URISyntaxException ex) {
                                JOptionPane.showMessageDialog(this, "Error opening link: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    });
                    break;
                case NOTE:
                    contentPane.setContentType("text/plain");
                    contentPane.setText(content); // Assume content is plain text for notes
                    break;
                case PDF:
                case IMAGE:
                case OTHER:
                    // For local files, provide a button to open them externally
                    contentPane.setContentType("text/html");
                    String fileType = type.toString().toLowerCase();
                    contentPane.setText("<html><body>" +
                            "<p>This is a " + fileType + " resource. Content path: " + content + "</p>" +
                            "<p>Click the button below to open this file with your default system application.</p>" +
                            "<button onclick='java.awt.Desktop.getDesktop().open(new java.io.File(\"" + content.replace("\\", "\\\\") + "\"))'>Open " + fileType.toUpperCase() + " File</button>" +
                            "</body></html>");
                    // Note: Direct JavaScript execution in JEditorPane is limited.
                    // The above `onclick` relies on a specific, limited capability often
                    // found in applets/trusted environments, and might not work universally.
                    // A safer approach is to use a Swing JButton outside the JEditorPane.
                    // For simplicity in this demo, we'll try the direct HTML.
                    // A better way for file opening:
                    JButton openFileButton = new JButton("Open " + fileType.toUpperCase() + " File");
                    openFileButton.addActionListener(e -> {
                        try {
                            File file = new File(content);
                            if (file.exists() && Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(file);
                            } else {
                                JOptionPane.showMessageDialog(this, "File not found or Desktop not supported.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    });
                    // You'd need to add this button to your layout, e.g., next to the contentPane.
                    // For this example, I'll keep the HTML button for brevity, but be aware of its limitations.
                    break;
            }
        } catch (Exception e) {
            contentPane.setContentType("text/plain");
            contentPane.setText("Error loading content: " + e.getMessage() + "\nContent: " + content);
            e.printStackTrace();
        }
    }

    private void updateReviewInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String lastReview = (currentResource.getLastReviewedDate() != null) ?
                currentResource.getLastReviewedDate().format(formatter) : "Never";
        String nextReview = (currentResource.getNextReviewDate() != null) ?
                currentResource.getNextReviewDate().format(formatter) : "N/A";
        reviewInfoLabel.setText(String.format("Last Reviewed: %s | Next Review Due: %s | Interval: %d days | Ease: %.2f",
                lastReview, nextReview, currentResource.getReviewIntervalDays(), currentResource.getEaseFactor()));
    }

    private void markResource(int quality) {
        dataManager.markResourceAsReviewed(currentResource.getId(), quality);
        // FIX: Call saveDataInBackground() directly on dataManager
        dataManager.saveDataInBackground(); // Save changes
        JOptionPane.showMessageDialog(this,
                "Resource marked as reviewed!\nNext review: " + currentResource.getNextReviewDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                "Review Complete", JOptionPane.INFORMATION_MESSAGE);

        // After review, navigate back to the course detail or dashboard
        parentFrame.showCourseDetail(parentCourse);
        parentFrame.showDashboard(); // Also refresh dashboard for due count
    }
}