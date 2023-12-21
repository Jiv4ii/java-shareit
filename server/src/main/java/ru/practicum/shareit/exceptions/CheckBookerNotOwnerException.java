package ru.practicum.shareit.exceptions;

public class CheckBookerNotOwnerException extends RuntimeException {
    public CheckBookerNotOwnerException(String message) {
        super(message);
    }
}
