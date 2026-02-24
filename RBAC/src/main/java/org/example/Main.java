package org.example;

import org.example.Filter.AssignmentFilter;
import org.example.Filter.AssignmentFilters;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        User user1 = User.create("john_doe", "John Doe", "john@company.com");
        User user2 = User.create("jane_smith", "Jane Smith", "jane@company.com");

        Permission readUsers = new Permission("READ", "users", "Can read users");
        Permission writeUsers = new Permission("WRITE", "users", "Can write users");

        Role admin = new Role("Administrator", "Admin role");
        admin.addPermission(readUsers);
        admin.addPermission(writeUsers);

        Role viewer = new Role("Viewer", "View only");
        viewer.addPermission(readUsers);

        AssignmentMetadata meta1 = AssignmentMetadata.now("admin", "Initial setup");
        PermanentAssignment permAssign = new PermanentAssignment(user1, admin, meta1);

        AssignmentMetadata meta2 = AssignmentMetadata.now("admin");
        String expiresAt = LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        TemporaryAssignment tempAssign = new TemporaryAssignment(user2, viewer, meta2, expiresAt, false);

        List<RoleAssignment> assignments = List.of(permAssign, tempAssign);

        System.out.println("=== Active assignments ===");
        assignments.stream()
                .filter(AssignmentFilters.activeOnly()::test)
                .forEach(a -> System.out.println(((AbstractRoleAssignment) a).summary()));

        System.out.println("\n=== Assignments by user john_doe ===");
        assignments.stream()
                .filter(AssignmentFilters.byUsername("john_doe")::test)
                .forEach(a -> System.out.println(((AbstractRoleAssignment) a).summary()));

        System.out.println("\n=== Temporary assignments ===");
        assignments.stream()
                .filter(AssignmentFilters.byType("TEMPORARY")::test)
                .forEach(a -> System.out.println(((AbstractRoleAssignment) a).summary()));

        System.out.println("\n=== Combined filter (active AND by admin) ===");
        assignments.stream()
                .filter(AssignmentFilters.activeOnly()
                        .and(AssignmentFilters.assignedBy("admin"))::test)
                .forEach(a -> System.out.println(((AbstractRoleAssignment) a).summary()));
    }
}