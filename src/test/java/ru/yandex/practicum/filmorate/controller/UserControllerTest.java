package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void init() {
        userController = new UserController();
    }

    @Test
    @DisplayName("Электронная почта")
    public void shouldEmail() {
        User user = User.builder()
                .login("test")
                .name("name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(UserController.EMAIL_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(ValidationException.class, () -> userController.createUser(user.toBuilder().email("").build()));
        assertEquals(UserController.EMAIL_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(ValidationException.class, () -> userController.createUser(user.toBuilder().email("yandex.ru").build()));
        assertEquals(UserController.EMAIL_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> userController.createUser(user.toBuilder().email("test@yandex.ru").build()));
        assertEquals("test@yandex.ru", userController.getAllUsers().getFirst().getEmail(), "Email не совпадают");
    }

    @Test
    @DisplayName("Логин")
    public void shouldLogin() {
        User user = User.builder()
                .email("test@yandex.ru")
                .name("name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(UserController.LOGIN_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(ValidationException.class, () -> userController.createUser(user.toBuilder().login("").build()));
        assertEquals(UserController.LOGIN_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(ValidationException.class, () -> userController.createUser(user.toBuilder().login("te st").build()));
        assertEquals(UserController.LOGIN_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> userController.createUser(user.toBuilder().login("test").build()));
        assertEquals("test", userController.getAllUsers().getFirst().getLogin(), "Login не совпадают");
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

        userController.createUser(user);
        assertEquals("name", userController.getAllUsers().getFirst().getName(), "Имена не совпадают");

        userController.createUser(user.toBuilder().name(null).build());
        assertEquals("test", userController.getAllUsers().getLast().getName(), "Имена не совпадают");
    }

    @Test
    @DisplayName("Дата рождения")
    public void shouldBirthday() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(UserController.BIRTHDAY_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> userController.createUser(user.toBuilder().birthday(LocalDate.now()).build()));
        assertEquals(LocalDate.now(), userController.getAllUsers().getFirst().getBirthday(), "Даты рождения не совпадают");
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void shouldUpdateUser() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("name")
                .birthday(LocalDate.now())
                .build();
        User newUser = userController.createUser(user.toBuilder().build());

        Throwable throwable = assertThrows(ValidationException.class, () -> userController.updateUser(user));
        assertEquals(UserController.USERID_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(NotFoundException.class, () -> userController.updateUser(user.toBuilder().id(0L).build()));
        assertEquals(String.format(UserController.USER_NOT_FOUND_ERROR, 0L), throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> userController.updateUser(newUser.toBuilder().login("login").build()));
        assertEquals("login", userController.getAllUsers().getFirst().getLogin(), "Login не совпадают");
    }
}
