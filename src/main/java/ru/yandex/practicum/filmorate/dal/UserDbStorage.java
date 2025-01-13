package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface UserDbStorage {
    Collection<User> getAllUsers();

    User addUser(User postUser);

    User updateUser(User putUser);

    Map<Long, User> getCollectionAllUsers();

    User getUser(long id);

    Set<User> getUserFriends(long id);

    void updateUsersFriends(long id, Set<User> userSetFriends);
}
