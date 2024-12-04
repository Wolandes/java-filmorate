package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class UserSeviceTest {
    UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserService();
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
        assertEquals(String.format(UserService.USER_NOT_FOUNT_ERROR, 0L), throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> userService.updateUser(newUser.toBuilder().login("login").build()));
        assertEquals("login", userService.getAllUsers().getFirst().getLogin(), "Не совпадают имена");
    }
}
