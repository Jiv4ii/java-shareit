package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userStorage;

    @Test
    void createUserTest() {
        User user = new User()
                .setName("Дмитрий");
        when(userStorage.save(user)).thenReturn(user);

        UserDto saveUser = userService.createUser(UserDtoMapper.userToDto(user));

        assertEquals("check", user.getName(), saveUser.getName());
    }


    @Test
    void getUserByIdTest() {
        int userId = 0;
        UserDto userExpected = new UserDto();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        when(userStorage.getReferenceById(userId)).thenReturn(UserDtoMapper.dtoToUser(userExpected));

        UserDto actualUser = userService.getUserById(userId);

        assertEquals("check", userExpected, actualUser);
    }

    @Test
    void getUserByIdWhenUserNotFound() {
        int userId = 99;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsersTest() {
        List<User> expectedUsers = List.of(new User());
        when(userStorage.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers().stream()
                .map(UserDtoMapper::dtoToUser)
                .collect(Collectors.toList());

        assertEquals("check", expectedUsers, actualUsers);
    }

    @Test
    void deleteUserTest() {
        int userId = 1;
        userService.deleteUser(1);
        verify(userStorage, times(1)).deleteById(userId);
    }

    @Test
    void updateUserTest() {
        int userId = 1;
        User userExpected = new User()
                .setId(userId)
                .setName("Дмитрий")
                .setEmail("mail@mail.ru");
        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        when(userStorage.getReferenceById(userId)).thenReturn(new User()
                .setName("Владимир")
                .setId(1));
        when(userStorage.save(userExpected)).thenReturn(userExpected);

        User actualUserDto = UserDtoMapper.dtoToUser(userService.updateUser(UserDtoMapper.userToDto(userExpected), userId));

        assertEquals("check", userExpected, actualUserDto);
    }
}