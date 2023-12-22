package ru.practicum.shareit.exceptions;

public class UnsupportedBookingStatusException extends RuntimeException {
    public UnsupportedBookingStatusException(final String mess) {
        super(mess);
    }
}