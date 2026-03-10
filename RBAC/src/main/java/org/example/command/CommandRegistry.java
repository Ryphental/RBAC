package org.example.command;

import org.example.*;
import org.example.Filter.*;
import org.example.Sort.UserSorters;
import org.example.Sort.RoleSorters;
import org.example.Sort.AssignmentSorters;
import org.example.Manager.UserManager;
import org.example.Manager.RoleManager;
import org.example.Manager.AssignmentManager;
import org.example.audit.AuditLog;
import org.example.report.ReportGenerator;
import org.example.util.ConsoleUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CommandRegistry {

    private final CommandParser parser;
    private final RBACSystem system;
    private final Scanner scanner;

    public CommandRegistry(RBACSystem system) {
        this.parser = new CommandParser();
        this.system = system;
        this.scanner = new Scanner(System.in);
        registerAllCommands();
    }

    private void registerAllCommands() {
        parser.registerCommand("user-list", "List all users", this::userList);
        parser.registerCommand("user-create", "Create new user", this::userCreate);
        parser.registerCommand("user-view", "View user details", this::userView);
        parser.registerCommand("user-update", "Update user", this::userUpdate);
        parser.registerCommand("user-delete", "Delete user", this::userDelete);
        parser.registerCommand("user-search", "Search users by filters", this::userSearch);

        parser.registerCommand("role-list", "List all roles", this::roleList);
        parser.registerCommand("role-create", "Create new role", this::roleCreate);
        parser.registerCommand("role-view", "View role details", this::roleView);
        parser.registerCommand("role-update", "Update role", this::roleUpdate);
        parser.registerCommand("role-delete", "Delete role", this::roleDelete);
        parser.registerCommand("role-add-permission", "Add permission to role", this::roleAddPermission);
        parser.registerCommand("role-remove-permission", "Remove permission from role", this::roleRemovePermission);
        parser.registerCommand("role-search", "Search roles by filters", this::roleSearch);

        parser.registerCommand("assign-role", "Assign role to user", this::assignRole);
        parser.registerCommand("revoke-role", "Revoke role from user", this::revokeRole);
        parser.registerCommand("assignment-list", "List all assignments", this::assignmentList);
        parser.registerCommand("assignment-list-user", "List user assignments", this::assignmentListUser);
        parser.registerCommand("assignment-list-role", "List users with role", this::assignmentListRole);
        parser.registerCommand("assignment-active", "List active assignments", this::assignmentActive);
        parser.registerCommand("assignment-expired", "List expired assignments", this::assignmentExpired);
        parser.registerCommand("assignment-extend", "Extend temporary assignment", this::assignmentExtend);
        parser.registerCommand("assignment-search", "Search assignments", this::assignmentSearch);

        parser.registerCommand("permissions-user", "View user permissions", this::permissionsUser);
        parser.registerCommand("permissions-check", "Check user permission", this::permissionsCheck);

        parser.registerCommand("help", "Show this help", (s, sys) -> parser.printHelp());
        parser.registerCommand("stats", "Show system statistics", this::stats);
        parser.registerCommand("clear", "Clear screen", this::clear);
        parser.registerCommand("exit", "Exit program", this::exit);
        parser.registerCommand("save", "Save data to file", this::save);
        parser.registerCommand("load", "Load data from file", this::load);

        parser.registerCommand("audit-log", "View audit log", this::auditLog);
        parser.registerCommand("audit-save", "Save audit log to file", this::auditSave);

        parser.registerCommand("report-users", "Generate user report", this::reportUsers);
        parser.registerCommand("report-roles", "Generate role report", this::reportRoles);
        parser.registerCommand("report-matrix", "Generate permission matrix", this::reportMatrix);
    }

    private void reportUsers(Scanner sc, RBACSystem sys) {
        ReportGenerator generator = new ReportGenerator();
        String report = generator.generateUserReport(sys.getUserManager(), sys.getAssignmentManager());

        System.out.println(report);

        System.out.print("Save to file? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.print("Enter filename: ");
            String filename = sc.nextLine().trim();
            if (!filename.endsWith(".txt")) filename += ".txt";
            generator.exportToFile(report, filename);
        }
    }

    private void reportRoles(Scanner sc, RBACSystem sys) {
        ReportGenerator generator = new ReportGenerator();
        String report = generator.generateRoleReport(sys.getRoleManager(), sys.getAssignmentManager());

        System.out.println(report);

        System.out.print("Save to file? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.print("Enter filename: ");
            String filename = sc.nextLine().trim();
            if (!filename.endsWith(".txt")) filename += ".txt";
            generator.exportToFile(report, filename);
        }
    }

    private void reportMatrix(Scanner sc, RBACSystem sys) {
        ReportGenerator generator = new ReportGenerator();
        String report = generator.generatePermissionMatrix(sys.getUserManager(), sys.getAssignmentManager());

        System.out.println(report);

        System.out.print("Save to file? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.print("Enter filename: ");
            String filename = sc.nextLine().trim();
            if (!filename.endsWith(".txt")) filename += ".txt";
            generator.exportToFile(report, filename);
        }
    }

    private void auditLog(Scanner sc, RBACSystem sys) {
        System.out.println("Audit log options:");
        System.out.println("1. Show all");
        System.out.println("2. Filter by performer");
        System.out.println("3. Filter by action");
        System.out.print("Choose (1-3): ");

        String choice = sc.nextLine().trim();
        List<AuditLog.AuditEntry> entries;

        switch (choice) {
            case "2":
                System.out.print("Enter performer username: ");
                String performer = sc.nextLine().trim();
                entries = sys.getAuditLog().getByPerformer(performer);
                break;
            case "3":
                System.out.print("Enter action: ");
                String action = sc.nextLine().trim().toUpperCase();
                entries = sys.getAuditLog().getByAction(action);
                break;
            default:
                entries = sys.getAuditLog().getAll();
        }

        if (entries.isEmpty()) {
            System.out.println("No audit entries found");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-20s | %-15s | %-15s | %-20s | %s\n",
                "Timestamp", "Action", "Performer", "Target", "Details");
        System.out.println("=".repeat(100));

        entries.forEach(e -> {
            System.out.printf("%-20s | %-15s | %-15s | %-20s | %s\n",
                    e.timestamp(),
                    truncate(e.action(), 15),
                    truncate(e.performer(), 15),
                    truncate(e.target(), 20),
                    truncate(e.details(), 30));
        });
        System.out.println("=".repeat(100));
        System.out.println("Total entries: " + entries.size());
    }

    private void auditSave(Scanner sc, RBACSystem sys) {
        System.out.print("Enter filename to save: ");
        String filename = sc.nextLine().trim();
        if (!filename.endsWith(".csv")) {
            filename += ".csv";
        }
        sys.getAuditLog().saveToFile(filename);
    }

    public void run() {
        System.out.println("RBAC System Console");
        System.out.println("Type 'help' for available commands");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                exit(scanner, system);
                break;
            }
            parser.parseAndExecute(input, scanner, system);
        }
    }

    private void userList(Scanner sc, RBACSystem sys) {
        List<User> users = sys.getUserManager().findAll(UserFilters.byFullNameContains(""), UserSorters.byUsername());
        printUserTable(users);
    }

    private void userCreate(Scanner sc, RBACSystem sys) {
        ConsoleUtils.printHeader("CREATE NEW USER");

        try {
            String username = ConsoleUtils.promptString(sc, "Username", true);
            String fullName = ConsoleUtils.promptString(sc, "Full name", true);
            String email = ConsoleUtils.promptString(sc, "Email", true);

            User user = User.create(username, fullName, email);
            sys.getUserManager().add(user);

            sys.getAuditLog().log("USER_CREATE", sys.getCurrentUser(), username,
                    String.format("Created user: %s <%s>", fullName, email));

            ConsoleUtils.printSuccess("User created successfully");
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void userUpdate(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Username to update: ");
            String username = sc.nextLine().trim();

            String oldFullName = sys.getUserManager().findByUsername(username)
                    .map(User::fullName).orElse("");
            String oldEmail = sys.getUserManager().findByUsername(username)
                    .map(User::email).orElse("");

            System.out.print("New full name (or press Enter to skip): ");
            String fullName = sc.nextLine().trim();
            if (fullName.isEmpty()) {
                fullName = oldFullName;
            }

            System.out.print("New email (or press Enter to skip): ");
            String email = sc.nextLine().trim();
            if (email.isEmpty()) {
                email = oldEmail;
            }

            sys.getUserManager().update(username, fullName, email);

            sys.getAuditLog().log("USER_UPDATE", sys.getCurrentUser(), username,
                    String.format("Updated: %s -> %s, %s -> %s", oldFullName, fullName, oldEmail, email));

            System.out.println("User updated successfully");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void userDelete(Scanner sc, RBACSystem sys) {
        ConsoleUtils.printHeader("DELETE USER");

        String username = ConsoleUtils.promptString(sc, "Username to delete", true);

        sys.getUserManager().findByUsername(username).ifPresentOrElse(
                user -> {
                    if (ConsoleUtils.promptYesNo(sc, "Are you sure you want to delete " + username + "?")) {

                        List<RoleAssignment> assignments = sys.getAssignmentManager().findByUser(user);
                        if (!assignments.isEmpty()) {
                            ConsoleUtils.printWarning("User has " + assignments.size() + " active assignments");
                            if (ConsoleUtils.promptYesNo(sc, "Delete assignments and continue?")) {
                                assignments.forEach(a -> sys.getAssignmentManager().remove(a));
                            } else {
                                ConsoleUtils.printWarning("Deletion cancelled");
                                return;
                            }
                        }

                        sys.getUserManager().remove(user);

                        sys.getAuditLog().log("USER_DELETE", sys.getCurrentUser(), username,
                                String.format("Deleted user: %s <%s>", user.fullName(), user.email()));

                        ConsoleUtils.printSuccess("User deleted successfully");
                    } else {
                        ConsoleUtils.printWarning("Deletion cancelled");
                    }
                },
                () -> ConsoleUtils.printError("User not found")
        );
    }

    private void roleCreate(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Role name: ");
            String name = sc.nextLine().trim();

            System.out.print("Description: ");
            String description = sc.nextLine().trim();

            Role role = new Role(name, description);
            sys.getRoleManager().add(role);

            sys.getAuditLog().log("ROLE_CREATE", sys.getCurrentUser(), name,
                    "Created role: " + description);

            System.out.println("Role created successfully");

            System.out.print("Add permissions now? (yes/no): ");
            if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                addPermissionsToRole(sc, sys, role.getName());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void roleDelete(Scanner sc, RBACSystem sys) {
        System.out.print("Role name to delete: ");
        String name = sc.nextLine().trim();

        sys.getRoleManager().findByName(name).ifPresentOrElse(
                role -> {
                    List<RoleAssignment> assignments = sys.getAssignmentManager().findByRole(role);
                    if (!assignments.isEmpty()) {
                        System.out.println("Warning: Role is assigned to these users:");
                        assignments.forEach(a -> System.out.println("  - " + a.user().username()));
                        System.out.print("Delete anyway? (yes/no): ");
                        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                            System.out.println("Deletion cancelled");
                            return;
                        }
                        assignments.forEach(a -> sys.getAssignmentManager().remove(a));
                    }

                    sys.getRoleManager().remove(role);

                    sys.getAuditLog().log("ROLE_DELETE", sys.getCurrentUser(), name,
                            "Deleted role with " + role.getPermissions().size() + " permissions");

                    System.out.println("Role deleted");
                },
                () -> System.out.println("Role not found")
        );
    }

    private void roleUpdate(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Role name to update: ");
            String name = sc.nextLine().trim();

            Role existing = sys.getRoleManager().findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            String oldName = existing.getName();
            String oldDesc = existing.getDescription();

            System.out.print("New name (or press Enter to skip): ");
            String newName = sc.nextLine().trim();
            if (newName.isEmpty()) newName = existing.getName();

            System.out.print("New description (or press Enter to skip): ");
            String newDesc = sc.nextLine().trim();
            if (newDesc.isEmpty()) newDesc = existing.getDescription();

            Role updated = new Role(newName, newDesc);
            existing.getPermissions().forEach(updated::addPermission);

            sys.getRoleManager().remove(existing);
            sys.getRoleManager().add(updated);

            sys.getAuditLog().log("ROLE_UPDATE", sys.getCurrentUser(), name,
                    String.format("Updated: %s/%s -> %s/%s", oldName, oldDesc, newName, newDesc));

            System.out.println("Role updated");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void assignRole(Scanner sc, RBACSystem sys) {
        ConsoleUtils.printHeader("ASSIGN ROLE TO USER");

        try {
            String username = ConsoleUtils.promptString(sc, "Username", true);

            User user = sys.getUserManager().findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<Role> roles = sys.getRoleManager().findAll();
            if (roles.isEmpty()) {
                ConsoleUtils.printError("No roles available");
                return;
            }

            Role role = ConsoleUtils.promptChoice(sc, "Select role to assign", roles);

            List<String> types = List.of("permanent", "temporary");
            String type = ConsoleUtils.promptChoice(sc, "Assignment type", types);

            String reason = ConsoleUtils.promptString(sc, "Reason (optional)", false);
            if (reason.isEmpty()) reason = null;

            AssignmentMetadata metadata = AssignmentMetadata.now(sys.getCurrentUser(), reason);
            RoleAssignment assignment;

            if (type.equals("temporary")) {
                String expiresAt = ConsoleUtils.promptString(sc, "Expiration date (yyyy-MM-dd HH:mm)", true);
                boolean autoRenew = ConsoleUtils.promptYesNo(sc, "Auto renew?");

                assignment = new TemporaryAssignment(user, role, metadata, expiresAt, autoRenew);
            } else {
                assignment = new PermanentAssignment(user, role, metadata);
            }

            sys.getAssignmentManager().add(assignment);

            sys.getAuditLog().log("ROLE_ASSIGN", sys.getCurrentUser(), username,
                    String.format("Assigned role %s (%s)", role.getName(), type));

            ConsoleUtils.printSuccess("Role assigned successfully");

        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }


    private void revokeRole(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Username: ");
            String username = sc.nextLine().trim();

            User user = sys.getUserManager().findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<RoleAssignment> assignments = sys.getAssignmentManager().findByUser(user).stream()
                    .filter(RoleAssignment::isActive)
                    .collect(Collectors.toList());

            if (assignments.isEmpty()) {
                System.out.println("No active assignments for this user");
                return;
            }

            System.out.println("Active assignments:");
            for (int i = 0; i < assignments.size(); i++) {
                RoleAssignment a = assignments.get(i);
                System.out.printf("%d. %s [%s]\n", i + 1, a.role().getName(), a.assignmentType());
            }

            System.out.print("Choose assignment to revoke (0 to cancel): ");
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice > 0 && choice <= assignments.size()) {
                RoleAssignment selected = assignments.get(choice - 1);
                String roleName = selected.role().getName();

                if (selected instanceof PermanentAssignment perm) {
                    perm.revoke();
                    sys.getAuditLog().log("ROLE_REVOKE", sys.getCurrentUser(), username,
                            "Revoked permanent role: " + roleName);
                    System.out.println("Assignment revoked");
                } else {
                    sys.getAssignmentManager().remove(selected);
                    sys.getAuditLog().log("ROLE_REVOKE", sys.getCurrentUser(), username,
                            "Removed temporary role: " + roleName);
                    System.out.println("Assignment removed");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void userView(Scanner sc, RBACSystem sys) {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        sys.getUserManager().findByUsername(username).ifPresentOrElse(
                user -> {
                    System.out.println("\n" + user.format());
                    System.out.println("\nAssigned roles:");
                    List<RoleAssignment> assignments = sys.getAssignmentManager().findByUser(user);
                    if (assignments.isEmpty()) {
                        System.out.println("  No roles assigned");
                    } else {
                        assignments.forEach(a -> {
                            String status = a.isActive() ? "ACTIVE" : "INACTIVE";
                            System.out.printf("  - %s [%s] %s\n",
                                    a.role().getName(), a.assignmentType(), status);
                        });
                    }

                    System.out.println("\nAll permissions:");
                    Set<Permission> permissions = sys.getAssignmentManager().getUserPermissions(user);
                    if (permissions.isEmpty()) {
                        System.out.println("  No permissions");
                    } else {
                        permissions.stream()
                                .sorted(Comparator.comparing(Permission::resource))
                                .forEach(p -> System.out.printf("  - %s\n", p.format()));
                    }
                },
                () -> System.out.println("User not found")
        );
    }

    private void userSearch(Scanner sc, RBACSystem sys) {
        ConsoleUtils.printHeader("SEARCH USERS");

        List<String> searchTypes = List.of(
                "Username (contains)",
                "Email (contains)",
                "Email domain",
                "Full name (contains)"
        );

        String choice = ConsoleUtils.promptChoice(sc, "Search by", searchTypes);
        UserFilter filter = null;

        try {
            switch (searchTypes.indexOf(choice)) {
                case 0:
                    String uname = ConsoleUtils.promptString(sc, "Enter username part", true);
                    filter = UserFilters.byUsernameContains(uname);
                    break;
                case 1:
                    String email = ConsoleUtils.promptString(sc, "Enter email part", true);
                    filter = user -> user.email().toLowerCase().contains(email.toLowerCase());
                    break;
                case 2:
                    String domain = ConsoleUtils.promptString(sc, "Enter domain (e.g., @company.com)", true);
                    filter = UserFilters.byEmailDomain(domain);
                    break;
                case 3:
                    String name = ConsoleUtils.promptString(sc, "Enter name part", true);
                    filter = UserFilters.byFullNameContains(name);
                    break;
            }

            List<User> results = sys.getUserManager().findByFilter(filter);
            if (results.isEmpty()) {
                ConsoleUtils.printWarning("No users found");
            } else {
                ConsoleUtils.printSuccess("Found " + results.size() + " users:");
                printUserTable(results);
            }
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void roleList(Scanner sc, RBACSystem sys) {
        List<Role> roles = sys.getRoleManager().findAll(RoleFilters.byNameContains(""), RoleSorters.byName());
        printRoleTable(roles);
    }

    private void roleView(Scanner sc, RBACSystem sys) {
        System.out.print("Role name: ");
        String name = sc.nextLine().trim();

        sys.getRoleManager().findByName(name).ifPresentOrElse(
                role -> {
                    System.out.println("\n" + role.format());

                    List<RoleAssignment> assignments = sys.getAssignmentManager().findByRole(role);
                    if (!assignments.isEmpty()) {
                        System.out.println("\nAssigned to:");
                        assignments.stream()
                                .filter(RoleAssignment::isActive)
                                .forEach(a -> System.out.println("  - " + a.user().username()));
                    }
                },
                () -> System.out.println("Role not found")
        );
    }

    private void roleAddPermission(Scanner sc, RBACSystem sys) {
        ConsoleUtils.printHeader("ADD PERMISSION TO ROLE");

        try {
            String roleName = ConsoleUtils.promptString(sc, "Role name", true);

            Role role = sys.getRoleManager().findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            String permName = ConsoleUtils.promptString(sc, "Permission name (e.g., READ)", true).toUpperCase();
            String resource = ConsoleUtils.promptString(sc, "Resource (e.g., users)", true).toLowerCase();
            String desc = ConsoleUtils.promptString(sc, "Description", true);

            Permission permission = new Permission(permName, resource, desc);
            sys.getRoleManager().addPermissionToRole(roleName, permission);

            sys.getAuditLog().log("PERMISSION_ADD", sys.getCurrentUser(), roleName,
                    String.format("Added permission %s on %s", permName, resource));

            ConsoleUtils.printSuccess("Permission added");
        } catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

    private void roleRemovePermission(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Role name: ");
            String roleName = sc.nextLine().trim();

            Role role = sys.getRoleManager().findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            List<Permission> perms = new ArrayList<>(role.getPermissions());
            if (perms.isEmpty()) {
                System.out.println("Role has no permissions");
                return;
            }

            System.out.println("Permissions:");
            for (int i = 0; i < perms.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, perms.get(i).format());
            }

            System.out.print("Enter number to remove (0 to cancel): ");
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice > 0 && choice <= perms.size()) {
                Permission removed = perms.get(choice - 1);
                sys.getRoleManager().removePermissionFromRole(roleName, removed);

                sys.getAuditLog().log("PERMISSION_REMOVE", sys.getCurrentUser(), roleName,
                        String.format("Removed permission %s on %s", removed.name(), removed.resource()));

                System.out.println("Permission removed");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void roleSearch(Scanner sc, RBACSystem sys) {
        System.out.println("Search by:");
        System.out.println("1. Name (contains)");
        System.out.println("2. Has specific permission");
        System.out.println("3. Minimum permission count");
        System.out.print("Choose (1-3): ");

        String choice = sc.nextLine().trim();
        RoleFilter filter = null;

        try {
            switch (choice) {
                case "1":
                    System.out.print("Enter name part: ");
                    String name = sc.nextLine().trim();
                    filter = RoleFilters.byNameContains(name);
                    break;
                case "2":
                    System.out.print("Permission name: ");
                    String pName = sc.nextLine().trim().toUpperCase();
                    System.out.print("Resource: ");
                    String resource = sc.nextLine().trim().toLowerCase();
                    filter = RoleFilters.hasPermission(pName, resource);
                    break;
                case "3":
                    System.out.print("Minimum permissions: ");
                    int min = Integer.parseInt(sc.nextLine().trim());
                    filter = RoleFilters.hasAtLeastNPermissions(min);
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }

            List<Role> results = sys.getRoleManager().findByFilter(filter);
            if (results.isEmpty()) {
                System.out.println("No roles found");
            } else {
                printRoleTable(results);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void assignmentList(Scanner sc, RBACSystem sys) {
        List<RoleAssignment> assignments = sys.getAssignmentManager().findAll();
        printAssignmentTable(assignments);
    }

    private void assignmentListUser(Scanner sc, RBACSystem sys) {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        sys.getUserManager().findByUsername(username).ifPresentOrElse(
                user -> {
                    List<RoleAssignment> assignments = sys.getAssignmentManager().findByUser(user);
                    if (assignments.isEmpty()) {
                        System.out.println("No assignments for this user");
                    } else {
                        printAssignmentTable(assignments);
                    }
                },
                () -> System.out.println("User not found")
        );
    }

    private void assignmentListRole(Scanner sc, RBACSystem sys) {
        System.out.print("Role name: ");
        String roleName = sc.nextLine().trim();

        sys.getRoleManager().findByName(roleName).ifPresentOrElse(
                role -> {
                    List<RoleAssignment> assignments = sys.getAssignmentManager().findByRole(role);
                    if (assignments.isEmpty()) {
                        System.out.println("No users with this role");
                    } else {
                        System.out.println("\nUsers with role " + roleName + ":");
                        assignments.stream()
                                .filter(RoleAssignment::isActive)
                                .forEach(a -> System.out.println("  - " + a.user().username()));
                    }
                },
                () -> System.out.println("Role not found")
        );
    }

    private void assignmentActive(Scanner sc, RBACSystem sys) {
        List<RoleAssignment> active = sys.getAssignmentManager().getActiveAssignments();
        printAssignmentTable(active);
    }

    private void assignmentExpired(Scanner sc, RBACSystem sys) {
        List<RoleAssignment> expired = sys.getAssignmentManager().getExpiredAssignments();
        printAssignmentTable(expired);
    }

    private void assignmentExtend(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Assignment ID: ");
            String id = sc.nextLine().trim();

            System.out.print("New expiration date (yyyy-MM-dd HH:mm): ");
            String newDate = sc.nextLine().trim();

            sys.getAssignmentManager().extendTemporaryAssignment(id, newDate);

            sys.getAuditLog().log("ASSIGNMENT_EXTEND", sys.getCurrentUser(), id,
                    "Extended to: " + newDate);

            System.out.println("Assignment extended");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void assignmentSearch(Scanner sc, RBACSystem sys) {
        System.out.println("Search by:");
        System.out.println("1. Username");
        System.out.println("2. Role name");
        System.out.println("3. Type (permanent/temporary)");
        System.out.println("4. Status (active/inactive)");
        System.out.print("Choose (1-4): ");

        String choice = sc.nextLine().trim();
        AssignmentFilter filter = null;

        try {
            switch (choice) {
                case "1":
                    System.out.print("Username: ");
                    String uname = sc.nextLine().trim();
                    filter = AssignmentFilters.byUsername(uname);
                    break;
                case "2":
                    System.out.print("Role name: ");
                    String rname = sc.nextLine().trim();
                    filter = AssignmentFilters.byRoleName(rname);
                    break;
                case "3":
                    System.out.print("Type: ");
                    String type = sc.nextLine().trim().toUpperCase();
                    filter = AssignmentFilters.byType(type);
                    break;
                case "4":
                    System.out.print("Status (active/inactive): ");
                    String status = sc.nextLine().trim();
                    filter = status.equalsIgnoreCase("active") ?
                            AssignmentFilters.activeOnly() : AssignmentFilters.inactiveOnly();
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }

            List<RoleAssignment> results = sys.getAssignmentManager().findByFilter(filter);
            if (results.isEmpty()) {
                System.out.println("No assignments found");
            } else {
                printAssignmentTable(results);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void permissionsUser(Scanner sc, RBACSystem sys) {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        sys.getUserManager().findByUsername(username).ifPresentOrElse(
                user -> {
                    Set<Permission> permissions = sys.getAssignmentManager().getUserPermissions(user);
                    if (permissions.isEmpty()) {
                        System.out.println("No permissions for this user");
                    } else {
                        System.out.println("\nPermissions for " + username + ":");
                        Map<String, List<Permission>> byResource = permissions.stream()
                                .collect(Collectors.groupingBy(Permission::resource));

                        byResource.forEach((resource, perms) -> {
                            System.out.println("\n  " + resource + ":");
                            perms.forEach(p -> System.out.println("    - " + p.name() + ": " + p.description()));
                        });
                    }
                },
                () -> System.out.println("User not found")
        );
    }

    private void permissionsCheck(Scanner sc, RBACSystem sys) {
        try {
            System.out.print("Username: ");
            String username = sc.nextLine().trim();

            System.out.print("Permission name: ");
            String permName = sc.nextLine().trim().toUpperCase();

            System.out.print("Resource: ");
            String resource = sc.nextLine().trim().toLowerCase();

            User user = sys.getUserManager().findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean hasPerm = sys.getAssignmentManager().userHasPermission(user, permName, resource);

            if (hasPerm) {
                System.out.println("✓ User HAS this permission");

                sys.getAssignmentManager().findByUser(user).stream()
                        .filter(RoleAssignment::isActive)
                        .map(RoleAssignment::role)
                        .filter(role -> role.hasPermission(permName, resource))
                        .forEach(role -> System.out.println("  - via role: " + role.getName()));
            } else {
                System.out.println("✗ User does NOT have this permission");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void stats(Scanner sc, RBACSystem sys) {
        System.out.println(sys.generateStatistics());

        Map<Role, Long> roleCounts = sys.getAssignmentManager().findAll().stream()
                .filter(RoleAssignment::isActive)
                .collect(Collectors.groupingBy(RoleAssignment::role, Collectors.counting()));

        System.out.println("\nTop 3 most popular roles:");
        roleCounts.entrySet().stream()
                .sorted(Map.Entry.<Role, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> System.out.printf("  %s: %d users\n", e.getKey().getName(), e.getValue()));
    }

    private void clear(Scanner sc, RBACSystem sys) {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    private void exit(Scanner sc, RBACSystem sys) {
        System.out.print("Save before exit? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            save(sc, sys);
        }
        System.out.println("Goodbye!");
    }

    private void save(Scanner sc, RBACSystem sys) {
        System.out.println("Save functionality not fully implemented");
    }

    private void load(Scanner sc, RBACSystem sys) {
        System.out.println("Load functionality not fully implemented");
    }

    private void addPermissionsToRole(Scanner sc, RBACSystem sys, String roleName) {
        while (true) {
            try {
                System.out.println("\nAdd permission (or empty name to finish):");
                System.out.print("Permission name: ");
                String name = sc.nextLine().trim();
                if (name.isEmpty()) break;

                System.out.print("Resource: ");
                String resource = sc.nextLine().trim().toLowerCase();

                System.out.print("Description: ");
                String desc = sc.nextLine().trim();

                Permission perm = new Permission(name, resource, desc);
                sys.getRoleManager().addPermissionToRole(roleName, perm);
                System.out.println("Permission added");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printUserTable(List<User> users) {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-20s | %-30s | %-30s\n", "Username", "Full Name", "Email");
        System.out.println("=".repeat(80));
        users.forEach(u -> System.out.printf("%-20s | %-30s | %-30s\n",
                u.username(), u.fullName(), u.email()));
        System.out.println("=".repeat(80));
        System.out.println("Total: " + users.size());
    }

    private void printRoleTable(List<Role> roles) {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-20s | %-30s | %-10s | %s\n", "Name", "Description", "Permissions", "ID");
        System.out.println("=".repeat(80));
        roles.forEach(r -> System.out.printf("%-20s | %-30s | %-10d | %s\n",
                r.getName(),
                truncate(r.getDescription(), 30),
                r.getPermissions().size(),
                truncate(r.getId(), 8)));
        System.out.println("=".repeat(80));
        System.out.println("Total: " + roles.size());
    }

    private void printAssignmentTable(List<RoleAssignment> assignments) {
        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-15s | %-15s | %-10s | %-8s | %s\n",
                "Username", "Role", "Type", "Status", "Assigned At");
        System.out.println("=".repeat(100));

        assignments.forEach(a -> {
            String status = a.isActive() ? "ACTIVE" : "INACTIVE";
            System.out.printf("%-15s | %-15s | %-10s | %-8s | %s\n",
                    a.user().username(),
                    truncate(a.role().getName(), 15),
                    a.assignmentType(),
                    status,
                    truncate(a.metadata().assignedAt(), 19));
        });
        System.out.println("=".repeat(100));
        System.out.println("Total: " + assignments.size());
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
    }

    public CommandParser getParser() {
        return parser;
    }
}