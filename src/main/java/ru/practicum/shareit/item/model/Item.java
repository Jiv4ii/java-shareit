package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "items")
public class Item {
    @Column(name = "item_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "item_name", nullable = false)
    @NotBlank(message = "В запросе отсутствует имя вещи.")
    private String name;

    @Column(name = "item_description", length = 2000)
    @NotBlank(message = "В запросе отсутствует описание вещи.")
    private String description;

    @Column(name = "item_available")
    @NotNull(message = "В запросе отсутствует статус запроса к аренде.")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_request_id")
    @JsonProperty("requestId")
    private Request request;


}