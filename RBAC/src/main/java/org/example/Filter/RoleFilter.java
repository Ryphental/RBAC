package org.example.Filter;

import org.example.Role;
import org.example.Permission;

@FunctionalInterface
public interface RoleFilter {
    boolean test(Role role);

    default RoleFilter and(RoleFilter other) {
        return role -> this.test(role) && other.test(role);
    }

    default RoleFilter or(RoleFilter other) {
        return role -> this.test(role) || other.test(role);
    }

    default RoleFilter negate() {
        return role -> !this.test(role);
    }
}
