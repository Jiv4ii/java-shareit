package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService itemRequestService;
    private final String USER_HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public RequestDto addRequest(@Valid @RequestBody RequestDto itemRequestDto,
                                 @RequestHeader(USER_HEADER_ID) int userId) {
        log.info("Запрос на создание запроса вещи");
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<RequestDto> getAllUsersItemRequest(@RequestHeader(USER_HEADER_ID) int userId) {
        log.info("Запрос на возврат списка запросов юзера: " + userId);
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllOtherUsersItemRequest(
            @RequestHeader(USER_HEADER_ID) int userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info(String.format("Запрос от юзера id = %d, " +
                "параметр from = %d, параметр size = %d", userId, from, size));
        return itemRequestService.getAllOtherUsersRequest(userId, from, size);
    }

    @GetMapping("{requestId}")
    public RequestDto getItemRequest(@PathVariable int requestId, @RequestHeader(USER_HEADER_ID) int userId) {
        log.info("Запрос на возврат запроса с id = " + requestId);
        return itemRequestService.getRequestDto(requestId, userId);
    }
}