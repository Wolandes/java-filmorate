package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService = new UserService();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("Вызван метод GET /films");
        List<User> users = userService.getAllUsers();
        log.info("Количество пользователей в ответе = {}", users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({ValidatorGroups.Create.class})
    public User createUser(@RequestBody @Valid User user) {
        log.info("Вызван метод POST /films с телом {}", user);
        User newUser = userService.createUser(user);
        log.info("Возвращен ответ {}", newUser);
        return newUser;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({ValidatorGroups.Update.class})
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Вызван метод PUT /films с телом {}", user);
        User newUser = userService.updateUser(user);
        log.info("Возвращен ответ {}", newUser);
        return newUser;
    }
}
