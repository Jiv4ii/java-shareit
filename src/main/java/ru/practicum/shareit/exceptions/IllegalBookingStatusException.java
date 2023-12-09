package ru.practicum.shareit.exceptions;

public class IllegalBookingStatusException extends RuntimeException{
    public IllegalBookingStatusException(String message) {
        super(message);
    }
}
