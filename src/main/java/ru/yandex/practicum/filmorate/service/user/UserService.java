package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    List<User> getFriends(Long userId);

    List<User> getFriendsCommonOther(Long userId, Long otherUserId);

    List<User> addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);
}
