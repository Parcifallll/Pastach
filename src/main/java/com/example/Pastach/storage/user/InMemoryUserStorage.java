package com.example.Pastach.storage.user;

import com.example.Pastach.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    int nextId = 1;

    @Override
    public Map<Integer, User> findAll() {
        return users;
    }

    @Override
    public User updateById(User user, int userId) {
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User create(User user) {
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> deleteById(int userId) {
        User deletedUser = users.get(userId);
        users.remove(userId);
        return Optional.ofNullable(deletedUser);

    }
}
