package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long sequenceId;
    private final Map<Long, User> users;
    private final Map<Long, Set<User>> friends;

    public InMemoryUserStorage() {
        sequenceId = 0L;
        users = new HashMap<>();
        friends = new HashMap<>();
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        User newUser = user.toBuilder()
                .id(generateId())
                .name(setUserName(user))
                .build();
        users.put(newUser.getId(), newUser);
        friends.put(newUser.getId(), new HashSet<>());
        return newUser;
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        User newUser = user.toBuilder().name(setUserName(user)).build();
        users.put(user.getId(), newUser);
        return newUser;
    }

    @Override
    public List<User> getFriends(User user) {
        return new ArrayList<>(friends.get(user.getId()));
    }

    @Override
    public List<User> getFriendsCommonOther(User user, User otherUser) {
        final Set<User> userFriends = friends.get(user.getId());
        final Set<User> otherUserFriends = friends.get(otherUser.getId());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .toList();
    }

    @Override
    public List<User> addFriend(User user, User friend) {
        Set<User> userFriends = friends.get(friend.getId());
        userFriends.add(user);

        userFriends = friends.get(user.getId());
        userFriends.add(friend);

        return new ArrayList<>(userFriends);
    }

    @Override
    public void removeFriend(User user, User friend) {
        Set<User> userFriends = friends.get(friend.getId());
        userFriends.remove(user);

        userFriends = friends.get(user.getId());
        userFriends.remove(friend);
    }

    private long generateId() {
        log.info("Сгенерирован id = {} для нового пользователя", ++sequenceId);
        return sequenceId;
    }

    private String setUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin();
        }
        return user.getName();
    }
}
