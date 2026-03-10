package org.example;

import org.example.util.ValidationUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TemporaryAssignment extends AbstractRoleAssignment {
    private String expiresAt;
    private final boolean autoRenew;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TemporaryAssignment(User user, Role role, AssignmentMetadata metadata,
                               String expiresAt, boolean autoRenew) {
        super(user, role, metadata);
        ValidationUtils.requireValidDate(expiresAt);
        this.expiresAt = expiresAt;
        this.autoRenew = autoRenew;
    }

    @Override
    public boolean isActive() {
        return !isExpired();
    }

    @Override
    public String assignmentType() {
        return "TEMPORARY";
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = LocalDateTime.parse(expiresAt, FORMATTER);
        return now.isAfter(expire);
    }

    public void extend(String newExpirationDate) {
        ValidationUtils.requireValidDate(newExpirationDate);
        this.expiresAt = newExpirationDate;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public String getTimeRemaining() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = LocalDateTime.parse(expiresAt, FORMATTER);

        if (now.isAfter(expire)) {
            return "Expired";
        }

        long days = ChronoUnit.DAYS.between(now, expire);
        long hours = ChronoUnit.HOURS.between(now, expire) % 24;
        long minutes = ChronoUnit.MINUTES.between(now, expire) % 60;

        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
    }

    @Override
    public String summary() {
        String baseSummary = super.summary();
        String status = isActive() ? "ACTIVE" : "EXPIRED";
        String autoRenewText = autoRenew ? " (auto-renew)" : "";

        return String.format("%s\nExpires: %s%s\nStatus: %s",
                baseSummary, expiresAt, autoRenewText, status);
    }
}