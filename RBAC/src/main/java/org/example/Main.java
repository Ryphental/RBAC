package org.example;

import org.example.Sort.UserSorters;
import org.example.Sort.RoleSorters;
import org.example.Sort.AssignmentSorters;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Test User sorting
        List<User> users = List.of(
                User.create("john_doe", "John Doe", "john@company.com"),
                User.create("alice_smith", "Alice Smith", "alice@company.com"),
                User.create("bob_wilson", "Bob Wilson", "bob@gmail.com")
        );

        System.out.println("=== Users sorted by username ===");
        users.stream()
                .sorted(UserSorters.byUsername())
                .forEach(u -> System.out.println(u.format()));

        // Test Role sorting
        Permission p1 = new Permission("READ", "users", "read");
        Permission p2 = new Permission("WRITE", "users", "write");
        Permission p3 = new Permission("DELETE", "users", "delete");

        Role admin = new Role("Admin", "admin role");
        admin.addPermission(p1);
        admin.addPermission(p2);
        admin.addPermission(p3);

        Role viewer = new Role("Viewer", "viewer role");
        viewer.addPermission(p1);

        Role moderator = new Role("Moderator", "mod role");
        moderator.addPermission(p1);
        moderator.addPermission(p2);

        List<Role> roles = List.of(admin, viewer, moderator);

        System.out.println("\n=== Roles sorted by permission count ===");
        roles.stream()
                .sorted(RoleSorters.byPermissionCount())
                .forEach(r -> System.out.println(r.getName() + ": " + r.getPermissions().size() + " permissions"));

        // Test Assignment sorting
        User user1 = User.create("john_doe", "John Doe", "john@company.com");
        User user2 = User.create("jane_smith", "Jane Smith", "jane@company.com");

        AssignmentMetadata meta1 = AssignmentMetadata.now("admin");
        AssignmentMetadata meta2 = AssignmentMetadata.now("admin", "Project X");

        PermanentAssignment permAssign = new PermanentAssignment(user1, admin, meta1);

        String expiresAt = LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        TemporaryAssignment tempAssign = new TemporaryAssignment(user2, viewer, meta2, expiresAt, false);

        List<RoleAssignment> assignments = List.of(permAssign, tempAssign);

        System.out.println("\n=== Assignments sorted by username ===");
        assignments.stream()
                .sorted(AssignmentSorters.byUsername())
                .forEach(a -> System.out.println(((AbstractRoleAssignment) a).summary()));
    }
}