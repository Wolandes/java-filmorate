package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    public static final String USERID_VALIDATION_ERROR = "Id должен быть указан";
    public static final String USER_NOT_FOUND_ERROR = "Пользователь с id = %d не найден";
    public static final String EMAIL_VALIDATION_ERROR = "Электронная почта не может быть пустой и должна содержать символ @";
    public static final String LOGIN_VALIDATION_ERROR = "Логин не может быть пустым и содержать пробелы";
    public static final String BIRTHDAY_VALIDATION_ERROR = "Дата рождения не может быть в будущем";

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validation(user);
        setName(user);
        user.setId(getNextId());
        log.info("Сгенерирован id = {} для нового пользователя", user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn(USERID_VALIDATION_ERROR);
            throw new ValidationException(USERID_VALIDATION_ERROR);
        }
        if (!users.containsKey(user.getId())) {
            log.warn(String.format(USER_NOT_FOUND_ERROR, user.getId()));
            throw new NotFoundException(String.format(USER_NOT_FOUND_ERROR, user.getId()));
        }
        validation(user);
        setName(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validation(final User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn(EMAIL_VALIDATION_ERROR);
            throw new ValidationException(EMAIL_VALIDATION_ERROR);
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn(LOGIN_VALIDATION_ERROR);
            throw new ValidationException(LOGIN_VALIDATION_ERROR);
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn(BIRTHDAY_VALIDATION_ERROR);
            throw new ValidationException(BIRTHDAY_VALIDATION_ERROR);
        }
    }

    private void setName(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
