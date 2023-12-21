package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.valid.Mark.Create;
import ru.practicum.shareit.valid.Mark.Update;

import javax.validation.constraints.Positive;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Запрос на создание юзера, name = {}, email = {} .", userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive int userId) {
        log.info("Запрос пользователя id = {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос всех пользователей.");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @RequestBody @Validated({Update.class}) UserDto userDto,
            @PathVariable @Positive int userId) {
        log.info("Обновление юзера id = {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive int userId) {
        log.info("Удаление юзера id = {}", userId);
        return userClient.deleteUser(userId);
    }
}