package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private int id;

    @NotBlank(message = "В запросе отсутствует описание запроса")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}