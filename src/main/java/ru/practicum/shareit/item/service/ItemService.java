package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CantCommentException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OwnerItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.utils.Page;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestService requestService;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, int userId) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        Item item = ItemDtoMapper.toItem(itemDto);
        User owner = UserDtoMapper.dtoToUser(userService.getUserById(userId));
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            requestService.checkExistsRequest(itemDto.getRequestId());
            item.setRequest(requestService.getRequest(itemDto.getRequestId()));
        }
        return ItemDtoMapper.toItemDto(repository.save(item));
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        Item itemFromBd = repository.getReferenceById(itemId);
        boolean checkUser = itemFromBd.getOwner().getId() == userId;

        if (!checkUser) {
            throw new NotOwnerException("Нет доступа к итему с id - " + itemId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemFromBd.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemFromBd.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemFromBd.setAvailable(itemDto.getAvailable());
        }

        return ItemDtoMapper.toItemDto(repository.save(itemFromBd));
    }

    @Transactional
    public ItemDto getItemById(int itemId, int userId) {
        checkItem(itemId);
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentDtoMapper::toCommentDto)
                .collect(Collectors.toList());

        if (repository.getReferenceById(itemId).getOwner().getId() == userId) {

            BookingDto lastBooking = null;
            BookingDto nextBooking = null;
            if (getLastBookingForItem(itemId) != null) {
                lastBooking = BookingDtoMapper.toBookingDto(getLastBookingForItem(itemId));
            }
            if (getNextBookingForItem(itemId) != null) {
                nextBooking = BookingDtoMapper.toBookingDto(getNextBookingForItem(itemId));
            }

            if (lastBooking == null && getCurrentBookingForItem(itemId) != null) {
                lastBooking = BookingDtoMapper.toBookingDto(getCurrentBookingForItem(itemId));
            }

            return ItemDtoMapper.toItemDto(repository.getReferenceById(itemId), lastBooking, nextBooking, comments);
        }
        return ItemDtoMapper.toItemDtoWithComments(repository.getReferenceById(itemId), comments);
    }

    @Transactional
    public List<OwnerItem> findAllUsersItems(int userId, @Min(0) int from, @Min(1) int size) {

        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        PageRequest pageRequest = Page.createPageRequest(from, size);
        List<Item> items = repository.findByOwnerId(userId, pageRequest);
        List<OwnerItem> ownerItems = new ArrayList<>();

        for (Item item : items) {
            BookingDto lastBooking = null;
            BookingDto nextBooking = null;
            if (getLastBookingForItem(item.getId()) != null) {
                lastBooking = BookingDtoMapper.toBookingDto(getLastBookingForItem(item.getId()));
            }
            if (getNextBookingForItem(item.getId()) != null) {
                nextBooking = BookingDtoMapper.toBookingDto(getNextBookingForItem(item.getId()));
            }
            if (lastBooking == null && getCurrentBookingForItem(item.getId()) != null) {
                lastBooking = BookingDtoMapper.toBookingDto(getCurrentBookingForItem(item.getId()));
            }
            List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                    .stream()
                    .map(CommentDtoMapper::toCommentDto)
                    .collect(Collectors.toList());
            ownerItems.add(OwnerItemMapper.toOwnerItem(item, lastBooking, nextBooking, comments));
        }

        return ownerItems;
    }

    @Transactional
    public List<ItemDto> searchItem(String text) {

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase().trim();
        return repository.findAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Item getItem(int itemId) {
        return repository.getReferenceById(itemId);
    }

    public boolean checkItemAvailable(Item item) {
        return item.getAvailable();
    }

    public void checkItem(int id) {
        if (repository.findById(id).isEmpty()) {
            throw new ItemNotFoundException("Итем с id - " + id + ", не найден");
        }
    }

    @Transactional
    public Booking getLastBookingForItem(int itemId) {
        if (bookingRepository.getLastBookingForItem(itemId, BookingStatus.APPROVED).isEmpty()) {
            return null;
        }

        return bookingRepository.getLastBookingForItem(itemId, BookingStatus.APPROVED).get(0);
    }

    @Transactional
    public Booking getCurrentBookingForItem(int itemId) {
        if (bookingRepository.getCurrentBookingForItem(itemId, BookingStatus.APPROVED).isEmpty()) {
            return null;
        }

        return bookingRepository.getCurrentBookingForItem(itemId, BookingStatus.APPROVED).get(0);
    }

    @Transactional
    public Booking getNextBookingForItem(int itemId) {
        if (bookingRepository.getNextBookingForItem(itemId, BookingStatus.APPROVED).isEmpty()) {
            return null;
        }

        return bookingRepository.getNextBookingForItem(itemId, BookingStatus.APPROVED).get(0);
    }

    @Transactional
    public CommentDto addComment(CommentDto commentDto, int itemId, int userId) {
        checkItem(itemId);
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }
        List<Booking> bookings = bookingRepository.getBookingItemWhichTookUser(itemId, userId);

        if (bookings.isEmpty()) {
            throw new CantCommentException("Нельзя оставить комментарий вещи, не взяв её в аренду");
        }

        Comment comment = CommentDtoMapper.toComment(commentDto);
        comment.setItem(repository.getReferenceById(itemId));
        comment.setAuthor(UserDtoMapper.dtoToUser(userService.getUserById(userId)));
        comment.setCreated(LocalDateTime.now());
        return CommentDtoMapper.toCommentDto(commentRepository.save(comment));
    }


}