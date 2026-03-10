package org.example;

import org.example.util.ValidationUtils;

public record User(String username, String fullName, String email) {

    public User {
        ValidationUtils.requireValidUsername(username);
        ValidationUtils.requireNonEmpty(fullName, "Full name");
        ValidationUtils.requireValidEmail(email);
    }

    public static User create(String username, String fullName, String email) {
        return new User(username.trim(),
                ValidationUtils.normalizeString(fullName),
                email.trim().toLowerCase());
    }

    public String format() {
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}