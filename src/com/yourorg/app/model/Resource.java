// ====================================================================================
// Package: com.yourorg.app.model
// File: Resource.java
// Description: Represents a single learning resource (e.g., a lecture, a note, a video)
//              with properties for spaced repetition.
// ====================================================================================
package com.yourorg.app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private ResourceType type;
    private String content; // Could be a URL, file path, or plain text notes

    // Spaced Repetition System (SRS) properties
    private LocalDate lastReviewedDate;
    private LocalDate nextReviewDate;
    private int reviewIntervalDays; // The interval in days for the next review
    private double easeFactor; // How "easy" the resource is (used by SRS algorithm)

    public enum ResourceType {
        LINK, NOTE, VIDEO, PDF, IMAGE, OTHER
    }

    public Resource(String title, String description, ResourceType type, String content) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.type = type;
        this.content = content;

        // Initialize SRS properties for a new resource
        this.lastReviewedDate = null; // No review yet
        this.nextReviewDate = LocalDate.now(); // Due for review immediately
        this.reviewIntervalDays = 0; // Initial interval
        this.easeFactor = 2.5; // Default ease factor (common in SM-2 algorithm)
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ResourceType getType() { return type; }
    public String getContent() { return content; }

    public LocalDate getLastReviewedDate() { return lastReviewedDate; }
    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public int getReviewIntervalDays() { return reviewIntervalDays; }
    public double getEaseFactor() { return easeFactor; }

    // --- Setters (Crucial for the fix) ---
    // You might choose not to have setters for all fields (like ID)
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setType(ResourceType type) { this.type = type; }
    public void setContent(String content) { this.content = content; }

    public void setLastReviewedDate(LocalDate lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
    }

    public void setNextReviewDate(LocalDate nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public void setReviewIntervalDays(int reviewIntervalDays) {
        this.reviewIntervalDays = reviewIntervalDays;
    }

    public void setEaseFactor(double easeFactor) {
        this.easeFactor = easeFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", lastReviewedDate=" + lastReviewedDate +
                ", nextReviewDate=" + nextReviewDate +
                ", reviewIntervalDays=" + reviewIntervalDays +
                ", easeFactor=" + easeFactor +
                '}';
    }
}