package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String GET_USER = "select id, email, login, name, birthday from public.users where id = :id";
    private static final String GET_ALL_USERS = "select id, email, login, name, birthday from public.users";
    private static final String INSERT_USER = """
            insert into public.users (email, login, name, birthday)
            values (:email, :login, :name, :birthday)
            """;
    private static final String UPDATE_USER = """
            update public.users
            set email = :email,
            login = :login,
            name = :name,
            birthday = :birthday
            where id = :id
            """;
    private static final String GET_FRIENDS = """
            select u.id, u.email, u.login, u.name, u.birthday
            from friends as f
            inner join public.users as u on u.id = f.friend_id
            where f.user_id = :user_id
            """;
    private static final String GET_FRIENDS_COMMON_OTHER = """
            select u.id, u.email, u.login, u.name, u.birthday
            from friends as f
            inner join public.users as u on u.id = f.friend_id
            where f.user_id = :user_id
            and f.friend_id in (select friend_id from public.friends where user_id = :friend_id)
            """;
    private static final String INSERT_FRIEND = """
            merge into public.friends(user_id, friend_id)
            values (:user_id, :friend_id)
            """;
    private static final String DELETE_FRIEND = """
            delete from public.friends
            where user_id = :user_id
            and friend_id = :friend_id
            """;

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<User> mapper;

    @Override
    public User getUser(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", userId);
        try {
            return jdbc.queryForObject(GET_USER, params, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return jdbc.query(GET_ALL_USERS, new MapSqlParameterSource(), mapper);
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public User createUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        try {
            jdbc.update(INSERT_USER, params, keyHolder, new String[]{"id"});
            Integer id = keyHolder.getKeyAs(Integer.class);
            return getUser(Long.valueOf(id));
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_USERS_ERROR, user));
        }
    }

    @Override
    public User updateUser(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", user.getId());
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        try {
            jdbc.update(UPDATE_USER, params);
            return getUser(user.getId());
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.UPDATE_USERS_ERROR, user.getId()));
        }
    }

    @Override
    public List<User> getFriends(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        try {
            return jdbc.query(GET_FRIENDS, params, mapper);
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<User> getFriendsCommonOther(User user, User otherUser) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("friend_id", otherUser.getId());
        try {
            return jdbc.query(GET_FRIENDS_COMMON_OTHER, params, mapper);
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<User> addFriend(User user, User friend) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("friend_id", friend.getId());
        try {
            jdbc.update(INSERT_FRIEND, params);
            return getFriends(user);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_FRIEND_ERROR, user.getId(), friend.getId()));
        }
    }

    @Override
    public void removeFriend(User user, User friend) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("friend_id", friend.getId());
        try {
            jdbc.update(DELETE_FRIEND, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_FRIEND_ERROR, user.getId(), friend.getId()));
        }
    }
}
