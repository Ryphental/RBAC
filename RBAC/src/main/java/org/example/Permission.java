package org.example;

public record Permission(String name, String resource, String description) {

    public Permission {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        name = name.toUpperCase().trim();
        if (name.contains(" ")) {
            throw new IllegalArgumentException("Name cannot contain spaces");
        }

        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Resource cannot be empty");
        }
        resource = resource.toLowerCase().trim();
    }

    public String format() {
        return String.format("%s on %s: %s", name, resource, description);
    }

    public boolean matches(String namePattern, String resourcePattern) {
        boolean nameMatches = namePattern == null ||
                name.toLowerCase().contains(namePattern.toLowerCase()) ||
                name.toLowerCase().matches(namePattern.toLowerCase());

        boolean resourceMatches = resourcePattern == null ||
                resource.contains(resourcePattern.toLowerCase()) ||
                resource.matches(resourcePattern.toLowerCase());

        return nameMatches && resourceMatches;
    }
}
