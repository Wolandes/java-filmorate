package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> allUsers = new HashMap<>();

    @Override
    public Map<Long, User> getCollectionAllUsers() {
        return allUsers;
    }

    public Map<Long, Set<User>> friendsMap = new HashMap<>();

    @Override
    public void setFriendsMap(Map<Long, Set<User>> friendsMap) {
        this.friendsMap = friendsMap;
    }

    @Override
    public Map<Long, Set<User>> getFriendsMap() {
        return friendsMap;
    }

    @Override
    public void setCollectionAllUsers(Map<Long, User> allUsers1) {
        allUsers = allUsers1;
    }

    @Override
    public Collection<User> getAllUsers() {
        return allUsers.values();
    }

    @Override
    public User addUser(User postUser) {
        long id = getNextId();
        postUser.setId(id);
        allUsers.put(postUser.getId(), postUser);
        log.info("Юзер добавлен в коллекцию: " + postUser);
        return postUser;
    }

    @Override
    public User updateUser(User putUser) {
        allUsers.put(putUser.getId(), putUser);
        return putUser;
    }

    private long getNextId() {
        long currentMaxId = allUsers.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
