package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.service.event.EventServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final EventService eventService;

    @Override
    public User getUser(Long userId) {
        return Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
    }

    @Override
    public List<User> getAllUsers() {
        return Optional.ofNullable(userStorage.getAllUsers())
                .orElse(new ArrayList<>());
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        if (userStorage.getUser(user.getId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, user.getId()));
        }
        return userStorage.updateUser(user);
    }

    @Override
    public void removeUser(Long userId) {
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        userStorage.removeUser(user);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        return Optional.ofNullable(userStorage.getFriends(user))
                .orElse(new ArrayList<>());
    }

    public List<User> getFriendsCommonOther(Long userId, Long otherUserId) {
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        User otherUser = Optional.ofNullable(userStorage.getUser(otherUserId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, otherUserId)));
        return Optional.ofNullable(userStorage.getFriendsCommonOther(user, otherUser))
                .orElse(new ArrayList<>());
    }

    @Override
    public List<User> addFriend(Long userId, Long friendId) {
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        User friend = Optional.ofNullable(userStorage.getUser(friendId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, friendId)));
        if (user.equals(friend))
            throw new ValidationException("Невозможно добавить в друзья самого себя");
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
        return userStorage.addFriend(user, friend);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        User friend = Optional.ofNullable(userStorage.getUser(friendId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, friendId)));
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
        userStorage.removeFriend(user, friend);
    }

    public List<Event> getFeed(Long userId) {
        return eventService.getFeed(userId);
    }
}
