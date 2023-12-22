package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UnsupportedBookingStatusException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constants.USER_HEADER_ID;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@Validated @RequestBody BookingDto bookingDto,
												@RequestHeader(USER_HEADER_ID) @Positive int userId) {
		log.info("Запрос на создание аренды вещи {} от юзера {}.", bookingDto.getItemId(), userId);
		return bookingClient.createBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> addStatusBooking(
			@RequestHeader(USER_HEADER_ID) @Positive int userId,
			@RequestParam boolean approved,
			@PathVariable @Positive int bookingId) {
		log.info("Установить статус {} для запроса {} от юзера {}.", approved, bookingId, userId);
		return bookingClient.addStatusBooking(userId, approved, bookingId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(USER_HEADER_ID) @Positive int userId,
												 @PathVariable @Positive int bookingId) {
		log.info("Запрос информации об аренде {} от юзера {}.", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> findAllBookingByUserId(
			@RequestHeader(USER_HEADER_ID) @Positive int userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(defaultValue = "0") @PositiveOrZero int from,
			@RequestParam(defaultValue = "10") @Positive int size) {
		log.info("Вернуть список аренды {} юзера {} c параметрами from {} и size {}", state, userId, from, size);
		BookingStatus status = BookingStatus.from(state)
				.orElseThrow(() -> new UnsupportedBookingStatusException("Unknown state: " + state));
		return bookingClient.findAllBookingByUserId(userId, status, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findAllBookingByOwnerId(
			@RequestHeader(USER_HEADER_ID) @Positive int userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(defaultValue = "0") @PositiveOrZero int from,
			@RequestParam(defaultValue = "10") @Positive int size) {
		log.info("Вернуть список аренды {} юзера {}", state, userId);
		BookingStatus status = BookingStatus.from(state)
				.orElseThrow(() -> new UnsupportedBookingStatusException("Unknown state: " + state));
		return bookingClient.findAllBookingByOwnerId(userId, status, from, size);
	}
}