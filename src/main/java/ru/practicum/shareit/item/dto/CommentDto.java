package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CommentDto {
    private int id;

    @NotBlank(message = "В запросе отсутствует текст отзыва.")
    @Length(max = 2000, message = "Превышена длина отзыва.")
    private String text;

    private String authorName;

    private LocalDateTime created;
}