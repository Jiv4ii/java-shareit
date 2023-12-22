package ru.practicum.shareit.exceptions;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(final String mess) {
        super(mess);
    }
}