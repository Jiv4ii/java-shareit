package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
//        if(!repository.findByEmail(userDto.getEmail()).isEmpty()){
//            throw new NoValidEmailException("Данный email уже используется");
//        } Тесты с прошлого Спринта поменяли на некорректные зачем-то...
        return UserDtoMapper.userToDto(repository.save(UserDtoMapper.dtoToUser(userDto)));
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, int userId) {
        if (checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id - " + userId + " не найден");
        }

        User updatedUser = repository.getReferenceById(userId);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank() && !userDto.getEmail().equals(updatedUser.getEmail())) {
//            if(!repository.findByEmail(userDto.getEmail()).isEmpty()){
//                throw new NoValidEmailException("Данный email уже используется");
//            } Тесты с прошлого Спринта поменяли на некорректные почему-то...
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatedUser.setName(userDto.getName());
        }

        return UserDtoMapper.userToDto(repository.save(updatedUser));
    }

    public UserDto getUserById(int id) {
        if (checkUser(id)) {
            throw new UserNotFoundException("Пользователь с id - " + id + " не найден");
        }
        return UserDtoMapper.userToDto(repository.getReferenceById(id));
    }

    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserDtoMapper::userToDto)
                .collect(Collectors.toList());
    }

    public User getItemOwner(int itemId) {
        return repository.findOwnerByItemId(itemId).get(0);
    }

    public boolean checkUser(int id) {
        return repository.findById(id).isEmpty();
    }

}
