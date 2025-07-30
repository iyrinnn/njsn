// ====================================================================================
// Package: com.yourorg.app.service
// File: DataManager.java
// Description: Manages the application's data (courses, resources, time logs)
//              including persistence (saving/loading to/from file).
// ====================================================================================
package com.yourorg.app.service;

import com.yourorg.app.model.Course;
import com.yourorg.app.model.Resource;
import com.yourorg.app.model.TimeLog;

import javax.swing.SwingWorker; // <--- ADD THIS IMPORT

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataManager {

    private List<Course> courses;
    private List<TimeLog> timeLogs;
    private final String DATA_FILE = "app_data.ser"; // Serialization file

    public DataManager() {
        this.courses = new ArrayList<>();
        this.timeLogs = new ArrayList<>();
        loadData(); // Attempt to load data on initialization
    }

    // --- Persistence Methods ---
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(courses);
            oos.writeObject(timeLogs);
            System.out.println("Data saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked") // Suppress warning for unchecked cast from readObject
    public void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists() && file.length() > 0) { // Check if file exists and is not empty
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                courses = (List<Course>) ois.readObject();
                timeLogs = (List<TimeLog>) ois.readObject();
                System.out.println("Data loaded successfully from " + DATA_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data: " + e.getMessage());
                e.printStackTrace();
                // Optionally, clear existing data if load fails to prevent corrupted state
                this.courses = new ArrayList<>();
                this.timeLogs = new ArrayList<>();
            }
        } else {
            System.out.println("No existing data file found or file is empty. Starting with fresh data.");
        }
    }

    /**
     * Initiates data saving in a background thread using SwingWorker.
     * This prevents the UI from freezing during the save operation.
     */
    public void saveDataInBackground() {
        // SwingWorker<ReturnTypeOfDoInBackground, TypeOfPublishProgress>
        // Void is used because saveData() doesn't return anything.
        // Void is used for progress as we're not reporting incremental progress.
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // This code runs on a background thread
                System.out.println("Saving data in background...");
                saveData(); // Call the synchronous save method
                return null; // No return value needed
            }

            @Override
            protected void done() {
                // This code runs back on the Event Dispatch Thread (EDT)
                // after doInBackground() completes (either successfully or with an error).
                try {
                    get(); // Check for exceptions that occurred during doInBackground()
                    System.out.println("Background save complete.");
                } catch (Exception e) {
                    System.err.println("Error during background save: " + e.getMessage());
                    e.printStackTrace();
                    // You might want to show a JOptionPane here, but be careful with
                    // showing UI from within DataManager; usually, the UI component
                    // calling this would handle error display.
                }
            }
        }.execute(); // Start the SwingWorker
    }


    // --- Course Management ---

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses); // Return a copy to prevent external modification of the internal list
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
            saveDataInBackground(); // Changed to background save
        }
    }

    public Course getCourseById(String id) {
        return courses.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean updateCourse(Course updatedCourse) {
        if (updatedCourse == null) return false;
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(updatedCourse.getId())) {
                courses.set(i, updatedCourse);
                saveDataInBackground(); // Changed to background save
                return true;
            }
        }
        return false;
    }

    public boolean deleteCourse(String courseId) {
        boolean removed = courses.removeIf(c -> c.getId().equals(courseId));
        if (removed) {
            // Also remove all resources and time logs associated with this course
            // This is a simplistic approach; a more robust solution might handle orphaned resources differently
            List<String> resourceIdsToRemove = new ArrayList<>();
            getAllResources().stream()
                    .filter(r -> getCourseForResource(r) != null && getCourseForResource(r).getId().equals(courseId))
                    .forEach(r -> resourceIdsToRemove.add(r.getId()));

            for(String resId : resourceIdsToRemove) {
                deleteResource(resId); // This will remove resource and its logs
            }
            saveDataInBackground(); // Changed to background save
        }
        return removed;
    }

    // --- Resource Management (through Courses) ---

    // Get all resources from all courses
    public List<Resource> getAllResources() {
        return courses.stream()
                .flatMap(course -> course.getResources().stream())
                .collect(Collectors.toList());
    }

    // Get resources for a specific course
    public List<Resource> getResourcesForCourse(String courseId) {
        Course course = getCourseById(courseId);
        return course != null ? new ArrayList<>(course.getResources()) : new ArrayList<>();
    }

    public Resource getResourceById(String resourceId) {
        return getAllResources().stream()
                .filter(r -> r.getId().equals(resourceId))
                .findFirst()
                .orElse(null);
    }

    // Important: Adding a resource implies adding it to a specific course
    public boolean addResourceToCourse(String courseId, Resource resource) {
        Course course = getCourseById(courseId);
        if (course != null && resource != null) {
            course.addResource(resource);
            saveDataInBackground(); // Changed to background save
            return true;
        }
        return false;
    }

    // Important: Updating a resource must update it within its parent course's list
    public boolean updateResource(Resource updatedResource) {
        if (updatedResource == null) return false;
        for (Course course : courses) {
            if (course.updateResource(updatedResource)) { // Course's updateResource handles finding by ID
                saveDataInBackground(); // Changed to background save
                return true;
            }
        }
        return false;
    }

    public boolean deleteResource(String resourceId) {
        boolean deletedFromAnyCourse = false;
        for (Course course : courses) {
            if (course.removeResource(resourceId)) {
                deletedFromAnyCourse = true;
                break; // Resource found and removed from this course
            }
        }
        if (deletedFromAnyCourse) {
            // Also remove associated time logs
            timeLogs.removeIf(log -> log.getResourceId().equals(resourceId));
            saveDataInBackground(); // Changed to background save
            return true;
        }
        return false;
    }

    // Utility to get the parent course of a resource
    public Course getCourseForResource(Resource resource) {
        if (resource == null) return null;
        return courses.stream()
                .filter(c -> c.getResources().contains(resource))
                .findFirst()
                .orElse(null);
    }


    // --- TimeLog Management ---

    public List<TimeLog> getAllTimeLogs() {
        return new ArrayList<>(timeLogs); // Return a copy
    }

    public void recordTimeLog(TimeLog log) {
        if (log != null) {
            timeLogs.add(log);
            saveDataInBackground(); // Changed to background save
        }
    }

    public List<TimeLog> getTimeLogsForResource(String resourceId) {
        return timeLogs.stream()
                .filter(log -> log.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

    /**
     * Marks a resource as reviewed, updates its Spaced Repetition System (SRS) properties,
     * and logs the review time.
     * @param resourceId The ID of the resource being reviewed.
     * @param quality The quality of recall (0-5, as defined in ResourceReviewPanel).
     */
    public void markResourceAsReviewed(String resourceId, int quality) {
        Resource resource = getResourceById(resourceId); // Use existing helper
        if (resource != null) {
            // 1. Update Spaced Repetition System (SRS) properties
            // This is a simplified example; a real SRS algorithm (e.g., SM-2) would be more complex.
            double newEaseFactor = resource.getEaseFactor() + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
            if (newEaseFactor < 1.3) newEaseFactor = 1.3; // Minimum ease factor
            resource.setEaseFactor(newEaseFactor);

            int newInterval;
            if (quality < 3) { // Forgot or hard - reset interval or shorter interval
                newInterval = 1;
            } else if (resource.getReviewIntervalDays() == 0) { // First successful review
                newInterval = 1; // Example: First interval is 1 day
            } else if (resource.getReviewIntervalDays() == 1) { // Second successful review
                newInterval = 6; // Example: Second interval is 6 days
            } else {
                newInterval = (int) Math.round(resource.getReviewIntervalDays() * newEaseFactor);
            }
            resource.setReviewIntervalDays(newInterval);

            // 2. Update last reviewed date and calculate next review date
            LocalDate today = LocalDate.now();
            resource.setLastReviewedDate(today);
            resource.setNextReviewDate(today.plusDays(newInterval));

            // 3. Log the time (assuming a fixed duration for simplicity, or add a duration parameter)
            // For this example, let's just log a nominal duration (e.g., 5 minutes = 300 seconds).
            TimeLog newLog = new TimeLog(resourceId, 300, today);
            timeLogs.add(newLog);

            // Important: After updating the resource's properties, ensure the change is saved
            // Since resources are within courses, updating the resource alone won't
            // trigger a save unless the `updateResource` method is called, which
            // you already have. Let's make sure it's called.
            updateResource(resource); // Call existing updateResource to persist changes to the resource
            // This internally calls saveDataInBackground()
        } else {
            System.err.println("Error: Resource with ID " + resourceId + " not found for review.");
        }
    }


    // --- Review Session Specific Methods ---

    /**
     * Retrieves resources that are due for review on or before the given date.
     * @param dueDate The date to check against.
     * @return A list of Resource objects that are due.
     */
    public List<Resource> getDueResources(LocalDate dueDate) {
        return getAllResources().stream()
                .filter(r -> r.getNextReviewDate() != null &&
                        (r.getNextReviewDate().isBefore(dueDate) || r.getNextReviewDate().isEqual(dueDate)))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total number of due resources.
     * @return The count of resources due today or earlier.
     */
    public int getDueResourceCount() {
        return getDueResources(LocalDate.now()).size();
    }
}