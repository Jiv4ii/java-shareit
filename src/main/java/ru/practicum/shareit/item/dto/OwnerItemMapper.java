package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OwnerItem;

import java.util.List;

public final class OwnerItemMapper {

    public static OwnerItem toOwnerItem(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> commentDtos) {
        return new OwnerItem()
                .setName(item.getName())
                .setId(item.getId())
                .setDescription(item.getDescription())
                .setAvailable(item.getAvailable())
                .setLastBooking(lastBooking)
                .setNextBooking(nextBooking)
                .setComments(commentDtos)
                ;


    }
}
