package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CantCommentException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OwnerItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private RequestRepository itemRequestStorage;

    @Mock
    private ItemRepository itemStorage;

    @Mock
    private RequestService itemRequestService;

    @Mock
    private CommentRepository commentsStorage;

    @Mock
    private BookingRepository bookingStorage;

    @Mock
    private UserRepository userStorage;

    @Test
    public void createItem_withoutItemRequest_test() {
        int userId = 1;
        Item item = new Item()
                .setId(1);
        ItemDto expectedItemDto = ItemDtoMapper.toItemDto(item);
        when(userService.getUserById(userId))
                .thenReturn(new UserDto()
                        .setId(userId));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(expectedItemDto, userId);

        assertEquals("check", expectedItemDto, actualItemDto);
    }

    @Test
    public void createItem_withItemRequest_test() {
        int userId = 1;
        Item item = new Item()
                .setId(1)
                .setRequest(Request.builder()
                        .id(1)
                        .build());
        ItemDto expectedItemDto = ItemDtoMapper.toItemDto(item);
        when(userService.getUserById(userId)).thenReturn(new UserDto()
                .setId(userId));
        when(itemStorage.save(any(Item.class))).thenReturn(item);
        when(itemRequestService.getRequest(1)).thenReturn(Request.builder()
                .id(1)
                .build());

        ItemDto actualItemDto = itemService.createItem(expectedItemDto, userId);

        assertEquals("check", expectedItemDto, actualItemDto);
    }

    @Test
    public void updateItem_whenUserOwner_test() {
        int userId = 1;
        int itemId = 1;
        Item item = new Item()
                .setId(itemId)
                .setName("Name")
                .setDescription("Description")
                .setAvailable(false)
                .setOwner(new User()
                        .setId(itemId));

        ItemDto expectedItemDto = ItemDtoMapper.toItemDto(item);

        when(itemStorage.save(item)).thenReturn(item);
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        ItemDto actualItemDto = itemService.updateItem(expectedItemDto, itemId, userId);

        assertEquals("check", expectedItemDto, actualItemDto);
    }

    @Test
    public void updateItem_whenUserNotOwner_test() {
        int userId = 2;
        int itemId = 1;
        Item item = new Item()
                .setId(itemId)
                .setName("Name")
                .setDescription("Description")
                .setAvailable(false)
                .setOwner(new User()
                        .setId(1));

        ItemDto expectedItemDto = ItemDtoMapper.toItemDto(item);

        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        assertThrows(NotOwnerException.class, () -> itemService.updateItem(expectedItemDto, userId, itemId));
    }

    @Test
    public void getItemId_whenFoundItemTest() {
        int itemId = 1;
        Item item = new Item()
                .setId(itemId)
                .setName("Name")
                .setDescription("Description")
                .setAvailable(false);

        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        Item actualItem = itemService.getItem(itemId);

        assertEquals("check", item, actualItem);
    }


    @Test
    public void getItemById_whenUserOwnerTest() {
        int itemId = 1;
        int userId = 1;

        Item item = new Item()
                .setId(itemId)
                .setName("Name")
                .setDescription("Description")
                .setAvailable(false)
                .setOwner(new User()
                        .setId(1));

        ItemDto expectedItem = ItemDtoMapper.toItemDto(item);
        expectedItem.setComments(Collections.emptyList());

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);
        when(commentsStorage.findByItemId(itemId)).thenReturn(Collections.emptyList());
        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals("check", expectedItem, actualItem);
    }


    @Test
    public void findAllOwnersItemsTest() {
        int userId = 1;
        int from = 1;
        int size = 1;

        when(itemStorage.findByOwnerId(userId, PageRequest.of(from, size))).thenReturn(List.of(new Item()));

        List<OwnerItem> actualItemList = itemService.findAllUsersItems(userId, from, size);

        assertEquals("check", 1, actualItemList.size());
    }

    @Test
    public void searchItem_whenTextIsBlankTest() {
        String text = " ";


        assertTrue("check", itemService.searchItem(text).isEmpty());
    }


    @Test
    public void addCommentWhenFoundBookingTest() {
        int itemId = 1;
        int userId = 1;

        Comment comment = new Comment()
                .setId(1)
                .setText("Хорошая камера")
                .setItem(new Item())
                .setAuthor(new User())
                .setCreated(LocalDateTime.now());

        CommentDto expectedCommentDto = CommentDtoMapper.toCommentDto(comment);

        when(userService.getUserById(userId)).thenReturn(new UserDto()
                .setId(1));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingStorage.getBookingItemWhichTookUser(itemId, userId))
                .thenReturn((List.of(new Booking())));
        when(itemStorage.getReferenceById(itemId)).thenReturn(new Item());
        when(commentsStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualCommentDto = itemService.addComment(expectedCommentDto, itemId, itemId);

        assertEquals("check", expectedCommentDto, actualCommentDto);
    }

    @Test
    public void addComment_whenNotFoundBookingTest() {
        int itemId = 1;
        int userId = 1;

        CommentDto expectedCommentDto = new CommentDto()
                .setText("Мобила");

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingStorage.getBookingItemWhichTookUser(itemId, userId))
                .thenReturn(List.of());

        assertThrows(CantCommentException.class, () -> itemService.addComment(expectedCommentDto, itemId, itemId));
    }
}