package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserDtoMapper {


    public User dtoToUser(UserDto userDto) {
        return new User().setId(userDto.getId()).setName(userDto.getName()).setEmail(userDto.getEmail());
    }

    public UserDto userToDto(User user) {
        return new UserDto().setId(user.getId()).setName(user.getName()).setEmail(user.getEmail());
    }


}
