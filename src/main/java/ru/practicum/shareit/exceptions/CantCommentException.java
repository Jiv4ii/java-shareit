package ru.practicum.shareit.exceptions;

public class CantCommentException extends RuntimeException {
    public CantCommentException(String message) {
        super(message);
    }
}
