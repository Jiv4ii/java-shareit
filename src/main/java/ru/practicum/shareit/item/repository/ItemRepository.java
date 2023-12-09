package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item);

    Item update(Item item);

    Item getItemById(int id);

    List<Item> getAllItem();

    boolean findItemById(int id);


}
