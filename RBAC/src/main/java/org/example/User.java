package org.example;

public record User(String username, String fullName, String email) {

    public User {
        if (username == null || username.isBlank()
                || fullName == null || fullName.isBlank()
                || email == null || email.isBlank()) {
            throw new IllegalArgumentException("All fields must be filled out");
        }

        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("The username must be 3-20 characters long: letters, numbers, _");
        }

        if (!email.matches("^.+@.+\\..+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static User create(String username, String fullName, String email) {
        return new User(username, fullName, email);
    }

    public String format() {
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}
