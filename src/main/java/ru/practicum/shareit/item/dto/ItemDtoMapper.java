package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public final class ItemDtoMapper {

    private ItemDtoMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto().setId(item.getId()).setName(item.getName()).setDescription(item.getDescription()).setAvailable(item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item().setName(itemDto.getName()).setDescription(itemDto.getDescription()).setAvailable(itemDto.getAvailable());
    }
}