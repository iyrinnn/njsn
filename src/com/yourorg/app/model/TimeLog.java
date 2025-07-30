package com.yourorg.app.model;

import java.io.Serializable;
import java.time.LocalDate; // Changed from LocalDateTime
import java.time.Duration; // Still useful if calculating duration

import java.util.Objects;
import java.util.UUID;

public class TimeLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String resourceId;
    private LocalDate date; // Changed from startTime/endTime to a single date for simplicity of "review completed on date"
    private long durationSeconds;

    // Constructor for when duration is known (e.g., from user input or simple calculation)
    public TimeLog(String resourceId, long durationSeconds, LocalDate date) {
        this.id = UUID.randomUUID().toString();
        this.resourceId = resourceId;
        this.durationSeconds = durationSeconds;
        this.date = date;
    }

    // You could also keep a LocalDateTime-based constructor if actual start/end times are needed for more detailed tracking
    /*
    public TimeLog(String resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = UUID.randomUUID().toString();
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = Duration.between(startTime, endTime).getSeconds();
        this.date = startTime.toLocalDate(); // Extract date from start time
    }
    */

    public String getId() { return id; }
    public String getResourceId() { return resourceId; }
    public LocalDate getDate() { return date; } // Changed from getStartTime/getEndTime
    public long getDurationSeconds() { return durationSeconds; }
    public long getDurationMinutes() { return durationSeconds / 60; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeLog timeLog = (TimeLog) o;
        return Objects.equals(id, timeLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TimeLog{" +
                "id='" + id + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", date=" + date +
                ", durationSeconds=" + durationSeconds +
                '}';
    }
}