package ru.yandex.practicum.filmorate.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {
    UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserServiceImpl(new InMemoryUserStorage());
    }

    @Test
    @DisplayName("Имя")
    public void shouldName() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        userService.createUser(user);
        assertEquals("name", userService.getAllUsers().getFirst().getName(), "Имена не совпадают");

        userService.createUser(user.toBuilder().name(null).build());
        assertEquals("test", userService.getAllUsers().getLast().getName(), "Имена не совпадают");
    }

    @Test
    @DisplayName("Обновление фильма")
    public void shouldUpdateFilm() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("name")
                .birthday(LocalDate.now())
                .build();
        User newUser = userService.createUser(user.toBuilder().build());

        Throwable throwable = assertThrows(NotFoundException.class, () -> userService.updateUser(user.toBuilder().id(0L).build()));
        assertEquals(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, 0L), throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> userService.updateUser(newUser.toBuilder().login("login").build()));
        assertEquals("login", userService.getAllUsers().getFirst().getLogin(), "Не совпадают имена");
    }
}
