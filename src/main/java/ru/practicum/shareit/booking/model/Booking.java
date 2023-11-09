package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private int itemId;
    private int renterId;

}
