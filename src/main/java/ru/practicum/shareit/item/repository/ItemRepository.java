package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(int userId, PageRequest pageable);

    @Query("select i from Item i " +
            "where i.request.id = ?1"
    )
    List<Item> findByRequestId(int itemRequestId);


}

