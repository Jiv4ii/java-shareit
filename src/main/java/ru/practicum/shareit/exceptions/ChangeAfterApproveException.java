package ru.practicum.shareit.exceptions;

public class ChangeAfterApproveException extends RuntimeException{
    public ChangeAfterApproveException(String message) {
        super(message);
    }
}
