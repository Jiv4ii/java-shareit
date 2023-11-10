package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoValidEmailException;
import ru.practicum.shareit.exceptions.NoValidUserException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserDto createUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new NoValidUserException("Поля name и email - обязательны");
        }
        return UserDtoMapper.userToDto(repository.createUser(UserDtoMapper.dtoToUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        if (!repository.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User updatedUser = repository.getUserById(userId);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank() && !userDto.getEmail().equals(updatedUser.getEmail())) {
            if (repository.checkEmail(userDto.getEmail())) {
                throw new NoValidEmailException("Данный email уже используется");
            }
            repository.getEmails().remove(updatedUser.getEmail());
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatedUser.setName(userDto.getName());
        }

        return UserDtoMapper.userToDto(repository.updateUser(updatedUser));
    }

    public UserDto getUserDtoById(int id) {
        if (!repository.checkUser(id)) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return UserDtoMapper.userToDto(repository.getUserById(id));
    }

    public void deleteUser(int id) {
        repository.deleteUser(id);
    }

    public List<UserDto> getAllUsers() {
        return repository.getAllUsers()
                .stream()
                .map(UserDtoMapper::userToDto)
                .collect(Collectors.toList());
    }

    public boolean checkUser(int id) {
        return repository.checkUser(id);
    }

}
