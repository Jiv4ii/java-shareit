package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
class ShareItTests {
	@Autowired
	UserService userService;

	@Test
	void contextLoads() {
	}

}
