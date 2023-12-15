package ru.practicum.shareit.exceptionsHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private UserNotFoundException userNotFoundException;

    @Mock
    private NotOwnerException notOwnerException;

    @Mock
    private ItemNotFoundException itemNotFoundException;

    @Mock
    private BookingNotFoundException bookingNotFoundException;

    @Mock
    private NoAccessException noAccessException;

    @Mock
    private NoAccessBookingException noAccessBookingException;

    @Mock
    private ItemNotAvailableException itemNotAvailableException;

    @Mock
    private NotValidIntervalException notValidIntervalException;

    @Mock
    private IllegalBookingStatusException illegalBookingStatusException;

    @Mock
    private CheckBookerNotOwnerException checkBookerNotOwnerException;

    @Mock
    private ChangeAfterApproveException changeAfterApproveException;

    @Mock
    private CantCommentException cantCommentException;

    @Mock
    private RequestNotFoundException requestNotFoundException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleUserNotFoundException() {
        when(userNotFoundException.getMessage()).thenReturn("User not found");

        Map<String, String> response = globalExceptionHandler.handleNoCount(userNotFoundException);

        assertEquals("User not found", response.get("error"));
    }

    @Test
    void testHandleNotOwnerException() {
        when(notOwnerException.getMessage()).thenReturn("Not owner");

        Map<String, String> response = globalExceptionHandler.handleNoCount(notOwnerException);

        assertEquals("Not owner", response.get("error"));
    }

    @Test
    void testHandleItemNotFoundException() {
        when(itemNotFoundException.getMessage()).thenReturn("Item not found");

        Map<String, String> response = globalExceptionHandler.handleNoCount(itemNotFoundException);

        assertEquals("Item not found", response.get("error"));
    }

    @Test
    void testHandleBookingNotFoundException() {
        when(bookingNotFoundException.getMessage()).thenReturn("Booking not found");

        Map<String, String> response = globalExceptionHandler.handleNoCount(bookingNotFoundException);

        assertEquals("Booking not found", response.get("error"));
    }

    @Test
    void testHandleNoAccessException() {
        when(noAccessException.getMessage()).thenReturn("No access");

        Map<String, String> response = globalExceptionHandler.handleNoCount(noAccessException);

        assertEquals("No access", response.get("error"));
    }

    @Test
    void testHandleNoAccessBookingException() {
        when(noAccessBookingException.getMessage()).thenReturn("No access booking");

        Map<String, String> response = globalExceptionHandler.handleNoCount(noAccessBookingException);

        assertEquals("No access booking", response.get("error"));
    }

    @Test
    void testHandleItemNotAvailableException() {
        when(itemNotAvailableException.getMessage()).thenReturn("Item not available");

        Map<String, String> response = globalExceptionHandler.handleNoCount(itemNotAvailableException);

        assertEquals("Item not available", response.get("error"));
    }

    @Test
    void testHandleNotValidIntervalException() {
        when(notValidIntervalException.getMessage()).thenReturn("Not valid interval");

        Map<String, String> response = globalExceptionHandler.handleNoCount(notValidIntervalException);

        assertEquals("Not valid interval", response.get("error"));
    }

    @Test
    void testHandleIllegalBookingStatusException() {
        when(illegalBookingStatusException.getMessage()).thenReturn("Illegal booking status");

        Map<String, String> response = globalExceptionHandler.handleNoCount(illegalBookingStatusException);

        assertEquals("Illegal booking status", response.get("error"));
    }

    @Test
    void testHandleCheckBookerNotOwnerException() {
        when(checkBookerNotOwnerException.getMessage()).thenReturn("Check booker not owner");

        Map<String, String> response = globalExceptionHandler.handleNoCount(checkBookerNotOwnerException);

        assertEquals("Check booker not owner", response.get("error"));
    }

    @Test
    void testHandleChangeAfterApproveException() {
        when(changeAfterApproveException.getMessage()).thenReturn("Change after approve");

        Map<String, String> response = globalExceptionHandler.handleNoCount(changeAfterApproveException);

        assertEquals("Change after approve", response.get("error"));;;
    }

    @Test
    void testHandleCantCommentException() {
        when(cantCommentException.getMessage()).thenReturn("Can't comment");

        Map<String, String> response = globalExceptionHandler.handleNoCount(cantCommentException);

        assertEquals("Can't comment", response.get("error"));
    }

    @Test
    void testHandleRequestNotFoundException() {
        when(requestNotFoundException.getMessage()).thenReturn("Request not found");

        Map<String, String> response = globalExceptionHandler.handleNoCount(requestNotFoundException);

        assertEquals("Request not found", response.get("error"));
    }
}
