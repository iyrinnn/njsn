// ====================================================================================
// Package: com.yourorg.app.gui
// File: MainApplicationFrame.java
// Description: The main JFrame for the application, handling overall layout,
//             sidebar navigation, and content panel switching.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainApplicationFrame extends JFrame {

    private DataManager dataManager;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    // private DefaultListModel<Course> courseListModel; // No longer directly used for sidebar JList
    // private JList<Course> courseJList; // No longer directly used for sidebar JList
    private JButton dashboardButton;
    private JButton coursesButton;

    // References to panels that might need refresh
    private DashboardPanel dashboardPanel;
    private CoursesListPage coursesListPage; // Renamed from CourseDetailPanel.CoursesListPage

    public MainApplicationFrame(DataManager dataManager) {
        this.dataManager = dataManager;
        setTitle("Smart Revision & Resource Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        add(mainContentPanel, BorderLayout.CENTER);

        // Initialize panels and add them to the card layout
        dashboardPanel = new DashboardPanel(dataManager, this);
        mainContentPanel.add(dashboardPanel, "Dashboard");

        coursesListPage = new CoursesListPage(dataManager, this); // Use standalone CoursesListPage
        mainContentPanel.add(coursesListPage, "CoursesList");

        // Show the Dashboard initially
        cardLayout.show(mainContentPanel, "Dashboard");

        // Load data on a background thread when the frame is visible
        loadDataInBackground();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(getWidth(), 70));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel logo = new JLabel("<html><b style='color: #6A5ACD;'>alltio</b></html>");
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setBorder(new EmptyBorder(0, 20, 0, 0));
        panel.add(logo, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField(" Search", 30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        userPanel.setOpaque(false);
        userPanel.setBorder(new EmptyBorder(0, 0, 0, 20));

        JLabel notificationIcon = new JLabel(new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB)));
        notificationIcon.setToolTipText("Notifications");
        userPanel.add(notificationIcon);

        JLabel userLabel = new JLabel("Robert Fox");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel userAvatar = new JLabel(new ImageIcon(new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB)));
        userAvatar.setBorder(BorderFactory.createLineBorder(new Color(106, 90, 205), 2, true));
        userPanel.add(userAvatar);
        userPanel.add(userLabel);
        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(220, getHeight()));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));

        panel.add(Box.createVerticalStrut(20));

        dashboardButton = createSidebarButton("Dashboard", "/icons/dashboard_icon.png");
        dashboardButton.addActionListener(e -> showDashboard());
        panel.add(dashboardButton);

        panel.add(Box.createVerticalStrut(5));

        coursesButton = createSidebarButton("Courses", "/icons/courses_icon.png");
        coursesButton.addActionListener(e -> showCoursesList());
        panel.add(coursesButton);

        panel.add(createSidebarButton("Assessments", "/icons/assessments_icon.png"));
        panel.add(createSidebarButton("Classes", "/icons/classes_icon.png"));
        panel.add(createSidebarButton("Students", "/icons/students_icon.png"));
        panel.add(createSidebarButton("Calendar", "/icons/calendar_icon.png"));
        panel.add(createSidebarButton("Reports", "/icons/reports_icon.png"));

        panel.add(Box.createVerticalGlue());

        JButton settingsButton = createSidebarButton("Settings", "/icons/settings_icon.png");
        panel.add(settingsButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton("<html><b>" + text + "</b></html>");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setBorder(new EmptyBorder(0, 20, 0, 0));

        ImageIcon icon = null;
        try {
            // Check if icon exists in resources
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                icon = new ImageIcon(imgURL);
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);
            } else {
                System.err.println("Icon not found: " + iconPath + ". Using blank placeholder.");
                icon = new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
            }
        } catch (Exception e) {
            System.err.println("Icon loading error for " + iconPath + ": " + e.getMessage());
            icon = new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
        }
        button.setIcon(icon);
        button.setIconTextGap(15);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground() == Color.WHITE) { // Only change if not selected
                    button.setBackground(new Color(240, 240, 240));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(new Color(240, 240, 240))) { // Only revert if it was the hover color
                    button.setBackground(Color.WHITE);
                }
            }
        });

        return button;
    }


    // --- Navigation Methods ---

    public void showDashboard() {
        dashboardPanel.refreshData(); // Refresh the dashboard panel
        cardLayout.show(mainContentPanel, "Dashboard");
        setSidebarButtonSelection(dashboardButton);
    }

    public void showCoursesList() {
        coursesListPage.refreshData(); // Refresh the courses list panel
        cardLayout.show(mainContentPanel, "CoursesList");
        setSidebarButtonSelection(coursesButton);
    }

    private void setSidebarButtonSelection(JButton selectedButton) {
        Component[] components = selectedButton == null ? new Component[0] : selectedButton.getParent().getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button == selectedButton) {
                    button.setBackground(new Color(230, 230, 250));
                    button.setForeground(new Color(106, 90, 205));
                    button.setFont(new Font("Arial", Font.BOLD, 15));
                } else {
                    button.setBackground(Color.WHITE);
                    button.setForeground(Color.BLACK);
                    button.setFont(new Font("Arial", Font.PLAIN, 15));
                }
            }
        }
    }


    public void showCourseDetail(Course course) {
        String cardName = "CourseDetail_" + course.getId();
        CourseDetailPanel courseDetailPanel;

        // Check if panel already exists
        Component existingPanel = null;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingPanel = comp;
                break;
            }
        }

        if (existingPanel instanceof CourseDetailPanel) {
            courseDetailPanel = (CourseDetailPanel) existingPanel;
            courseDetailPanel.refreshData(); // Refresh data if panel already exists
        } else {
            // Create a new CourseDetailPanel if it doesn't exist or is not the correct type
            courseDetailPanel = new CourseDetailPanel(dataManager, this, course);
            mainContentPanel.add(courseDetailPanel, cardName);
            courseDetailPanel.setName(cardName); // Set name for future lookup
        }
        cardLayout.show(mainContentPanel, cardName);
        setSidebarButtonSelection(null); // Clear sidebar selection as we are in a sub-detail view
    }

    public void showResourceDetail(Resource resource, Course parentCourse) {
        String cardName = "ResourceDetail_" + resource.getId();
        ResourceDetailPanel resourceDetailPanel;

        Component existingPanel = null;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingPanel = comp;
                break;
            }
        }

        if (existingPanel instanceof ResourceDetailPanel) {
            resourceDetailPanel = (ResourceDetailPanel) existingPanel;
            resourceDetailPanel.setResource(resource); // Update resource and refresh
        } else {
            resourceDetailPanel = new ResourceDetailPanel(dataManager, this, resource, parentCourse);
            mainContentPanel.add(resourceDetailPanel, cardName);
            resourceDetailPanel.setName(cardName);
        }
        cardLayout.show(mainContentPanel, cardName);
        setSidebarButtonSelection(null);
    }

    public void showAddCourseDialog() {
        AddEditCourseDialog dialog = new AddEditCourseDialog(this, dataManager, null);
        dialog.setVisible(true);
        if (dialog.isCourseSaved()) {
            refreshCourseList(); // Refresh any relevant displays like dashboard and courses list
        }
    }

    public void showAddEditResourceDialog(Resource resourceToEdit, Course parentCourse) {
        AddEditResourceDialog dialog = new AddEditResourceDialog(this, dataManager, parentCourse, resourceToEdit);
        dialog.setVisible(true);
        if (dialog.isResourceSaved()) {
            // If resource was saved, refresh the parent course's view
            if (parentCourse != null) {
                showCourseDetail(parentCourse); // This will refresh the CourseDetailPanel with updated resource
            }
        }
    }

    public void showReviewSessionPanel() {
        ReviewSessionPanel reviewPanel = new ReviewSessionPanel(dataManager, this);
        String cardName = "ReviewSession";

        // Remove existing review panel to ensure a fresh session
        Component existingReviewPanel = null;
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingReviewPanel = comp;
                break;
            }
        }
        if (existingReviewPanel != null) {
            mainContentPanel.remove(existingReviewPanel);
        }

        mainContentPanel.add(reviewPanel, cardName);
        reviewPanel.setName(cardName);
        cardLayout.show(mainContentPanel, cardName);
        setSidebarButtonSelection(null); // No sidebar button active for review session
    }

    // --- Data Loading and Refreshing ---

    /**
     * This method is called to trigger refreshes on relevant panels
     * after data changes (e.g., add/edit/delete course or resource).
     */
    public void refreshCourseList() {
        dashboardPanel.refreshData(); // Refresh dashboard
        coursesListPage.refreshData(); // Refresh the full courses list page
        // Any other panels that display a list of courses or resources might need to be refreshed here.
    }

    public void loadDataInBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                dataManager.loadData(); // This should be quick enough, but in a real app,
                // loading large data sets would benefit from a progress bar.
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // This will re-throw any exceptions from doInBackground
                    refreshCourseList(); // Refresh relevant UI components after data is loaded
                    showDashboard(); // Ensure dashboard is showing and updated after load
                    // JOptionPane.showMessageDialog(MainApplicationFrame.this, // Removed for cleaner startup
                    //         "Data loaded successfully!", "Load Complete",
                    //         JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause();
                    String message = "Failed to load data.";
                    if (cause instanceof IOException) {
                        message = "Error reading data file: " + cause.getMessage();
                    } else if (cause instanceof ClassNotFoundException) {
                        message = "Data file corrupted or incompatible version: " + cause.getMessage();
                    }
                    JOptionPane.showMessageDialog(MainApplicationFrame.this,
                            message + "\nStarting with empty data.", "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}