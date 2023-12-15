package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testUserDto() throws Exception {
        ItemDto itemDto = new ItemDto()
                .setId(1)
                .setName("Клавиатура")
                .setDescription("Клавиатура для китайского языка")
                .setAvailable(true);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Клавиатура");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Клавиатура для китайского языка");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}