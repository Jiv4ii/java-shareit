package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.RequestNotFoundException;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoMapper;


import ru.practicum.shareit.request.model.Request;

import ru.practicum.shareit.request.repository.RequestRepository;

import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @InjectMocks
    private RequestService itemRequestService;

    @Mock
    private RequestRepository itemRequestStorage;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemStorage;

    @Test
    public void addRequestTest() {
        int userId = 1;
        LocalDateTime dateTime = LocalDateTime.now();
        Request itemRequest = Request.builder()
                .description("Нужен телефон.")
                .created(dateTime)
                .requestor(new User()
                        .setId(userId))
                .build();
        RequestDto expectedItemRequestDto = RequestDtoMapper.toItemRequestDto(itemRequest);
        when(userService.getUserById(userId)).thenReturn(new UserDto().setId(userId));
        when(itemRequestStorage.save(any(Request.class))).thenReturn(itemRequest);

        RequestDto actualItemRequestDto = itemRequestService
                .addRequest(expectedItemRequestDto, userId);

        assertEquals("check", expectedItemRequestDto, actualItemRequestDto);
        ;
    }

    @Test
    public void getAllItemRequestTest() {
        int userId = 1;
        when(itemRequestStorage.findAllByRequestorId(userId)).thenReturn(List.of(new Request()));

        List<RequestDto> actualListItemRequest = itemRequestService.getAllRequests(userId);

        assertEquals("check", 1, actualListItemRequest.size());
    }

    @Test
    public void getAllOtherUsersItemRequest() {
        int userId = 1;
        int from = 5;
        int size = 5;

        when(itemRequestStorage.findAllByRequestorIdIsNotOrderByCreated(anyInt(), any(PageRequest.class)))
                .thenReturn(List.of(new Request()));

        List<RequestDto> actualListItemRequest =
                itemRequestService.getAllOtherUsersRequest(userId, from, size);

        assertEquals("check", 1, actualListItemRequest.size());
    }

    @Test
    public void getItemRequest_whenFoundItemRequest_test() {
        int requestId = 1;
        int userId = 1;

        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.of(Request.builder().id(1).build()));
        when(itemRequestService.getRequest(requestId)).thenReturn(Request.builder().id(1).build());
        when(itemStorage.findByRequestId(requestId)).thenReturn(List.of(new Item()));

        RequestDto actualListItemREquestDto = itemRequestService.getRequestDto(requestId, userId);

        assertEquals("check", requestId, actualListItemREquestDto.getId());
    }

    @Test
    public void getItemRequest_whenNotFoundItemRequest_test() {
        int requestId = 1;
        int userId = 1;

        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(RequestNotFoundException.class, () -> itemRequestService.getRequestDto(requestId, userId));
    }
}