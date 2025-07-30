// ====================================================================================
// Package: com.yourorg.app.gui
// File: AddEditResourceDialog.java
// Description: A dialog for adding new resources or editing existing ones.
// ====================================================================================
package com.yourorg.app.gui;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.model.Resource.ResourceType;
import com.yourorg.app.service.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddEditResourceDialog extends JDialog {

    private DataManager dataManager;
    private MainApplicationFrame mainFrame;
    private Course parentCourse; // The course this resource belongs to
    private Resource resourceToEdit; // Will be null if adding a new resource
    private boolean resourceSaved = false;
    private Resource savedResource;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<ResourceType> typeComboBox;
    private JTextField contentField; // For URL, file path, or brief text
    private JButton saveButton;
    private JButton cancelButton;

    public AddEditResourceDialog(MainApplicationFrame mainFrame, DataManager dataManager, Course parentCourse, Resource resourceToEdit) {
        super(mainFrame, true); // Modal dialog
        this.mainFrame = mainFrame;
        this.dataManager = dataManager;
        this.parentCourse = parentCourse;
        this.resourceToEdit = resourceToEdit;

        if (resourceToEdit == null) {
            setTitle("Add New Resource to " + parentCourse.getName());
        } else {
            setTitle("Edit Resource: " + resourceToEdit.getTitle());
        }

        setSize(600, 450);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        populateFields();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 248, 255));

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleLabel, gbc);
        titleField = new JTextField(30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(descriptionLabel, gbc);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weighty = 1.0;
        formPanel.add(descScrollPane, gbc);

        // Type
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0; // Reset weighty
        formPanel.add(typeLabel, gbc);
        typeComboBox = new JComboBox<>(ResourceType.values());
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(typeComboBox, gbc);

        // Content (Path/URL/Text)
        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(contentLabel, gbc);
        contentField = new JTextField(30);
        contentField.setFont(new Font("Arial", Font.PLAIN, 14));
        contentField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(contentField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        saveButton = new JButton("Save Resource");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(106, 90, 205));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveButton.addActionListener(e -> saveResource());
        buttonPanel.add(saveButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 220, 220));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void populateFields() {
        if (resourceToEdit != null) {
            titleField.setText(resourceToEdit.getTitle());
            descriptionArea.setText(resourceToEdit.getDescription());
            typeComboBox.setSelectedItem(resourceToEdit.getType());
            contentField.setText(resourceToEdit.getContent());
        }
    }

    private void saveResource() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        ResourceType type = (ResourceType) typeComboBox.getSelectedItem();
        String content = contentField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Resource Title cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (parentCourse == null) {
            JOptionPane.showMessageDialog(this, "Internal Error: Parent course not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (resourceToEdit == null) {
            // Add new resource
            Resource newResource = new Resource(title, description, type, content);
            if (dataManager.addResourceToCourse(parentCourse.getId(), newResource)) {
                savedResource = newResource;
                JOptionPane.showMessageDialog(this, "Resource '" + title + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add resource.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // Edit existing resource
            resourceToEdit.setTitle(title);
            resourceToEdit.setDescription(description);
            resourceToEdit.setType(type);
            resourceToEdit.setContent(content);
            if (dataManager.updateResource(resourceToEdit)) { // This updates it within its course
                savedResource = resourceToEdit;
                JOptionPane.showMessageDialog(this, "Resource '" + title + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update resource.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        resourceSaved = true;
        dispose();
    }

    public boolean isResourceSaved() {
        return resourceSaved;
    }

    public Resource getSavedResource() {
        return savedResource;
    }
}