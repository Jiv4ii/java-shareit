package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Mark.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    @NotBlank(groups = {Create.class}, message = "В запросе отсутствует описание запроса.")
    @Size(groups = {Class.class, Update.class}, max = 2000,
            message = "Максимальная длина описания 2000 символов.")
    private String description;
}