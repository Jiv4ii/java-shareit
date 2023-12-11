package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public final class ItemDtoMapper {
    private ItemDtoMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto().setId(item.getId()).setName(item.getName()).setDescription(item.getDescription()).setAvailable(item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item().setId(itemDto.getId()).setName(itemDto.getName()).setDescription(itemDto.getDescription()).setAvailable(itemDto.getAvailable());
    }

    public static ItemDto toItemDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> commentDtos) {
        return new ItemDto().setId(item.getId()).setName(item.getName()).setDescription(item.getDescription()).setAvailable(item.getAvailable()).setLastBooking(lastBooking).setNextBooking(nextBooking).setComments(commentDtos);
    }

    public static ItemDto toItemDtoWithComments(Item item, List<CommentDto> commentDtos) {
        return new ItemDto().setId(item.getId()).setName(item.getName()).setDescription(item.getDescription()).setAvailable(item.getAvailable()).setComments(commentDtos);
    }
}