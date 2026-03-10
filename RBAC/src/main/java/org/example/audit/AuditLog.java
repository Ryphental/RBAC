package org.example.audit;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLog {

    private final List<AuditEntry> entries;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public record AuditEntry(
            String timestamp,
            String action,
            String performer,
            String target,
            String details
    ) {
        public String format() {
            return String.format("[%s] %s | %s | %s | %s",
                    timestamp, action, performer, target, details);
        }
    }

    public AuditLog() {
        this.entries = new ArrayList<>();
    }

    public void log(String action, String performer, String target, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        AuditEntry entry = new AuditEntry(timestamp, action, performer, target, details);
        entries.add(entry);
    }

    public List<AuditEntry> getAll() {
        return new ArrayList<>(entries);
    }

    public List<AuditEntry> getByPerformer(String performer) {
        return entries.stream()
                .filter(e -> e.performer().equalsIgnoreCase(performer))
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getByAction(String action) {
        return entries.stream()
                .filter(e -> e.action().equalsIgnoreCase(action))
                .collect(Collectors.toList());
    }

    public void printLog() {
        if (entries.isEmpty()) {
            System.out.println("Audit log is empty");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("AUDIT LOG");
        System.out.println("=".repeat(100));
        entries.forEach(entry -> System.out.println(entry.format()));
        System.out.println("=".repeat(100));
        System.out.println("Total entries: " + entries.size());
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Timestamp,Action,Performer,Target,Details");
            for (AuditEntry entry : entries) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        entry.timestamp(),
                        entry.action(),
                        entry.performer(),
                        entry.target(),
                        entry.details().replace(",", ";"));
            }
            System.out.println("Audit log saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving audit log: " + e.getMessage());
        }
    }
}