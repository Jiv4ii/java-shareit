package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, int userId) {
        return post("/", userId, null, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, int itemId, int userId) {
        String path = String.format("/%d", itemId);
        return patch(path, userId, null, itemDto);
    }

    public ResponseEntity<Object> getItemById(int itemId, int userId) {
        String path = String.format("/%d", itemId);
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllItemOwner(int userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItem(String text, int from, int size, int userId) {

        if (text.isBlank()) {
            return ResponseEntity.of(Optional.of(Collections.emptyList()));
        }

        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, int userId, int itemId) {
        String path = String.format("/%d/comment", itemId);
        return post(path, userId, null, commentDto);
    }

}