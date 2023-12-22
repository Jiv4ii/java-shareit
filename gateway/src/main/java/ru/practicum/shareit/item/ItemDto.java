package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Mark.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @NotBlank(groups = {Create.class}, message = "В запросе отсутствует имя вещи.")
    @Size(groups = {Create.class, Update.class},max = 255,
            message = "Максимальная длин имени вещи 255 симоволов.")
    private String name;

    @NotBlank(groups = {Create.class}, message = "В запросе отсутствует описание вещи.")
    @Size(groups = {Create.class, Update.class}, max = 2000,
            message = "Максимальная длина описания вещи 2000 симовлов.")
    private String description;

    @NotNull(groups = {Create.class}, message = "В запросе отсутствует статус запроса к аренде.")
    private Boolean available;

    @JsonProperty("requestId")
    private Long itemRequest;
}