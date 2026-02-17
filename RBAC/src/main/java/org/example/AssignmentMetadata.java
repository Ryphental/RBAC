package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public AssignmentMetadata {
        if (assignedBy == null || assignedBy.isBlank()) {
            throw new IllegalArgumentException("assignedBy cannot be empty");
        }
        if (assignedAt == null || assignedAt.isBlank()) {
            throw new IllegalArgumentException("assignedAt cannot be empty");
        }
    }

    public static AssignmentMetadata now(String assignedBy, String reason) {
        String now = LocalDateTime.now().format(FORMATTER);
        return new AssignmentMetadata(assignedBy, now, reason);
    }

    public static AssignmentMetadata now(String assignedBy) {
        return now(assignedBy, null);
    }

    public String format() {
        if (reason == null || reason.isBlank()) {
            return String.format("Assigned by: %s at %s", assignedBy, assignedAt);
        }
        return String.format("Assigned by: %s at %s (Reason: %s)", assignedBy, assignedAt, reason);
    }
}