package org.example;

public class Main {
    public static void main(String[] args) {
        Permission readUsers = new Permission("READ", "users", "Can view user list");
        Permission writeUsers = new Permission("WRITE", "users", "Can create and edit users");
        Permission deleteUsers = new Permission("DELETE", "users", "Can delete users");

        Role admin = new Role("Administrator", "Full system access");

        admin.addPermission(readUsers);
        admin.addPermission(writeUsers);
        admin.addPermission(deleteUsers);

        System.out.println(admin.format());

        System.out.println("Has READ on users: " + admin.hasPermission("READ", "users"));
        System.out.println("Has DELETE on reports: " + admin.hasPermission("DELETE", "reports"));

        Role admin2 = new Role("Administrator", "Another admin", admin.getPermissions());
        System.out.println("Same ID? " + admin.equals(admin2));

        try {
            admin.getPermissions().add(new Permission("TEST", "test", "test"));
        } catch (UnsupportedOperationException e) {
            System.out.println("Permissions are immutable");
        }
    }
}