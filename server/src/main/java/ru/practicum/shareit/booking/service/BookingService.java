package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.utils.Page;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, int userId) {

        checkBookingValid(bookingDto, userId);

        bookingDto.setBooker(userService.getUserById((userId)));
        bookingDto.setItem(itemService.getItemById(bookingDto.getItemId(), userId));
        bookingDto.setStatus(BookingStatus.WAITING);

        return BookingDtoMapper.toBookingDto(
                repository.save(BookingDtoMapper.toBooking(bookingDto)));
    }


    @Transactional
    public BookingDto addStatusBooking(int userId, int bookingId, boolean approved) {
        checkBooking(bookingId);
        Booking booking = repository.getReferenceById(bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NoAccessException("Информация о бронировании с id - " + booking.getItem().getId() + ", недоступна для пользователя id - " + userId);
        }
        if (getBooking(userId, bookingId).getStatus() == BookingStatus.APPROVED) {
            throw new ChangeAfterApproveException("У данного бронирования нельзя менять статус");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingDtoMapper.toBookingDto(repository.save(booking));
    }

    @Transactional
    public BookingDto getBooking(int userId, int bookingId) {
        userService.checkUser(userId);
        checkBooking(bookingId);
        Booking booking = repository.getReferenceById(bookingId);

        if (booking.getBooker().getId() == userId
                || booking.getItem().getOwner().getId() == userId) {
            return BookingDtoMapper.toBookingDto(repository.getReferenceById(bookingId));
        }
        throw new NoAccessException("Информация о бронировании с id - " + booking.getItem().getId() + ", недоступна для пользователя id - " + userId);
    }

    @Transactional
    public List<BookingDto> findAllBookingByUserId(int userId, String state, @Min(0) int from, @Min(1) int size) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        List<Booking> bookings;
        BookingStatus status = transcriptStatus(state);
        PageRequest pageRequest = Page.createPageRequest(from, size);

        switch (status) {
            case ALL:
                bookings = repository.findByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookings = repository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = repository.getBookingCurrentByUserId(userId,
                        Arrays.asList(BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED), pageRequest);
                break;
            default:
                bookings = repository.findByBookerIdAndStatusOrderByStartDesc(userId, status, pageRequest);
        }

        return bookings
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookingDto> findAllBookingByOwnerId(int userId, String state, @Min(0) int from, @Min(1) int size) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        List<Booking> bookings;
        BookingStatus status = transcriptStatus(state);
        PageRequest pageRequest = Page.createPageRequest(from, size);

        switch (status) {
            case ALL:
                bookings = repository.getAllBookingByOwnerId(userId, pageRequest);
                break;
            case PAST:
                bookings = repository.getPastBookingByOwnerId(userId, pageRequest);
                break;
            case FUTURE:
                bookings = repository.getFutureBookingByOwnerId(userId, pageRequest);
                break;
            case CURRENT:
                bookings = repository.getBookingCurrentByOwnerId(userId,
                        Arrays.asList(BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED), pageRequest);
                break;
            default:
                bookings = repository.getBookingWithStatusByOwnerId(userId, status, pageRequest);
        }

        return bookings
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private static List<BookingDto> getCollect(List<Booking> bookings) {
        return bookings
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .sorted((x, x1) -> x1.getStart().compareTo(x.getStart()))
                .collect(Collectors.toList());
    }

    private BookingStatus transcriptStatus(String status) {
        try {
            BookingStatus statusTranscipted = BookingStatus.valueOf(status);
            return statusTranscipted;
        } catch (RuntimeException e) {
            throw new IllegalBookingStatusException("Unknown state: " + status);
        }
    }


    private void checkBookingValid(BookingDto bookingDto, int userId) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        itemService.checkItem(bookingDto.getItemId());

        if (!itemService.checkItemAvailable(itemService.getItem(bookingDto.getItemId()))) {
            throw new ItemNotAvailableException("Вещь с id - " + bookingDto.getItemId() + " недоступна");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new NotValidIntervalException("Некорректный срок аренды");
        }


        Item item = itemService.getItem(bookingDto.getItemId());

        if (item.getOwner().getId() == userId) {
            throw new CheckBookerNotOwnerException("Нельзя взять свою вещь в аренду");
        }

    }

    private void checkBooking(int id) {
        if (repository.findById(id).isEmpty())
            throw new BookingNotFoundException("Бронирование с id - " + id + ", не найдено");
    }


}


