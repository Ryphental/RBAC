package org.example;

import org.example.Filter.RoleFilter;
import org.example.Filter.RoleFilters;

public class Main {
    public static void main(String[] args) {
        Permission readUsers = new Permission("READ", "users", "Can read users");
        Permission writeUsers = new Permission("WRITE", "users", "Can write users");
        Permission deleteUsers = new Permission("DELETE", "users", "Can delete users");
        Permission readReports = new Permission("READ", "reports", "Can read reports");

        Role admin = new Role("Administrator", "Full access");
        admin.addPermission(readUsers);
        admin.addPermission(writeUsers);
        admin.addPermission(deleteUsers);
        admin.addPermission(readReports);

        Role moderator = new Role("Moderator", "Limited access");
        moderator.addPermission(readUsers);
        moderator.addPermission(readReports);

        Role viewer = new Role("Viewer", "View only");
        viewer.addPermission(readReports);

        java.util.List<Role> roles = java.util.List.of(admin, moderator, viewer);

        System.out.println("=== Roles with name containing 'Admin' ===");
        roles.stream()
                .filter(RoleFilters.byNameContains("Admin")::test)
                .forEach(r -> System.out.println(r.getName()));

        System.out.println("\n=== Roles with DELETE on users permission ===");
        roles.stream()
                .filter(RoleFilters.hasPermission("DELETE", "users")::test)
                .forEach(r -> System.out.println(r.getName()));

        System.out.println("\n=== Roles with at least 3 permissions ===");
        roles.stream()
                .filter(RoleFilters.hasAtLeastNPermissions(3)::test)
                .forEach(r -> System.out.println(r.getName() + ": " + r.getPermissions().size() + " permissions"));

        System.out.println("\n=== Combined filter (has READ users AND at least 2 permissions) ===");
        RoleFilter readUsersFilter = RoleFilters.hasPermission("READ", "users");
        RoleFilter min2Perms = RoleFilters.hasAtLeastNPermissions(2);

        roles.stream()
                .filter(readUsersFilter.and(min2Perms)::test)
                .forEach(r -> System.out.println(r.getName()));
    }
}