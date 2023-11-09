package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.List;

public interface UserRepository {

    User createUser(User user);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(int id);

    User getUserById(int id);

    boolean checkUser(int id);

    boolean checkEmail(String email);

    HashSet<String> getEmails();


}
