package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserDbStorage extends BaseRepository implements UserDbStorage {

    private static final String findAllQuery = "SELECT * FROM Users";
    private static final String addOneQuery = "INSERT INTO Users (login, name, email, birthday) VALUES (?, ?, ?, ?)";
    private static final String findOneQuery = "SELECT * FROM Users WHERE id = ?";
    private static final String updateOneQuery = "UPDATE Users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";

    public InMemoryUserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper){
        super(jdbc,mapper);
    }

    private Map<Long, User> allUsers = new HashMap<>();

    private Map<Long, Set<User>> friendsMap = new HashMap<>();

    @Override
    public void setFriendsMap(Map<Long, Set<User>> friendsMap) {
        this.friendsMap = friendsMap;
    }

    @Override
    public Map<Long, User> getCollectionAllUsers() {
        return allUsers;
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
        return findMany(findAllQuery);
    }

    @Override
    public User addUser(User postUser) {
        long id = insert(addOneQuery, postUser.getLogin(),postUser.getName(),postUser.getEmail(),postUser.getBirthday());
        postUser.setId(id);
        log.info("Юзер добавлен в коллекцию: " + postUser);
        return postUser;
    }

    @Override
    public User updateUser(User putUser) {
        update(updateOneQuery, putUser.getLogin(),putUser.getName(),putUser.getEmail(),putUser.getBirthday(),putUser.getId());
        log.info("Юзер обновлен");
        return putUser;
    }

    @Override
    public User getUser(long id) {
        Optional<User> userOptinal = findOne(findOneQuery,String.valueOf(id));
        User user = userOptinal.get();
        if (user == null) {
            log.info("Нет пользователя с таким id: " + user);
            throw new NotFoundException("Нет пользователя с таким id: " + user);
        }
        return user;
    }

    @Override
    public Set<User> getUserFriends(long id) {
        return friendsMap.get(id);
    }

    @Override
    public void updateUsersFriends(long id, Set<User> userSetFriends) {
        friendsMap.put(id, userSetFriends);
    }
}
