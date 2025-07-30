package com.yourorg.app.gui;

import com.yourorg.app.model.Course;

import javax.swing.*;
import java.awt.*;

public class AddCourseDialog extends JDialog {

    private JTextField nameField;
    private JTextArea descriptionArea;
    private Course course; // The course being edited (null if new)
    private boolean courseAdded = false; // Renamed from isCourseSaved for semantic clarity

    public AddCourseDialog(Frame owner) {
        this(owner, null); // Constructor for adding new course
    }

    public AddCourseDialog(Frame owner, Course courseToEdit) {
        super(owner, true); // Modal dialog
        this.course = courseToEdit;
        setTitle(courseToEdit == null ? "Add New Course" : "Edit Course");
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        initComponents();
        populateFields();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Course Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveCourse());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (course != null) {
            nameField.setText(course.getName());
            descriptionArea.setText(course.getDescription());
        }
    }

    private void saveCourse() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (course == null) { // New course
            course = new Course(name, description);
        } else { // Editing existing course
            course.setName(name);
            course.setDescription(description);
        }
        courseAdded = true; // Indicate that a course was successfully added/edited
        dispose(); // Close the dialog
    }

    public boolean isCourseAdded() { // Check if the dialog resulted in a saved course
        return courseAdded;
    }

    public Course getNewCourse() { // Returns the new or updated course object
        return course;
    }
}