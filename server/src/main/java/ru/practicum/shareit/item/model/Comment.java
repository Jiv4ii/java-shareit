package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@Accessors(chain = true)
public class Comment {
    @Column(name = "comment_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "comment_text")
    @NotBlank(message = "В запросе нет текста комментария.")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "created")
    private LocalDateTime created;
}