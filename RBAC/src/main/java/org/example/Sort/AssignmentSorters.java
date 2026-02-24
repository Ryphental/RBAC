package org.example.Sort;

import org.example.RoleAssignment;
import org.example.AbstractRoleAssignment;
import org.example.TemporaryAssignment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class AssignmentSorters {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter TEMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AssignmentSorters() {}

    public static Comparator<RoleAssignment> byUsername() {
        return Comparator.comparing(a -> a.user().username(), String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<RoleAssignment> byRoleName() {
        return Comparator.comparing(a -> a.role().getName(), String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<RoleAssignment> byAssignmentDate() {
        return Comparator.comparing(a -> {
            String dateStr = a.metadata().assignedAt();
            return LocalDateTime.parse(dateStr, FORMATTER);
        });
    }

    public static Comparator<RoleAssignment> byExpirationDate() {
        return (a1, a2) -> {
            if (a1 instanceof TemporaryAssignment t1 && a2 instanceof TemporaryAssignment t2) {
                LocalDateTime d1 = LocalDateTime.parse(t1.getExpiresAt(), TEMP_FORMATTER);
                LocalDateTime d2 = LocalDateTime.parse(t2.getExpiresAt(), TEMP_FORMATTER);
                return d1.compareTo(d2);
            }
            if (a1 instanceof TemporaryAssignment) return -1;
            if (a2 instanceof TemporaryAssignment) return 1;
            return 0;
        };
    }
}
