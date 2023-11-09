package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NoValidEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
public class UserInMemoryRepository implements UserRepository {
    private int id = 1;
    private final HashMap<Integer, User> users = new HashMap<>();
    private final HashSet<String> emails = new HashSet<>();

    @Override
    public User createUser(User user) {
        if (!emails.contains(user.getEmail())) {
            emails.add(user.getEmail());
            user.setId(id);
            users.put(id++, user);
            return user;
        }
        throw new NoValidEmailException("Данный email уже используется");
    }

    @Override
    public User updateUser(User user) {
        if (!checkEmail(user.getEmail())) {
            emails.add(user.getEmail());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(int id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public boolean checkUser(int id) {
        return users.containsKey(id);
    }

    @Override
    public boolean checkEmail(String email) {
        return emails.contains(email);
    }

    @Override
    public HashSet<String> getEmails() {
        return emails;
    }
}
