package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoCount(final UserNotFoundException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoCount(final NotOwnerException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoCount(final ItemNotFoundException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoCount(final BookingNotFoundException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoCount(final NoAccessException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoCount(final NoAccessBookingException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoCount(final ItemNotAvailableException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoCount(final NotValidIntervalException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoCount(final IllegalBookingStatusException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoCount(final CheckBookerNotOwnerException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoCount(final ChangeAfterApproveException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoCount(final CantCommentException e) {
        log.info(e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
