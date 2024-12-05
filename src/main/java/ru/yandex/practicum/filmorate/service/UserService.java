package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserService {
    public static final String USER_NOT_FOUNT_ERROR = "Пользователь с id = %d не найден";

    private long sequenceId;
    private final Map<Long, User> users;

    public UserService() {
        sequenceId = 0L;
        users = new HashMap<>();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        User newUser = user.toBuilder()
                .id(generateId())
                .name(setUserName(user))
                .build();
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public User updateUser(User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            log.warn(String.format(USER_NOT_FOUNT_ERROR, user.getId()));
            throw new NotFoundException(String.format(USER_NOT_FOUNT_ERROR, user.getId()));
        }
        User newUser = user.toBuilder().name(setUserName(user)).build();
        users.put(user.getId(), newUser);
        return newUser;
    }

    private long generateId() {
        log.info("Сгенерирован id = {} для нового пользователя", ++sequenceId);
        return sequenceId;
    }

    private String setUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin();
        }
        return user.getName();
    }
}
