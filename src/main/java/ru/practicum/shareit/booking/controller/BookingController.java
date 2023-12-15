package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    private final String userHeaderId = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@Validated @RequestBody BookingDto bookingDto,
                                    @RequestHeader(userHeaderId) int userId) {
        log.info("Запрос на создание аренды вещи {} от юзера {}.", bookingDto.getItemId(), userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto addStatusBooking(@RequestHeader(userHeaderId) int userId,
                                       @RequestParam boolean approved,
                                       @PathVariable int bookingId) {
        log.info("Установить статус {} для запроса {} от юзера {}.", approved, bookingId, userId);
        return bookingService.addStatusBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(userHeaderId) int userId,
                                     @PathVariable int bookingId) {
        log.info("Запрос информации об аренде {} от юзера {}.", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingByUserId(@RequestHeader(userHeaderId) int userId,
                                                   @RequestParam(defaultValue = "ALL") String state, @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Вернуть список аренды {} юзера {}", state, userId);
        return bookingService.findAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingByOwnerId(@RequestHeader(userHeaderId) int userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Вернуть список аренды {} юзера {}", state, userId);
        return bookingService.findAllBookingByOwnerId(userId, state, from, size);
    }
}