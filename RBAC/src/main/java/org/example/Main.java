package org.example;

import org.example.Manager.UserManager;
import org.example.Filter.UserFilters;
import org.example.Sort.UserSorters;

public class Main {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();

        try {
            userManager.add(User.create("john_doe", "John Doe", "john@company.com"));
            userManager.add(User.create("jane_smith", "Jane Smith", "jane@company.com"));
            userManager.add(User.create("bob_wilson", "Bob Wilson", "bob@gmail.com"));


        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("=== Company email users ===");
        userManager.findByFilter(UserFilters.byEmailDomain("@company.com"))
                .forEach(u -> System.out.println(u.format()));

        System.out.println("\n=== All users sorted by username ===");
        userManager.findAll(UserFilters.byFullNameContains(""), UserSorters.byUsername())
                .forEach(u -> System.out.println(u.format()));

        try {
            userManager.update("john_doe", "John Updated", "john.new@company.com");
            System.out.println("\n=== After update ===");
            userManager.findByUsername("john_doe")
                    .ifPresent(u -> System.out.println("Updated: " + u.format()));
        } catch (IllegalArgumentException e) {
            System.out.println("Update error: " + e.getMessage());
        }

        System.out.println("\n=== Exists checks ===");
        System.out.println("john_doe exists: " + userManager.exists("john_doe"));
        System.out.println("unknown exists: " + userManager.exists("unknown"));

        System.out.println("Total users: " + userManager.count());
    }
}