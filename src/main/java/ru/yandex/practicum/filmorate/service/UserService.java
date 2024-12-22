package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    final UserStorage userStorage;

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
        Map<Long, User> allUsersMap = userStorage.getCollectionAllUsers();
        User user = allUsersMap.get(id);
        User friend = allUsersMap.get(friendId);
        if (user == null) {
            throw new NotFoundException("Нет такого пользователя с id:" + id);
        }
        if (friend == null) {
            throw new NotFoundException("Нет такого пользователя с id:" + id);
        }
        Map<Long, Set<User>> friendsMap = userStorage.getFriendsMap();
        Set<User> userSet = friendsMap.get(id);
        Set<User> friendSet = friendsMap.get(friendId);
        if (userSet == null) {
            userSet = new HashSet<>();
        }
        if (friendSet == null) {
            friendSet = new HashSet<>();
        }
        userSet.add(friend);
        friendSet.add(user);
        friendsMap.put(id, userSet);
        friendsMap.put(friendId, friendSet);
        userStorage.setFriendsMap(friendsMap);
        log.info("Друг добавлен с id: " + friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.info("Id пользователя и id друга совпадают");
            throw new ValidationException("Id пользователя и id друга совпадают");
        }
        Map<Long, User> allUsersMap = userStorage.getCollectionAllUsers();
        User user = allUsersMap.get(id);
        User friend = allUsersMap.get(friendId);
        if (user == null) {
            throw new NotFoundException("Нет такого пользователя с id:" + id);
        }
        if (friend == null) {
            throw new NotFoundException("Нет такого пользователя с id:" + id);
        }
        Map<Long, Set<User>> friendsMap = userStorage.getFriendsMap();
        Set<User> userSet = friendsMap.get(id);
        Set<User> friendSet = friendsMap.get(friendId);
        if (userSet == null) {
            friendsMap.put(user.getId(), null);
            log.info("нет списка друзей");
        }
        if (friendSet == null) {
            friendsMap.put(friendId, null);
            log.info("нет списка друзей");
            userStorage.setFriendsMap(friendsMap);
            return;
        }
        for (User user1 : userSet) {
            if (user1.getId() == friendId) {
                userSet.remove(friend);
                friendSet.remove(user);
                friendsMap.put(id, userSet);
                friendsMap.put(friendId, friendSet);
                userStorage.setFriendsMap(friendsMap);
                log.info("Пользователь с id: " + friendId + "удален из списка");
            }
        }
        log.info("Пользователь с id: " + friendId + "Нет в списке id");
    }

    public Collection<User> allIdFriends(Long id) {
        Map<Long, Set<User>> friendsMap = userStorage.getFriendsMap();
        Map<Long, User> userMap = userStorage.getCollectionAllUsers();
        User user = userMap.get(id);
        if (user == null) {
            throw new NotFoundException("Нет такого пользователя с id: " + id);
        }
        Set<User> friendsSet = friendsMap.get(id);
        if (friendsSet == null) {
            friendsSet = new HashSet<>();
        }
        return friendsSet;
    }

    public Collection<User> generalFriends(Long id, Long otherId) {
        Collection<User> generalFriends = new ArrayList<>();
        Map<Long, Set<User>> allUsersMap = userStorage.getFriendsMap();
        Set<User> users = allUsersMap.get(id);
        Set<User> otherUsers = allUsersMap.get(otherId);
        if (users == null) {
            log.info("Список у id:" + id + " пуст");
            throw new NotFoundException("Список у id:" + id + " пуст");
        }
        if (otherUsers == null) {
            log.info("Список у id:" + otherId + " пуст");
            throw new NotFoundException("Список у id:" + otherId + " пуст");
        }
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

//Map<Long, User> allUsersMap = userStorage.getCollectionAllUsers();
//        User user = allUsersMap.get(id);
//        User newFriend = allUsersMap.get(friendId);
//        Set<User> usersFriends = user.getFriends();
//        Set<User> newFriendsAllFriends = newFriend.getFriends();
//        for (User usersFriend : usersFriends) {
//            if (usersFriend.getId() == friendId) {
//                log.info("Друг с id: " + friendId + "уже есть в списке друзей");
//                return;
//            }
//        }
//        usersFriends.add(newFriend);
//        newFriendsAllFriends.add(user);
//        user.setFriends(usersFriends);
//        newFriend.setFriends(newFriendsAllFriends);
//        allUsersMap.put(user.getId(), user);
//        allUsersMap.put(newFriend.getId(), newFriend);
//        userStorage.setCollectionAllUsers(allUsersMap);

//        Map<Long, User> allUsersMap = userStorage.getCollectionAllUsers();
//        User user = allUsersMap.get(id);
//        User deleteFriend = allUsersMap.get(friendId);
//        Set<User> userFriends = user.getFriends();
//        for (User userFriend : userFriends) {
//            if (userFriend.getId() == deleteFriend.getId()) {
//                userFriends.remove(deleteFriend);
//                user.setFriends(userFriends);
//                allUsersMap.put(user.getId(), user);
//                userStorage.setCollectionAllUsers(allUsersMap);
//                log.info("Пользователь с id: " + friendId + " удален из списка друзей");
//                return;
//            }
//        }

//        Map<Long, User> allUsers = userStorage.getCollectionAllUsers();
//        User user = allUsers.get(id);
//        User otherUser = allUsers.get(otherId);
//        if (id.equals(otherId)) {
//            log.info("Совпадает id: " + id);
//            return user.getFriends();
//        }
//        Set<User> userFriends = user.getFriends();
//        Set<User> otherFriends = otherUser.getFriends();
//        for (User otherFriend : userFriends) {
//            for (User friend : otherFriends) {
//                if (otherFriend.getId() == friend.getId()) {
//                    generalFriends.add(friend);
//                    break;
//                }
//            }
//        }