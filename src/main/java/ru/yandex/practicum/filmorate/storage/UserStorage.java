package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {
    Collection<User> getAllUsers();
    User addUser(User postUser);
    User updateUser(User putUser);

    User addFried(Integer addFriendId);
    User deleteFriend(Integer deleteFriendId);
    Collection<User> allIdFriends();
    Collection<User> generalFriends(Integer idOther);
}