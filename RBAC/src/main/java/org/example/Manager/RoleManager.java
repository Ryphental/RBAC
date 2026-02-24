package org.example.Manager;

import org.example.Role;
import org.example.Permission;
import org.example.Filter.RoleFilter;
import org.example.Repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoleManager implements Repository<Role> {

    private final Map<String, Role> rolesById;
    private final Map<String, Role> rolesByName;
    private final Set<String> assignedRoleIds; // track roles assigned to users

    public RoleManager() {
        this.rolesById = new HashMap<>();
        this.rolesByName = new HashMap<>();
        this.assignedRoleIds = new HashSet<>();
    }

    @Override
    public void add(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (rolesById.containsKey(role.getId())) {
            throw new IllegalArgumentException("Role with ID " + role.getId() + " already exists");
        }

        if (rolesByName.containsKey(role.getName())) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
        }

        rolesById.put(role.getId(), role);
        rolesByName.put(role.getName(), role);
    }

    @Override
    public boolean remove(Role role) {
        if (role == null) return false;

        if (assignedRoleIds.contains(role.getId())) {
            throw new IllegalStateException("Cannot remove role " + role.getName() + " - it is assigned to users");
        }

        Role removed = rolesById.remove(role.getId());
        if (removed != null) {
            rolesByName.remove(role.getName());
            return true;
        }
        return false;
    }

    @Override
    public Optional<Role> findById(String id) {
        return Optional.ofNullable(rolesById.get(id));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(rolesById.values());
    }

    @Override
    public int count() {
        return rolesById.size();
    }

    @Override
    public void clear() {
        if (!assignedRoleIds.isEmpty()) {
            throw new IllegalStateException("Cannot clear roles - some roles are assigned to users");
        }
        rolesById.clear();
        rolesByName.clear();
    }

    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(rolesByName.get(name));
    }

    public List<Role> findByFilter(RoleFilter filter) {
        if (filter == null) return findAll();

        return rolesById.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<Role> findAll(RoleFilter filter, Comparator<Role> sorter) {
        Stream<Role> stream = rolesById.values().stream();

        if (filter != null) {
            stream = stream.filter(filter::test);
        }

        if (sorter != null) {
            stream = stream.sorted(sorter);
        }

        return stream.collect(Collectors.toList());
    }

    public boolean exists(String name) {
        return rolesByName.containsKey(name);
    }

    public void addPermissionToRole(String roleName, Permission permission) {
        Role role = rolesByName.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role with name " + roleName + " not found");
        }

        role.addPermission(permission);
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        Role role = rolesByName.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role with name " + roleName + " not found");
        }

        role.removePermission(permission);
    }

    public List<Role> findRolesWithPermission(String permissionName, String resource) {
        return rolesById.values().stream()
                .filter(role -> role.hasPermission(permissionName, resource))
                .collect(Collectors.toList());
    }

    public void markRoleAssigned(String roleId) {
        if (rolesById.containsKey(roleId)) {
            assignedRoleIds.add(roleId);
        }
    }

    public void markRoleUnassigned(String roleId) {
        assignedRoleIds.remove(roleId);
    }

    public boolean isRoleAssigned(String roleId) {
        return assignedRoleIds.contains(roleId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleManager that = (RoleManager) o;
        return Objects.equals(rolesById, that.rolesById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolesById);
    }

    @Override
    public String toString() {
        return "RoleManager{roles=" + rolesById.values() + "}";
    }
}
