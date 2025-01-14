package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserDbStorage extends BaseRepository implements UserDbStorage {

    private static final String findAllQuery = "SELECT * FROM Users";
    private static final String addOneQuery = "INSERT INTO Users (login, name, email, birthday) VALUES (?, ?, ?, ?)";
    private static final String findOneQuery = "SELECT * FROM Users WHERE id = ?";
    private static final String updateOneQuery = "UPDATE Users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";
    private static final String getUserFriends = "SELECT u.id, u.name, u.email, u.login, u.birthday FROM Friends_Map as fm JOIN Users as u ON fm.id_Friend = u.id WHERE fm.id_user = ? ORDER BY id";
    private static final String deleteUserFriends = "DELETE FROM Friends_Map WHERE id_User = ?";
    private static final String insertUserFriends = "INSERT INTO Friends_Map (id_User, id_Friend) VALUES (?, ?)";
    private static final String getCollections = "SELECT id, name, email, login, birthday FROM Users";

    public InMemoryUserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Map<Long, User> getCollectionAllUsers() {
        List<User> userList = jdbc.query(getCollections, mapper);
        return userList.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public Collection<User> getAllUsers() {
        return findMany(findAllQuery);
    }

    @Override
    public User addUser(User postUser) {
        long id = insert(addOneQuery, postUser.getLogin(), postUser.getName(), postUser.getEmail(), postUser.getBirthday());
        postUser.setId(id);
        log.info("Юзер добавлен в коллекцию: " + postUser);
        return postUser;
    }

    @Override
    public User updateUser(User putUser) {
        update(updateOneQuery, putUser.getLogin(), putUser.getName(), putUser.getEmail(), putUser.getBirthday(), putUser.getId());
        log.info("Юзер обновлен");
        return putUser;
    }

    @Override
    public User getUser(long id) {
        Optional<User> userOptinal = findOne(findOneQuery, String.valueOf(id));
        User user = userOptinal.get();
        return user;
    }

    @Override
    public Set<User> getUserFriends(long id) {
        List<User> friends = jdbc.query(getUserFriends, mapper, id);
        return new HashSet<>(friends);
    }

    @Override
    public void updateUsersFriends(long id, Set<User> userSetFriends) {
        if (userSetFriends == null || userSetFriends.isEmpty()) {
            jdbc.update(deleteUserFriends, id);
            return;
        }
        jdbc.update(deleteUserFriends, id);
        for (User friend : userSetFriends) {
            jdbc.update(insertUserFriends, id, friend.getId());
        }
    }
}
