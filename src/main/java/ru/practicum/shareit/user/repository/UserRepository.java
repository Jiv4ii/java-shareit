package ru.practicum.shareit.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select i.owner from Item i" +
            " where i.id =?1 ")
    List<User> findOwnerByItemId(int itemId);
}
