package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.valid.Mark.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constants.USER_HEADER_ID;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Validated({Create.class}) @RequestBody RequestDto requestDto,
                                             @RequestHeader(USER_HEADER_ID) @Positive int userId) {
        log.info("Запрос на создание запроса вещи от юзера id = {}", userId);
        return requestClient.addRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersItemRequest(@RequestHeader(USER_HEADER_ID) @Positive int userId) {
        log.info("Запрос на возврат списка запросов юзера: {}", userId);
        return requestClient.getAllUsersItemRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherUsersItemRequest(
            @RequestHeader(USER_HEADER_ID) @Positive int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос от юзера id = {}, параметр from = {}, параметр size = {}", userId, from, size);
        return requestClient.getAllOtherUsersItemRequest(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable @Positive int requestId,
                                                 @RequestHeader(USER_HEADER_ID) @Positive int userId) {
        log.info("Запрос на возврат запроса с id = {}, от юзера id = {}", requestId, userId);
        return requestClient.getItemRequest(userId, requestId);
    }
}