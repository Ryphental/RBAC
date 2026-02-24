package org.example.Manager;

import org.example.User;
import org.example.Filter.UserFilter;
import org.example.Repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserManager implements Repository<User> {

    private final Map<String, User> usersByUsername;
    private final Map<String, User> usersByEmail;

    public UserManager() {
        this.usersByUsername = new HashMap<>();
        this.usersByEmail = new HashMap<>();
    }

    @Override
    public void add(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (usersByUsername.containsKey(user.username())) {
            throw new IllegalArgumentException("User with username " + user.username() + " already exists");
        }

        if (usersByEmail.containsKey(user.email())) {
            throw new IllegalArgumentException("User with email " + user.email() + " already exists");
        }

        usersByUsername.put(user.username(), user);
        usersByEmail.put(user.email(), user);
    }

    @Override
    public boolean remove(User user) {
        if (user == null) return false;

        User removed = usersByUsername.remove(user.username());
        if (removed != null) {
            usersByEmail.remove(user.email());
            return true;
        }
        return false;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersByUsername.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersByUsername.values());
    }

    @Override
    public int count() {
        return usersByUsername.size();
    }

    @Override
    public void clear() {
        usersByUsername.clear();
        usersByEmail.clear();
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    public List<User> findByFilter(UserFilter filter) {
        if (filter == null) return findAll();

        return usersByUsername.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {
        Stream<User> stream = usersByUsername.values().stream();

        if (filter != null) {
            stream = stream.filter(filter::test);
        }

        if (sorter != null) {
            stream = stream.sorted(sorter);
        }

        return stream.collect(Collectors.toList());
    }

    public boolean exists(String username) {
        return usersByUsername.containsKey(username);
    }

    public void update(String username, String newFullName, String newEmail) {
        User existing = usersByUsername.get(username);
        if (existing == null) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        }

        if (!existing.email().equals(newEmail) && usersByEmail.containsKey(newEmail)) {
            throw new IllegalArgumentException("Email " + newEmail + " is already in use");
        }

        User updated = User.create(username, newFullName, newEmail);

        usersByEmail.remove(existing.email());

        usersByUsername.put(username, updated);
        usersByEmail.put(newEmail, updated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserManager that = (UserManager) o;
        return Objects.equals(usersByUsername, that.usersByUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usersByUsername);
    }

    @Override
    public String toString() {
        return "UserManager{users=" + usersByUsername.values() + "}";
    }
}
