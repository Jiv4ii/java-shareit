package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.Request;

import java.util.Collections;

public class RequestDtoMapper {
    public static Request toItemRequest(RequestDto itemRequestDto) {
        return Request.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static RequestDto toItemRequestDto(Request itemRequest) {
        return RequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();
    }
}