package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> allUsers = new HashMap<>();

    public Map<Long, User> getCollectionAllUsers(){
        return allUsers;
    }

    @Override
    public Collection<User> getAllUsers(){
        return allUsers.values();
    }

    @Override
    public User addUser(User postUser){
        long id = getNextId();
        postUser.setId(id);
        allUsers.put(postUser.getId(),postUser);
        log.info("Юзер добавлен в коллекцию: " + postUser);
        return postUser;
    }
    @Override
    public User updateUser(User putUser){
        allUsers.put(putUser.getId(),putUser);
        return putUser;
    }

    private long getNextId() {
        long currentMaxId = allUsers.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
