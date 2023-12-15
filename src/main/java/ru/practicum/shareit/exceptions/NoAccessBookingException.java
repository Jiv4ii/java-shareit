package ru.practicum.shareit.exceptions;

public class NoAccessBookingException extends RuntimeException {
    public NoAccessBookingException(String message) {
        super(message);
    }
}
