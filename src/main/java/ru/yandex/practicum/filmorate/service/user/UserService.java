package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

public interface UserService {
    User getUser(Long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void removeUser(Long userId);

    List<User> getFriends(Long userId);

    List<User> getFriendsCommonOther(Long userId, Long otherUserId);

    List<User> addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Film> findRecommendations(Long userId);

    List<Event> getFeed(Long userId);
}
