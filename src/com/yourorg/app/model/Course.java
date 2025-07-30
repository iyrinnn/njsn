// ====================================================================================
// Package: com.yourorg.app.model
// File: Course.java
// Description: Represents a course/subject, containing a list of resources.
// ====================================================================================
package com.yourorg.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable for version control

    private String id;
    private String name;
    private String description;
    private List<Resource> resources; // List of resources belonging to this course

    public Course(String name, String description) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID for each new course
        this.name = name;
        this.description = description;
        this.resources = new ArrayList<>(); // Initialize an empty list to hold resources
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    // Returns an unmodifiable list to prevent external direct modification
    public List<Resource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    // --- Setters (for updating properties) ---
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    // --- Methods to manage Resources within this Course ---
    public void addResource(Resource resource) {
        if (resource != null && !this.resources.contains(resource)) { // Avoid duplicates
            this.resources.add(resource);
        }
    }

    public boolean removeResource(String resourceId) {
        // Remove resource by its ID
        return this.resources.removeIf(r -> r.getId().equals(resourceId));
    }

    public Resource getResourceById(String resourceId) {
        // Find a specific resource by its ID
        return this.resources.stream()
                .filter(r -> r.getId().equals(resourceId))
                .findFirst()
                .orElse(null); // Returns null if not found
    }

    // You can also add a method to update an existing resource
    public boolean updateResource(Resource updatedResource) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getId().equals(updatedResource.getId())) {
                resources.set(i, updatedResource);
                return true;
            }
        }
        return false;
    }

    // --- Utility Methods for Object Comparison and Debugging ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id); // Courses are considered equal if their IDs match
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash code based on the unique ID
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", resourceCount=" + resources.size() +
                '}';
    }
}
