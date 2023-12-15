package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

@Data
@Accessors(chain = true)
public class OwnerItem {

    private int id;

    private String name;


    private String description;


    private Boolean available;

    private BookingDto lastBooking;
    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
