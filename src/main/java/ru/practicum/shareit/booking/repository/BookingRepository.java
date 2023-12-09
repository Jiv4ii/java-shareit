package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerId(int userId);

    List<Booking> findByBookerIdAndEndBefore(int userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfter(int userId, LocalDateTime dateTime);

    List<Booking> findByBookerIdAndStatus(int userId, BookingStatus state);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.status = ?2 or b.status = ?3 or b.status = ?4)" +
            "and (b.end > current_timestamp and b.start < current_timestamp)")
    List<Booking> getBookingCurrentByUserId(
            int userId, BookingStatus waiting, BookingStatus rejected, BookingStatus current);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1")
    List<Booking> getAllBookingByOwnerId(int userId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < current_timestamp")
    List<Booking> getPastBookingByOwnerId(int userId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > current_timestamp")
    List<Booking> getFutureBookingByOwnerId(int userId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2")
    List<Booking> getBookingWithStatusByOwnerId(int userId, BookingStatus state);


    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and (b.status = ?2 or b.status = ?3 or b.status = ?4)" +
            "and (b.end > current_timestamp and b.start < current_timestamp)")
    List<Booking> getBookingCurrentByOwnerId(
            int userId, BookingStatus waiting, BookingStatus rejected, BookingStatus current);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and  b.end < current_timestamp " +
            "and b.status = ?2 " +
            "order by b.end desc ")
    Optional<List<Booking>> getLastBookingForItem(int itemId, BookingStatus approved);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = ?2 " +
            "and  b.start > current_timestamp " +
            "order by b.end ASC ")
    Optional<List<Booking>> getNextBookingForItem(int itemId, BookingStatus approved);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.end > current_timestamp and b.start < current_timestamp " +
            "and b.status = ?2 " +
            "order by b.end asc ")
    Optional<List<Booking>> getCurrentBookingForItem(int itemId, BookingStatus approved);


    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.end < current_timestamp ")
    Optional<List<Booking>> getBookingItemWhichTookUser(int itemId, int userId);

}
