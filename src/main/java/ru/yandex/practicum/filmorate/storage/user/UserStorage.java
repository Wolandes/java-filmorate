package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User getUser(Long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void removeUser(User user);

    List<User> getFriends(User user);

    List<User> getFriendsCommonOther(User user, User otherUser);

    List<User> addFriend(User user, User friend);

    void removeFriend(User user, User friend);
}
