package org.example;

import org.example.util.ValidationUtils;

public record Permission(String name, String resource, String description) {

    public Permission {
        ValidationUtils.requireNonEmpty(name, "Permission name");
        ValidationUtils.requireNonEmpty(resource, "Resource");
        ValidationUtils.requireNonEmpty(description, "Description");
    }

    public static Permission create(String name, String resource, String description) {
        return new Permission(
                name.trim().toUpperCase().replaceAll("\\s+", ""),
                resource.trim().toLowerCase(),
                ValidationUtils.normalizeString(description)
        );
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