package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.utils.Page;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestService {
    private final RequestRepository itemRequestStorage;
    private final UserService userService;
    private final ItemRepository itemStorage;

    @Transactional
    public RequestDto addRequest(RequestDto itemRequestDto, int userId) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + ", не найден");
        }
        Request itemRequest = RequestDtoMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(UserDtoMapper.dtoToUser(userService.getUserById(userId)));
        itemRequest.setCreated(LocalDateTime.now());
        return RequestDtoMapper.toItemRequestDto(itemRequestStorage.save(itemRequest));
    }

    @Transactional
    public List<RequestDto> getAllRequests(int userId) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + ", не найден");
        }
        userService.checkUser(userId);
        return getListRequestDto(itemRequestStorage.findAllByRequestorId(userId));
    }

    @Transactional
    public List<RequestDto> getAllOtherUsersRequest(int userId, @Min(0) int from, @Min(1) int size) {
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + ", не найден");
        }
        log.info("From = " + from + " Size = " + size);
        return getListRequestDto(itemRequestStorage
                .findAllByRequestorIdIsNotOrderByCreated(userId, Page.createPageRequest(from, size)));
    }

    private List<RequestDto> getListRequestDto(List<Request> itemRequests) {
        List<RequestDto> listItemRequestDto = itemRequests.stream()
                .map(RequestDtoMapper::toItemRequestDto)
                .collect(Collectors.toList());

        for (RequestDto itemRequestDto : listItemRequestDto) {
            itemRequestDto.setItems(itemStorage.findByRequestId(itemRequestDto.getId())
                    .stream()
                    .map(ItemDtoMapper::toItemDto)
                    .collect(Collectors.toList()));
        }

        return listItemRequestDto;
    }

    @Transactional
    public RequestDto getRequestDto(int requestId, int userId) {
        checkExistsRequest(requestId);
        if (userService.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + ", не найден");
        }
        Request itemRequest = getRequest(requestId);
        RequestDto itemRequestDto = RequestDtoMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemStorage.findByRequestId(itemRequestDto.getId()).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }

    public void checkExistsRequest(int requestId) {
        if (itemRequestStorage.findById(requestId).isEmpty()) {
            throw new RequestNotFoundException("Запроса с id " + requestId + " не существует");
        }
    }

    public Request getRequest(int requestId) {
        return itemRequestStorage.getReferenceById(requestId);
    }
}