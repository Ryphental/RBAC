package org.example.Manager;

import org.example.*;
import org.example.Filter.AssignmentFilter;
import org.example.Repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssignmentManager implements Repository<RoleAssignment> {

    private final Map<String, RoleAssignment> assignmentsById;
    private final UserManager userManager;
    private final RoleManager roleManager;

    public AssignmentManager(UserManager userManager, RoleManager roleManager) {
        this.assignmentsById = new HashMap<>();
        this.userManager = userManager;
        this.roleManager = roleManager;
    }

    @Override
    public void add(RoleAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }

        if (!userManager.exists(assignment.user().username())) {
            throw new IllegalArgumentException("User " + assignment.user().username() + " does not exist");
        }

        if (!roleManager.exists(assignment.role().getName())) {
            throw new IllegalArgumentException("Role " + assignment.role().getName() + " does not exist");
        }

        boolean hasActiveAssignment = assignmentsById.values().stream()
                .filter(a -> a.user().equals(assignment.user()))
                .filter(a -> a.role().equals(assignment.role()))
                .anyMatch(RoleAssignment::isActive);

        if (hasActiveAssignment) {
            throw new IllegalStateException("User already has active assignment for role " + assignment.role().getName());
        }

        assignmentsById.put(assignment.assignmentId(), assignment);
        roleManager.markRoleAssigned(assignment.role().getId());
    }

    @Override
    public boolean remove(RoleAssignment assignment) {
        if (assignment == null) return false;

        RoleAssignment removed = assignmentsById.remove(assignment.assignmentId());
        if (removed != null) {
            roleManager.markRoleUnassigned(assignment.role().getId());
            return true;
        }
        return false;
    }

    @Override
    public Optional<RoleAssignment> findById(String id) {
        return Optional.ofNullable(assignmentsById.get(id));
    }

    @Override
    public List<RoleAssignment> findAll() {
        return new ArrayList<>(assignmentsById.values());
    }

    @Override
    public int count() {
        return assignmentsById.size();
    }

    @Override
    public void clear() {
        assignmentsById.clear();
    }

    public List<RoleAssignment> findByUser(User user) {
        return assignmentsById.values().stream()
                .filter(a -> a.user().equals(user))
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findByRole(Role role) {
        return assignmentsById.values().stream()
                .filter(a -> a.role().equals(role))
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        if (filter == null) return findAll();

        return assignmentsById.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter) {
        Stream<RoleAssignment> stream = assignmentsById.values().stream();

        if (filter != null) {
            stream = stream.filter(filter::test);
        }

        if (sorter != null) {
            stream = stream.sorted(sorter);
        }

        return stream.collect(Collectors.toList());
    }

    public List<RoleAssignment> getActiveAssignments() {
        return assignmentsById.values().stream()
                .filter(RoleAssignment::isActive)
                .collect(Collectors.toList());
    }

    public List<RoleAssignment> getExpiredAssignments() {
        return assignmentsById.values().stream()
                .filter(a -> !a.isActive())
                .filter(a -> a instanceof TemporaryAssignment)
                .collect(Collectors.toList());
    }

    public boolean userHasRole(User user, Role role) {
        return assignmentsById.values().stream()
                .filter(a -> a.user().equals(user))
                .filter(a -> a.role().equals(role))
                .anyMatch(RoleAssignment::isActive);
    }

    public boolean userHasPermission(User user, String permissionName, String resource) {
        return assignmentsById.values().stream()
                .filter(a -> a.user().equals(user))
                .filter(RoleAssignment::isActive)
                .map(RoleAssignment::role)
                .anyMatch(role -> role.hasPermission(permissionName, resource));
    }

    public Set<Permission> getUserPermissions(User user) {
        return assignmentsById.values().stream()
                .filter(a -> a.user().equals(user))
                .filter(RoleAssignment::isActive)
                .flatMap(a -> a.role().getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public void revokeAssignment(String assignmentId) {
        RoleAssignment assignment = assignmentsById.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment with ID " + assignmentId + " not found");
        }

        if (assignment instanceof PermanentAssignment perm) {
            perm.revoke();
        } else if (assignment instanceof TemporaryAssignment temp) {
            assignmentsById.remove(assignmentId);
        }
    }

    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate) {
        RoleAssignment assignment = assignmentsById.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment with ID " + assignmentId + " not found");
        }

        if (!(assignment instanceof TemporaryAssignment temp)) {
            throw new IllegalArgumentException("Assignment is not temporary");
        }

        temp.extend(newExpirationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentManager that = (AssignmentManager) o;
        return Objects.equals(assignmentsById, that.assignmentsById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentsById);
    }

    @Override
    public String toString() {
        return "AssignmentManager{assignments=" + assignmentsById.values() + "}";
    }
}
