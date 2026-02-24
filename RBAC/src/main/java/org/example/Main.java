package org.example;

import org.example.Filter.UserFilter;
import org.example.Filter.UserFilters;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<User> users = List.of(
                User.create("john_doe", "John Doe", "john@company.com"),
                User.create("jane_smith", "Jane Smith", "jane@company.com"),
                User.create("bob_wilson", "Bob Wilson", "bob@gmail.com"),
                User.create("alice_brown", "Alice Brown", "alice@company.co.uk")
        );

        UserFilter companyEmail = UserFilters.byEmailDomain("@company.com");
        UserFilter nameContainsJane = UserFilters.byFullNameContains("jane");
        UserFilter usernameContainsBob = UserFilters.byUsernameContains("bob");

        System.out.println("=== Company email users ===");
        users.stream()
                .filter(companyEmail::test)
                .forEach(u -> System.out.println(u.format()));

        System.out.println("\n=== Name contains 'jane' ===");
        users.stream()
                .filter(nameContainsJane::test)
                .forEach(u -> System.out.println(u.format()));

        System.out.println("\n=== Combined filter (company email AND name contains 'jane') ===");
        users.stream()
                .filter(companyEmail.and(nameContainsJane)::test)
                .forEach(u -> System.out.println(u.format()));

        System.out.println("\n=== Combined filter (company email OR username contains 'bob') ===");
        users.stream()
                .filter(companyEmail.or(usernameContainsBob)::test)
                .forEach(u -> System.out.println(u.format()));
    }
}