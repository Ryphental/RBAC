package org.example;

import org.example.util.DateUtils;
import org.example.util.ValidationUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TemporaryAssignment extends AbstractRoleAssignment {
    private String expiresAt;
    private final boolean autoRenew;

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
        return DateUtils.isBefore(expiresAt, DateUtils.getCurrentDateTime());
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
        if (isExpired()) {
            return "Expired " + DateUtils.formatRelativeTime(expiresAt);
        }
        return DateUtils.formatRelativeTime(expiresAt);
    }

    @Override
    public String summary() {
        String baseSummary = super.summary();
        String status = isActive() ? "ACTIVE" : "EXPIRED";
        String autoRenewText = autoRenew ? " (auto-renew)" : "";
        String expiresInfo = "Expires: " + DateUtils.formatDateTime(expiresAt) + " (" + getTimeRemaining() + ")";

        return String.format("%s\n%s%s\nStatus: %s",
                baseSummary, expiresInfo, autoRenewText, status);
    }
}