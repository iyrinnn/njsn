// ====================================================================================
// Package: com.yourorg.app.gui
// File: AddEditCourseDialog.java
// Description: A dialog for adding new courses or editing existing ones.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.util.UUID; // No longer explicitly needed here as Course handles it

public class AddEditCourseDialog extends JDialog {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course courseToEdit; // Will be null if adding a new course
    private boolean courseSaved = false;
    private Course savedCourse; // To return the saved/updated course

    private JTextField courseNameField;
    private JTextArea courseDescriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    public AddEditCourseDialog(MainApplicationFrame mainFrame, DataManager dataManager, Course courseToEdit) {
        super(mainFrame, true); // Modal dialog
        this.mainFrame = mainFrame;
        this.dataManager = dataManager;
        this.courseToEdit = courseToEdit;

        if (courseToEdit == null) {
            setTitle("Add New Course");
        } else {
            setTitle("Edit Course: " + courseToEdit.getName());
        }

        setSize(500, 350);
        setLocationRelativeTo(mainFrame); // Center relative to main frame
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Close on X button

        initComponents();
        populateFields();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 248, 255)); // Light background

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Make it transparent to show mainPanel background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally

        // Course Name Label and Field
        JLabel nameLabel = new JLabel("Course Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameLabel, gbc);

        courseNameField = new JTextField(25);
        courseNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        courseNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Allow it to expand horizontally
        formPanel.add(courseNameField, gbc);

        // Course Description Label and Area
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align to top-left for JTextArea
        formPanel.add(descriptionLabel, gbc);

        courseDescriptionArea = new JTextArea(5, 25);
        courseDescriptionArea.setWrapStyleWord(true);
        courseDescriptionArea.setLineWrap(true);
        courseDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        courseDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(courseDescriptionArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0; // Allow it to expand vertically
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        saveButton = new JButton("Save Course");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(106, 90, 205)); // Purple
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCourse();
            }
        });
        buttonPanel.add(saveButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 220, 220)); // Light gray
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void populateFields() {
        if (courseToEdit != null) {
            courseNameField.setText(courseToEdit.getName());
            courseDescriptionArea.setText(courseToEdit.getDescription());
        }
    }

    private void saveCourse() {
        String name = courseNameField.getText().trim();
        String description = courseDescriptionArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (courseToEdit == null) {
            // Add new course
            // CORRECTED LINE: Use the constructor that takes only name and description
            Course newCourse = new Course(name, description);
            dataManager.addCourse(newCourse);
            savedCourse = newCourse;
            JOptionPane.showMessageDialog(this, "Course '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Edit existing course
            courseToEdit.setName(name);
            courseToEdit.setDescription(description);
            // In a real app, you might have a dataManager.updateCourse(courseToEdit)
            // For now, since DataManager stores by reference, modifying the object is enough
            // but we'll simulate an update call.
            dataManager.updateCourse(courseToEdit); // Assuming this method exists or is handled internally
            savedCourse = courseToEdit;
            JOptionPane.showMessageDialog(this, "Course '" + name + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        courseSaved = true;
        mainFrame.refreshCourseList(); // Notify main frame to refresh course displays
        dispose(); // Close the dialog
    }

    public boolean isCourseSaved() {
        return courseSaved;
    }

    public Course getSavedCourse() {
        return savedCourse;
    }
}