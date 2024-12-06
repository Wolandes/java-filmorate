package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validate.Validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    Validation validation = new Validation();
    Map<Long, User> allUsers = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получаем данные об всех юзерах");
        return allUsers.values();
    }

    @PostMapping
    public User addUser(@RequestBody User postUser) {
        log.info("Пошел процесс добавления юзера " + postUser);
        postUser = validation.checkValidationUser(postUser);
        long id = getNextId();
        postUser.setId(id);
        allUsers.put(postUser.getId(), postUser);
        log.info("Юзер добавлен в коллекцию: " + postUser);
        return postUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User putUser) {
        log.info("Пошел процесс обновление юзера " + putUser);
        putUser = validation.checkValidationUserOnPut(allUsers.keySet(), putUser);
        allUsers.put(putUser.getId(), putUser);
        log.info("Юзер обновлен в коллекции: " + putUser);
        return putUser;
    }

    private long getNextId() {
        long currentMaxId = allUsers.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
