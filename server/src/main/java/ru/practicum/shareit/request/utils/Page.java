package ru.practicum.shareit.request.utils;

import org.springframework.data.domain.PageRequest;

public class Page {
    public static PageRequest createPageRequest(int from, int size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}