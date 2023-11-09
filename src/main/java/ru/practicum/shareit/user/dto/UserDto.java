package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class UserDto {
    private int id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
