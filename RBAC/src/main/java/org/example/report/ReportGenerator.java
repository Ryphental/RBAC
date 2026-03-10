package org.example.report;

import org.example.*;
import org.example.Filter.RoleFilters;
import org.example.Filter.UserFilters;
import org.example.Manager.UserManager;
import org.example.Manager.RoleManager;
import org.example.Manager.AssignmentManager;
import org.example.Sort.RoleSorters;
import org.example.Sort.UserSorters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        sb.append("=".repeat(100)).append("\n");
        sb.append("USER REPORT\n");
        sb.append("Generated: ").append(timestamp).append("\n");
        sb.append("=".repeat(100)).append("\n\n");

        List<User> users = userManager.findAll(UserFilters.byFullNameContains(""), UserSorters.byUsername());

        for (User user : users) {
            sb.append("User: ").append(user.format()).append("\n");

            List<RoleAssignment> assignments = assignmentManager.findByUser(user);
            if (assignments.isEmpty()) {
                sb.append("  No roles assigned\n");
            } else {
                sb.append("  Assigned roles:\n");
                for (RoleAssignment ra : assignments) {
                    String status = ra.isActive() ? "ACTIVE" : "INACTIVE";
                    String expiresInfo = "";
                    if (ra instanceof TemporaryAssignment temp) {
                        expiresInfo = " (expires: " + temp.getExpiresAt() + ")";
                    }
                    sb.append(String.format("    - %s [%s] %s%s\n",
                            ra.role().getName(), ra.assignmentType(), status, expiresInfo));
                }

                Set<Permission> permissions = assignmentManager.getUserPermissions(user);
                sb.append("  Total permissions: ").append(permissions.size()).append("\n");
            }
            sb.append("-".repeat(80)).append("\n");
        }

        sb.append("\n").append("=".repeat(100)).append("\n");
        sb.append("Total users: ").append(users.size()).append("\n");
        sb.append("=".repeat(100)).append("\n");

        return sb.toString();
    }

    public String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        sb.append("=".repeat(100)).append("\n");
        sb.append("ROLE REPORT\n");
        sb.append("Generated: ").append(timestamp).append("\n");
        sb.append("=".repeat(100)).append("\n\n");

        List<Role> roles = roleManager.findAll(RoleFilters.byNameContains(""), RoleSorters.byName());

        for (Role role : roles) {
            sb.append(role.format()).append("\n");

            List<RoleAssignment> assignments = assignmentManager.findByRole(role);
            long activeCount = assignments.stream().filter(RoleAssignment::isActive).count();

            sb.append("  Assigned to: ").append(assignments.size()).append(" users")
                    .append(" (").append(activeCount).append(" active)\n");

            if (!assignments.isEmpty()) {
                sb.append("  Users:\n");
                assignments.stream()
                        .filter(RoleAssignment::isActive)
                        .forEach(a -> sb.append("    - ").append(a.user().username()).append("\n"));
            }

            sb.append("  Permissions (").append(role.getPermissions().size()).append("):\n");
            role.getPermissions().stream()
                    .sorted(Comparator.comparing(Permission::resource))
                    .forEach(p -> sb.append("    - ").append(p.format()).append("\n"));

            sb.append("-".repeat(80)).append("\n");
        }

        sb.append("\n").append("=".repeat(100)).append("\n");
        sb.append("Total roles: ").append(roles.size()).append("\n");
        sb.append("=".repeat(100)).append("\n");

        return sb.toString();
    }

    public String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        sb.append("=".repeat(120)).append("\n");
        sb.append("PERMISSION MATRIX\n");
        sb.append("Generated: ").append(timestamp).append("\n");
        sb.append("=".repeat(120)).append("\n\n");

        List<User> users = userManager.findAll(UserFilters.byFullNameContains(""), UserSorters.byUsername());

        // Collect all unique resources
        Set<String> allResources = new TreeSet<>();
        Map<User, Set<Permission>> userPermissions = new HashMap<>();

        for (User user : users) {
            Set<Permission> perms = assignmentManager.getUserPermissions(user);
            userPermissions.put(user, perms);
            perms.stream().map(Permission::resource).forEach(allResources::add);
        }

        // Header
        sb.append(String.format("%-20s", "Username"));
        for (String resource : allResources) {
            sb.append(String.format(" | %-15s", resource));
        }
        sb.append("\n");
        sb.append("-".repeat(20 + allResources.size() * 18)).append("\n");

        // Rows
        for (User user : users) {
            sb.append(String.format("%-20s", user.username()));

            Map<String, List<Permission>> permsByResource = userPermissions.get(user).stream()
                    .collect(Collectors.groupingBy(Permission::resource));

            for (String resource : allResources) {
                List<Permission> perms = permsByResource.getOrDefault(resource, List.of());
                String permStr = perms.stream()
                        .map(Permission::name)
                        .collect(Collectors.joining(","));
                if (permStr.isEmpty()) permStr = "-";
                sb.append(String.format(" | %-15s", permStr.length() > 15 ? permStr.substring(0, 12) + "..." : permStr));
            }
            sb.append("\n");
        }

        sb.append("\n").append("=".repeat(120)).append("\n");
        sb.append("Total users: ").append(users.size()).append("\n");
        sb.append("Total resources: ").append(allResources.size()).append("\n");
        sb.append("=".repeat(120)).append("\n");

        return sb.toString();
    }

    public void exportToFile(String report, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(report);
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving report: " + e.getMessage());
        }
    }
}