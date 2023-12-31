package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingStorage;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Test
    void createBooking_whenNotValidDateTest() {
        int userId = 1;
        BookingDto bookingDto = new BookingDto()
                .setItemId(1)
                .setStart(LocalDateTime.of(2023, 12, 3, 12, 0))
                .setEnd(LocalDateTime.of(2023, 12, 2, 12, 0));

        when(itemService.checkItemAvailable(any())).thenReturn(true);
        assertThrows(NotValidIntervalException.class,
                () -> bookingService.createBooking(bookingDto, userId));
    }

    @Test
    void createBooking_whenNotAvailableItemTest() {
        int userId = 1;
        BookingDto bookingDto = new BookingDto()
                .setItemId(1)
                .setStart(LocalDateTime.of(2023, 12, 3, 12, 0))
                .setEnd(LocalDateTime.of(2023, 12, 2, 12, 0));

        when(itemService.checkItemAvailable(itemService.getItem(1))).thenReturn(false);

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(bookingDto, userId));
    }

    @Test
    void createBooking_whenUserOwnerItemTest() {
        int userId = 1;
        BookingDto bookingDto = new BookingDto()
                .setItemId(1)
                .setStart(LocalDateTime.of(2023, 12, 2, 12, 0))
                .setEnd(LocalDateTime.of(2023, 12, 3, 12, 0));

        Item item = new Item()
                .setOwner(new User()
                        .setId(1));

        when(itemService.checkItemAvailable(any(Item.class))).thenReturn(true);
        when(itemService.getItem(bookingDto.getItemId())).thenReturn(item);

        assertThrows(CheckBookerNotOwnerException.class,
                () -> bookingService.createBooking(bookingDto, userId));
    }


    @Test
    void createBookingTest() {
        int userId = 2;
        int itemId = 1;
        UserDto booker = new UserDto().setId(userId);
        Item item = new Item().setId(itemId).setOwner(new User().setId(1));
        BookingDto expectedBookingDto = new BookingDto()
                .setId(0)
                .setItemId(itemId)
                .setStart(LocalDateTime.of(2023, 12, 2, 12, 0))
                .setEnd(LocalDateTime.of(2023, 12, 3, 12, 0))
                .setBooker(booker)
                .setBookerId(booker.getId())
                .setItem(ItemDtoMapper.toItemDto(item))
                .setStatus(BookingStatus.WAITING);

        when(itemService.checkItemAvailable(any(Item.class))).thenReturn(true);
        when(itemService.getItem(expectedBookingDto.getItemId())).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(booker);
        when(itemService.getItemById(expectedBookingDto.getItemId(), userId))
                .thenReturn(ItemDtoMapper.toItemDto(item));
        when(bookingStorage.save(BookingDtoMapper.toBooking(expectedBookingDto)))
                .thenReturn(BookingDtoMapper.toBooking(expectedBookingDto));

        BookingDto actualBookingDto = bookingService.createBooking(expectedBookingDto, userId);

        assertEquals("check", expectedBookingDto, actualBookingDto);
    }

    @Test
    void addStatusBookingWhenNotFoundBookingTest() {
        int userId = 1;
        int bookingId = 1;
        boolean isApproved = false;

        User user = new User()
                .setId(2);

        Item item = new Item()
                .setOwner(user);

        Booking booking = new Booking().setItem(item);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.addStatusBooking(userId, bookingId, isApproved));
    }


    @Test
    void addStatusBooking_whenUserNotOwnerItemTest() {
        int userId = 1;
        int bookingId = 1;
        boolean isApproved = false;

        User user = new User()
                .setId(2);

        Item item = new Item()
                .setOwner(user);

        Booking booking = new Booking()
                .setItem(item);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.getReferenceById(bookingId)).thenReturn(booking);
        assertThrows(NoAccessException.class,
                () -> bookingService.addStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    void addStatusBooking_whenNotValidBookingStatus() {
        int userId = 1;
        int bookingId = 1;
        boolean isApproved = false;

        User user = new User()
                .setId(userId);

        Item item = new Item()
                .setOwner(user);


        Booking booking = new Booking()
                .setItem(item)
                .setStatus(BookingStatus.APPROVED)
                .setBooker(user);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.getReferenceById(bookingId)).thenReturn(booking);

        assertThrows(ChangeAfterApproveException.class,
                () -> bookingService.addStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    void addStatusBooking_whenBookingStatusIsApproved() {
        int userId = 1;
        int bookingId = 1;
        boolean isApproved = true;

        User user = new User()
                .setId(userId);

        Item item = new Item()
                .setOwner(user);

        Booking booking = new Booking()
                .setBooker(new User().setId(1))
                .setItem(item)
                .setStatus(BookingStatus.WAITING);

        Booking expectedBooking = new Booking()
                .setBooker(new User().setId(1))
                .setItem(item)
                .setStatus(BookingStatus.APPROVED);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.getReferenceById(anyInt())).thenReturn(booking);
        when(bookingStorage.save(expectedBooking)).thenReturn(expectedBooking);

        BookingDto actualBooking = bookingService.addStatusBooking(userId, bookingId, isApproved);
        assertEquals("check", BookingDtoMapper.toBookingDto(expectedBooking), actualBooking);
    }


    @Test
    void getBookingById_whenUserNotBookerOrNotOwnerTest() {
        int userId = 2;
        int bookingId = 1;

        User user = new User()
                .setId(1);

        Item item = new Item().setOwner(new User().setId(3));

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.getReferenceById(bookingId)).thenReturn(booking);

        assertThrows(NoAccessException.class, () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void getBookingById_whenUserBookerTest() {
        int userId = 2;
        int bookingId = 1;

        User user = new User()
                .setId(1);

        Item item = new Item()
                .setOwner(new User()
                        .setId(userId));

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.getReferenceById(bookingId)).thenReturn(booking);

        BookingDto actualBooking = bookingService.getBooking(userId, bookingId);

        assertEquals("Check", BookingDtoMapper.toBookingDto(booking), actualBooking);
    }

    @Test
    void findAllBookingByUserId_whenNotValidStatusTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        String state = "ALL1";

        assertThrows(IllegalBookingStatusException.class,
                () -> bookingService.findAllBookingByUserId(userId, state, from, size));
    }

    @Test
    void findAllBookingByUserId_whenStatusAllTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "ALL";

        when(bookingStorage.findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
    }

    @Test
    void findAllBookingByUserId_whenStatusPastTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "PAST";

        when(bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(anyInt(),
                any(LocalDateTime.class), any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId,
                Arrays.asList(BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED), PageRequest.of(page, size));
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    void findAllBookingByUserId_whenStatusCurrentTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "CURRENT";

        when(bookingStorage.getBookingCurrentByUserId(userId, Arrays.asList(
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED), PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    void findAllBookingByUserId_whenStatusFutureTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "FUTURE";

        when(bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId, Arrays.asList(
                BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED), PageRequest.of(page, size));
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    void findAllBookingByUserId_whenStatusDefaultTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "WAITING";

        when(bookingStorage.findByBookerIdAndStatusOrderByStartDesc(anyInt(),
                any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId, Arrays.asList(
                BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED), PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
    }


    @Test
    void findAllBookingByOwnerId_whenStatusAllTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "ALL";

        when(bookingStorage.getAllBookingByOwnerId(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

    }

    @Test
    void findAllBookingByOwnerId_whenStatusPastTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "PAST";

        when(bookingStorage.getPastBookingByOwnerId(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        Mockito.verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId, Arrays.asList(
                        BookingStatus.APPROVED,
                        BookingStatus.WAITING,
                        BookingStatus.REJECTED),
                PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyInt(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void findAllBookingByOwnerId_whenStatusFutureTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "FUTURE";

        when(bookingStorage.getFutureBookingByOwnerId(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId, Arrays.asList(
                        BookingStatus.APPROVED,
                        BookingStatus.WAITING,
                        BookingStatus.REJECTED),
                PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyInt(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void findAllBookingByOwnerId_whenStatusCurrentTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "CURRENT";

        when(bookingStorage.getBookingCurrentByOwnerId(userId, Arrays.asList(
                        BookingStatus.APPROVED,
                        BookingStatus.WAITING,
                        BookingStatus.REJECTED),
                PageRequest.of(page, size))).thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyInt(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void findAllBookingByOwnerId_whenStatusDefautTest() {
        int userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "WAITING";

        when(bookingStorage.getBookingWithStatusByOwnerId(anyInt(),
                any(BookingStatus.class), any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId, Arrays.asList(
                        BookingStatus.APPROVED,
                        BookingStatus.WAITING,
                        BookingStatus.REJECTED),
                PageRequest.of(page, size));
    }


}