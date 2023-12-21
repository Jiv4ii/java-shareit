package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdOrderByStartDesc(int userId, PageRequest pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime dateTime, PageRequest pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime dateTime, PageRequest pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int userId, BookingStatus state, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status in ?2 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start asc ")
    List<Booking> getBookingCurrentByUserId(
            int userId, List<BookingStatus> status, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc ")
    List<Booking> getAllBookingByOwnerId(int userId, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> getPastBookingByOwnerId(int userId, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> getFutureBookingByOwnerId(int userId, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> getBookingWithStatusByOwnerId(int userId, BookingStatus state, PageRequest pageable);


    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status in ?2 " +
            "and current_timestamp between b.start and b.end " +
            " order by b.start asc ")
    List<Booking> getBookingCurrentByOwnerId(
            int userId, List<BookingStatus> status, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and  b.end < current_timestamp " +
            "and b.status = ?2 " +
            "order by b.end asc ")
    List<Booking> getLastBookingForItem(int itemId, BookingStatus approved);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = ?2 " +
            "and  b.start > current_timestamp " +
            "order by b.end asc ")
    List<Booking> getNextBookingForItem(int itemId, BookingStatus approved);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and  current_timestamp between b.start and b.end " +
            "and b.status = ?2 " +
            "order by b.end asc ")
    List<Booking> getCurrentBookingForItem(int itemId, BookingStatus approved);


    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.end < current_timestamp ")
    List<Booking> getBookingItemWhichTookUser(int itemId, int userId);

}
