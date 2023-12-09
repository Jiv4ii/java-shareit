package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

@Component
public final class BookingDtoMapper {


    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto()
                .setId(booking.getId())
                .setStart(booking.getStart())
                .setEnd(booking.getEnd())
                .setItem(ItemDtoMapper.toItemDto(booking.getItem()))
                .setBooker(UserDtoMapper.userToDto(booking.getBooker()))
                .setStatus(booking.getStatus())
                .setBookerId(booking.getBooker().getId());
    }

    public static Booking toBooking(BookingDto itemDto) {
        return new Booking()
                .setId(itemDto.getId())
                .setStart(itemDto.getStart())
                .setEnd(itemDto.getEnd())
                .setBooker(UserDtoMapper.dtoToUser(itemDto.getBooker()))
                .setItem(ItemDtoMapper.toItem(itemDto.getItem()))
                .setStatus(itemDto.getStatus());
    }

    public static BookingDto toPastBookingDto(Booking booking) {
        return new BookingDto()
                .setId(booking.getId())
                .setItem(ItemDtoMapper.toItemDto(booking.getItem()))
                .setBooker(UserDtoMapper.userToDto(booking.getBooker()))
                .setStatus(booking.getStatus())
                .setBookerId(booking.getBooker().getId());
    }


}
