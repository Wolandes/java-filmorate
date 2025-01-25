package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User postUser) {
        return userStorage.addUser(postUser);
    }

    public User updateUser(User putUser) {
        return userStorage.updateUser(putUser);
    }

    public void addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.info("Id пользователя и id друга совпадают");
            throw new ValidationException("Id пользователя и id друга совпадают");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        Set<User> userSet = userStorage.getUserFriends(id);
        if (userSet == null) {
            userSet = new HashSet<>();
        }
        userSet.add(friend);
        userStorage.updateUsersFriends(id, userSet);
        log.info("Друг добавлен с id: " + friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.info("Id пользователя и id друга совпадают");
            throw new ValidationException("Id пользователя и id друга совпадают");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        Set<User> userSet = userStorage.getUserFriends(id);
        if (userSet == null) {
            userStorage.updateUsersFriends(user.getId(), null);
            log.info("нет списка друзей");
        }
        for (User user1 : userSet) {
            if (user1.getId() == friendId) {
                userSet.remove(friend);
                userStorage.updateUsersFriends(id, userSet);
                log.info("Пользователь с id: " + friendId + "удален из списка");
                return;
            }
        }
        log.info("Пользователь с id: " + friendId + "Нет в списке id");
    }

    public Collection<User> allIdFriends(Long id) {
        User user = userStorage.getUser(id);
        Set<User> friendsSet = userStorage.getUserFriends(id);
        if (friendsSet == null) {
            friendsSet = new HashSet<>();
        }
        return friendsSet;
    }

    public Collection<User> generalFriends(Long id, Long otherId) {
        Collection<User> generalFriends = new ArrayList<>();
        Set<User> users = userStorage.getUserFriends(id);
        Set<User> otherUsers = userStorage.getUserFriends(otherId);
        for (User user : users) {
            for (User otherUser : otherUsers) {
                if (user.getId() == otherUser.getId()) {
                    generalFriends.add(otherUser);
                    break;
                }
            }
        }
        log.info("Возврат списка общих друзей");
        return generalFriends;
    }

    public Map<Long, User> getAllUserMap() {
        return userStorage.getCollectionAllUsers();
    }
}