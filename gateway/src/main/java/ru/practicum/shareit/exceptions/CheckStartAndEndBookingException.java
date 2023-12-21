package ru.practicum.shareit.exceptions;

public class CheckStartAndEndBookingException extends RuntimeException {
    public CheckStartAndEndBookingException(final String mess) {
        super(mess);
    }
}