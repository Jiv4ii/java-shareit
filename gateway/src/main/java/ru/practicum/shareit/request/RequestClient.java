package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Map;

@Service
@Validated
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(RequestDto itemRequestDto,  int userId) {
        return post("/", userId, null, itemRequestDto);
    }

    public ResponseEntity<Object> getAllUsersItemRequest(int userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getAllOtherUsersItemRequest(int userId,  int from,  int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemRequest(int userId, int requestId) {
        String path = String.format("/%d", requestId);
        return get(path, userId);
    }
}