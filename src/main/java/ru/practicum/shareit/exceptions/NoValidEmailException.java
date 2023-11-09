package ru.practicum.shareit.exceptions;

public class NoValidEmailException extends RuntimeException{
    public NoValidEmailException(String message) {
        super(message);
    }
}

