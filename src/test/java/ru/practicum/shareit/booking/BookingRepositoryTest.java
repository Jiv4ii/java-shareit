package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.utils.Page;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {
    @Autowired
    public BookingRepository bookingStorage;

    @Autowired
    private TestEntityManager em;

    @Test
    public void bookingRepositoryTest() {
        PageRequest pageRequest = Page.createPageRequest(0, 1);
        User owner = new User()
                .setName("Dmitrij")
                .setEmail("Dimon@mail.ru");
        User booker = new User()
                .setName("NeDmitrij")
                .setEmail("NeDimon@mail.ru");
        Item item = new Item()
                .setName("Item")
                .setDescription("Description")
                .setAvailable(true)
                .setOwner(owner);
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = new Booking()
                .setItem(item)
                .setBooker(booker)
                .setStart(dateTime)
                .setEnd(dateTime.plusDays(1))
                .setStatus(BookingStatus.WAITING);

        Assertions.assertEquals(0, owner.getId());
        em.persist(owner);
        Assertions.assertEquals(1, owner.getId());

        em.persist(booker);
        em.persist(item);
        em.persist(booking);

        List<Booking> ownerBookings = bookingStorage.getAllBookingByOwnerId(1, pageRequest);
        List<Booking> expected = Arrays.asList(booking);
        Assertions.assertEquals(expected, ownerBookings);

        booking.setStatus(BookingStatus.WAITING);

        List<Booking> ownerWaitings = bookingStorage.getBookingWithStatusByOwnerId(1, BookingStatus.WAITING, pageRequest);
        expected = Arrays.asList(booking);
        Assertions.assertEquals(expected, ownerWaitings);

        booking.setStatus(BookingStatus.APPROVED);
        expected = bookingStorage.getLastBookingForItem(item.getId(), BookingStatus.APPROVED);
        List<Booking> itemLastBooking = bookingStorage.getLastBookingForItem(1, BookingStatus.APPROVED);
    }

}
