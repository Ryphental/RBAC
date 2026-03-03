package org.example.command;

import org.example.Manager.UserManager;
import org.example.Manager.RoleManager;
import org.example.Manager.AssignmentManager;
import org.example.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RBACSystem {

    private final UserManager userManager;
    private final RoleManager roleManager;
    private final AssignmentManager assignmentManager;
    private String currentUser;

    public RBACSystem() {
        this.userManager = new UserManager();
        this.roleManager = new RoleManager();
        this.assignmentManager = new AssignmentManager(userManager, roleManager);
        this.currentUser = "system";
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    public void setCurrentUser(String username) {
        if (userManager.exists(username)) {
            this.currentUser = username;
        } else {
            throw new IllegalArgumentException("User " + username + " does not exist");
        }
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void initialize() {
        Permission readUsers = new Permission("READ", "users", "Can view users");
        Permission writeUsers = new Permission("WRITE", "users", "Can create/edit users");
        Permission deleteUsers = new Permission("DELETE", "users", "Can delete users");

        Permission readReports = new Permission("READ", "reports", "Can view reports");
        Permission writeReports = new Permission("WRITE", "reports", "Can create/edit reports");
        Permission deleteReports = new Permission("DELETE", "reports", "Can delete reports");

        Permission readSettings = new Permission("READ", "settings", "Can view settings");
        Permission writeSettings = new Permission("WRITE", "settings", "Can modify settings");

        Role adminRole = new Role("Admin", "Full system access");
        adminRole.addPermission(readUsers);
        adminRole.addPermission(writeUsers);
        adminRole.addPermission(deleteUsers);
        adminRole.addPermission(readReports);
        adminRole.addPermission(writeReports);
        adminRole.addPermission(deleteReports);
        adminRole.addPermission(readSettings);
        adminRole.addPermission(writeSettings);

        Role managerRole = new Role("Manager", "Can manage users and view reports");
        managerRole.addPermission(readUsers);
        managerRole.addPermission(writeUsers);
        managerRole.addPermission(readReports);
        managerRole.addPermission(writeReports);
        managerRole.addPermission(readSettings);

        Role viewerRole = new Role("Viewer", "Read-only access");
        viewerRole.addPermission(readUsers);
        viewerRole.addPermission(readReports);
        viewerRole.addPermission(readSettings);

        // Add roles to manager
        roleManager.add(adminRole);
        roleManager.add(managerRole);
        roleManager.add(viewerRole);

        // Create admin user
        User admin = User.create("admin", "System Administrator", "admin@system.local");
        userManager.add(admin);

        // Assign Admin role to admin user
        AssignmentMetadata metadata = AssignmentMetadata.now("system", "Initial setup");
        PermanentAssignment adminAssignment = new PermanentAssignment(admin, adminRole, metadata);
        assignmentManager.add(adminAssignment);

        // Set current user
        currentUser = "admin";
    }

    public String generateStatistics() {
        int userCount = userManager.count();
        int roleCount = roleManager.count();
        int assignmentCount = assignmentManager.count();
        int activeAssignments = assignmentManager.getActiveAssignments().size();
        int expiredAssignments = assignmentManager.getExpiredAssignments().size();

        long totalPermissions = roleManager.findAll().stream()
                .mapToLong(role -> role.getPermissions().size())
                .sum();

        StringBuilder stats = new StringBuilder();
        stats.append("=== RBAC System Statistics ===\n");
        stats.append(String.format("Generated: %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        stats.append(String.format("Current user: %s\n\n", currentUser));

        stats.append("Users:\n");
        stats.append(String.format("  Total users: %d\n", userCount));

        stats.append("\nRoles:\n");
        stats.append(String.format("  Total roles: %d\n", roleCount));
        stats.append(String.format("  Total permissions across all roles: %d\n", totalPermissions));

        stats.append("\nAssignments:\n");
        stats.append(String.format("  Total assignments: %d\n", assignmentCount));
        stats.append(String.format("  Active assignments: %d\n", activeAssignments));
        stats.append(String.format("  Expired assignments: %d\n", expiredAssignments));

        if (userCount > 0) {
            double avgAssignmentsPerUser = (double) assignmentCount / userCount;
            stats.append(String.format("  Avg assignments per user: %.2f\n", avgAssignmentsPerUser));
        }

        stats.append("=============================");

        return stats.toString();
    }

    @Override
    public String toString() {
        return String.format("RBACSystem{users=%d, roles=%d, assignments=%d, currentUser='%s'}",
                userManager.count(), roleManager.count(), assignmentManager.count(), currentUser);
    }
}