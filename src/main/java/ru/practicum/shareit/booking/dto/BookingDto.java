package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BookingDto {

    private int id;

    @NotNull(message = "Отсутствует ИД вещи в запросе.")
    private Integer itemId;

    @NotNull(message = "Отсутствует начальная точка отсчета.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Отсутствует конечная точка отсчета.")
    @FutureOrPresent
    private LocalDateTime end;

    private Integer bookerId;

    private UserDto booker;

    private ItemDto item;

    private BookingStatus status;


}