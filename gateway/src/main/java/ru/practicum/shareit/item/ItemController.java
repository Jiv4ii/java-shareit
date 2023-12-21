package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.valid.Mark.Create;
import ru.practicum.shareit.valid.Mark.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constants.USER_HEADER_ID;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_HEADER_ID) @Positive int userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос создания вещи юзера id = {}", userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable @Positive int itemId,
            @RequestHeader(USER_HEADER_ID) @Positive int userId,
            @RequestBody @Validated({Update.class}) ItemDto itemDto) {
        log.info("Обновление вещи пользователя id = {}", userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_HEADER_ID) @Positive int userId,
                                              @PathVariable @Positive int itemId) {
        log.info("Запрос вещи id = {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemOwner(
            @RequestHeader(USER_HEADER_ID) @Positive int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос всех вещей юзера id = {}, from = {}, size = {}", userId, from, size);
        return itemClient.getAllItemOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader(USER_HEADER_ID) @Positive int userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос от юзера id = {}, поиск вещи по тексту = {}, from = {}, size = {}.", userId,
                text, from, size);

        return itemClient.searchItem(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_HEADER_ID) @Positive int userId,
                                             @PathVariable @Positive int itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Добавить комментарий от юзера id = {}, text = {}", userId, commentDto.getText());
        return itemClient.addComment(commentDto, userId, itemId);
    }
}