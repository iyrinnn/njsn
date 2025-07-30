package com.yourorg.app.gui;

import com.yourorg.app.model.Resource;

import javax.swing.*;
import java.awt.*;

public class ResourceFormDialog extends JDialog {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<Resource.ResourceType> typeComboBox;
    private JTextField contentField; // For URL/FilePath/Text
    private Resource resource; // The resource being edited (null if new)
    private boolean resourceSaved = false;

    public ResourceFormDialog(Frame owner, Resource resourceToEdit) {
        super(owner, true); // Modal dialog
        this.resource = resourceToEdit;
        setTitle(resourceToEdit == null ? "Add New Resource" : "Edit Resource");
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        initComponents();
        populateFields();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField(25);
        formPanel.add(titleField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane);

        formPanel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(Resource.ResourceType.values());
        formPanel.add(typeComboBox);

        formPanel.add(new JLabel("Content (URL/Path/Text):"));
        contentField = new JTextField(25);
        formPanel.add(contentField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveResource());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (resource != null) {
            titleField.setText(resource.getTitle());
            descriptionArea.setText(resource.getDescription());
            typeComboBox.setSelectedItem(resource.getType());
            contentField.setText(resource.getContent());
        }
    }

    private void saveResource() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Resource.ResourceType type = (Resource.ResourceType) typeComboBox.getSelectedItem();
        String content = contentField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (resource == null) { // New resource
            resource = new Resource(title, description, type, content);
        } else { // Editing existing resource
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setType(type);
            resource.setContent(content);
        }
        resourceSaved = true;
        dispose(); // Close the dialog
    }

    public boolean isResourceSaved() {
        return resourceSaved;
    }

    public Resource getResource() {
        return resource;
    }
}