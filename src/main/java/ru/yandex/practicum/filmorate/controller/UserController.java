package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable("id") Long userId) {
        log.info("Вызван метод GET /users/{}", userId);
        User user = userService.getUser(userId);
        log.info("Метод GET /users/{} вернул ответ {}", userId, user);
        return user;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("Вызван метод GET /users");
        List<User> users = userService.getAllUsers();
        log.info("Метод GET /users вернул ответ {}", users);
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({ValidatorGroups.Create.class})
    public User createUser(@RequestBody @Valid User user) {
        log.info("Вызван метод POST /users с телом {}", user);
        User newUser = userService.createUser(user);
        log.info("Метод POST /users вернул ответ {}", newUser);
        return newUser;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({ValidatorGroups.Update.class})
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Вызван метод PUT /users с телом {}", user);
        User newUser = userService.updateUser(user);
        log.info("Метод PUT /users вернул ответ {}", newUser);
        return newUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFilm(@PathVariable("userId") Long userId) {
        log.info("Вызван метод DELETE /users/{}", userId);
        userService.removeUser(userId);
        log.info("Метод DELETE /users/{} успешно выполнен", userId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable("id") Long userId) {
        log.info("Вызван метод GET /users/{id}/friends с id = {}", userId);
        List<User> userFriends = userService.getFriends(userId);
        log.info("Метод GET /users/{id}/friends вернул ответ {}", userFriends);
        return userFriends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendsCommonOther(@PathVariable("id") Long userId,
                                            @PathVariable("otherId") Long otherId) {
        log.info("Вызван метод GET /users/{id}/friends/common/{otherId} с id = {} и otherId = {}", userId, otherId);
        List<User> userFriends = userService.getFriendsCommonOther(userId, otherId);
        log.info("Метод GET /users/{id}/friends/common/{otherId} вернул ответ {}", userFriends);
        return userFriends;
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> addFriend(@PathVariable("id") Long userId,
                                @PathVariable("friendId") Long friendId) {
        log.info("Вызван метод PUT /{id}/friends/{friendId} с id = {} и friendId = {}", userId, friendId);
        List<User> userFriends = userService.addFriend(userId, friendId);
        log.info("Метод PUT /{id}/friends/{friendId} вернул ответ {}", userFriends);
        return userFriends;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("id") Long userId,
                             @PathVariable("friendId") Long friendId) {
        log.info("Вызван метод DELETE /{id}/friends/{friendId} с id = {} и friendId = {}", userId, friendId);
        userService.removeFriend(userId, friendId);
        log.info("Метод DELETE /{id}/friends/{friendId} успешно выполнен");
    }

    @GetMapping("{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getRecommendations(@PathVariable Long id){
        return userService.findRecommendations(id);
    }
}
