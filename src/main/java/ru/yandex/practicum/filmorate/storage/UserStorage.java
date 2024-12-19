package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {
    Collection<User> allUsers();
    User addUser(User postUser);
    User updateUser(User putUser);

    User addFried(User addFriend);
    User deleteFriend(Integer deleteFriendId);
    Collection<User> allIdFriends(Integer id);
    Collection<User> generalFriends(Integer id);
}
