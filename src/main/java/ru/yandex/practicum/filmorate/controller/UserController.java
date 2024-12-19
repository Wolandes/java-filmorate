package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.ValidationService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    ValidationService validationService = new ValidationService();
    InMemoryUserStorage inMemoryUserStorage;

    UserController(InMemoryUserStorage inMemoryUserStorage){
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получаем данные об всех юзерах");
        return inMemoryUserStorage.getAllUsers();
    }

    @PostMapping
    public User addUser(@RequestBody User postUser) {
        log.info("Пошел процесс добавления юзера " + postUser);
        postUser = validationService.checkValidationUser(postUser);
        return inMemoryUserStorage.addUser(postUser);
    }

    @PutMapping
    public User updateUser(@RequestBody User putUser) {
        log.info("Пошел процесс обновление юзера " + putUser);
        putUser = validationService.checkValidationUserOnPut(inMemoryUserStorage.getCollectionAllUsers().keySet(), putUser);
        inMemoryUserStorage.updateUser(putUser);
        log.info("Юзер обновлен в коллекции: " + putUser);
        return putUser;
    }
}
