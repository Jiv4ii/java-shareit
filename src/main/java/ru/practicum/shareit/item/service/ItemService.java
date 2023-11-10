package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemStorage;
    private final UserService userService;


    public ItemDto createItem(ItemDto itemDto, int userId) {
        if (!userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        Item item = ItemDtoMapper.toItem(itemDto);
        User owner = UserDtoMapper.dtoToUser(userService.getUserDtoById(userId));
        item.setOwner(owner);
        return ItemDtoMapper.toItemDto(itemStorage.createItem(item));
    }

    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        if (!userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        Item itemFromBd = itemStorage.getItemById(itemId);
        boolean checkUser = itemFromBd.getOwner().getId() == userId;

        if (!checkUser) {
            throw new NotOwnerException("Нет доступа к айтему с id = " + itemId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemFromBd.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemFromBd.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemFromBd.setAvailable(itemDto.getAvailable());
        }

        return ItemDtoMapper.toItemDto(itemStorage.update(itemFromBd));
    }

    public ItemDto getItemById(int id) {

        if (!itemStorage.findItemById(id)) {
            throw new ItemNotFoundException("Айтем с id = " + id + ", не найден");
        }

        return ItemDtoMapper.toItemDto(itemStorage.getItemById(id));
    }

    public List<ItemDto> findAllUsersItems(int userId) {

        if (!userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return itemStorage.getAllItem()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase().trim();
        return itemStorage.getAllItem()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}