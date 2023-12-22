package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Mark.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(groups = {Create.class}, message = "Имя не должно быть пустым.")
    @Size(groups = {Create.class, Update.class}, max = 255,
            message = "Длина имени не должна превышать 255 симоволов.")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Отсутствует email.")
    @Email(groups = {Create.class, Update.class}, message = "Введен некорректный адрес электронной почты.")
    private String email;
}