package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, int userId) {

        CheckBookingValid(bookingDto, userId);

        bookingDto.setBooker(userService.getUserById((userId)));
        bookingDto.setItem(itemService.getItemById(bookingDto.getItemId(),userId));
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
        if (getBooking(userId,bookingId).getStatus() == BookingStatus.APPROVED){
            throw new ChangeAfterApproveException("У данного бронирования нельзя менять статус");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingDtoMapper.toBookingDto(repository.save(booking));
    }

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

    public List<BookingDto> findAllBookingByUserId(int userId, String state) {
        if (userService.checkUser(userId)){
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        List<Booking> bookings;
        BookingStatus status = transcriptStatus(state);

        switch (status) {
            case ALL:
                bookings = repository.findByBookerId(userId);
                break;
            case PAST:
                bookings = repository.findByBookerIdAndEndBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = repository.getBookingCurrentByUserId(userId,
                        BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
                break;
            default:
                bookings = repository.findByBookerIdAndStatus(userId, status);
        }

        return getCollect(bookings);
    }

    public List<BookingDto> findAllBookingByOwnerId(int userId, String state){
        if (userService.checkUser(userId)){
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        List<Booking> bookings;
        BookingStatus status = transcriptStatus(state);

        switch (status) {
            case ALL:
                bookings = repository.getAllBookingByOwnerId(userId);
                break;
            case PAST:
                bookings = repository.getPastBookingByOwnerId(userId);
                break;
            case FUTURE:
                bookings = repository.getFutureBookingByOwnerId(userId);
                break;
            case CURRENT:
                bookings = repository.getBookingCurrentByOwnerId(userId,
                        BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
                break;
            default:
                bookings = repository.getBookingWithStatusByOwnerId(userId, status);
        }

        return getCollect(bookings);
    }

    private static List<BookingDto> getCollect(List<Booking> bookings) {
        return bookings
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .sorted((x, x1) -> x1.getStart().compareTo(x.getStart()))
                .collect(Collectors.toList());
    }

    private BookingStatus transcriptStatus(String status){
        try {
            BookingStatus statusTranscipted = BookingStatus.valueOf(status);
            return statusTranscipted;
        } catch (RuntimeException e) {
            throw new IllegalBookingStatusException("Unknown state: " + status);
        }
    }


    private void CheckBookingValid(BookingDto bookingDto, int userId) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        itemService.checkItem(bookingDto.getItemId());

        if (!itemService.checkItemAvailable(ItemDtoMapper.toItem(itemService.getItemById(bookingDto.getItemId(), userId)))) {
            throw new ItemNotAvailableException(
                    String.format("Вещь с id - " + bookingDto.getItemId() + " недоступна"));
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

        private void checkBooking(int id){
        if(repository.findById(id).isEmpty())
            throw new BookingNotFoundException("Бронирование с id - " + id + ", не найдено");
        }



    }


