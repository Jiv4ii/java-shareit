package ru.practicum.shareit.exceptions;

public class NoValidUserException extends RuntimeException {
    public NoValidUserException(String message) {
        super(message);
    }
}
