package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private ValidationService validationService = new ValidationService();
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получаем данные об всех юзерах");
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User postUser) {
        log.info("Пошел процесс добавления юзера " + postUser);
        postUser = validationService.checkValidationUser(postUser);
        return userService.addUser(postUser);
    }

    @PutMapping
    public User updateUser(@RequestBody User putUser) {
        log.info("Пошел процесс обновление юзера " + putUser);
        putUser = validationService.checkValidationUserOnPut(userService.getAllUserMap().keySet(), putUser);
        userService.updateUser(putUser);
        log.info("Юзер обновлен в коллекции: " + putUser);
        return putUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пошел процесс добавления друга с id:" + friendId + ". Пользователем id:" + id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пошел процесс удаления друга с id:" + friendId + ". Пользователем id:" + id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> allIdFriends(@PathVariable long id) {
        log.info("Идет процесс получения друзей у пользователя с id: " + id);
        return userService.allIdFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> generalFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Идет процесс получения общих друзей у пользователя с id: " + id + ". C пользователя с id: " + otherId);
        return userService.generalFriends(id, otherId);
    }
}
