package ru.practicum.shareit.request;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    final private int id;
    final private String name;
    final private String description;
}
