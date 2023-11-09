package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ItemDto {

    private long id;

    @NotBlank(message = "В запросе отсутствует имя вещи.")
    private String name;

    @NotBlank(message = "В запросе отсутствует описание вещи.")
    private String description;

    @NotNull(message = "В запросе отсутствует статус запроса к аренде.")
    private Boolean available;

}