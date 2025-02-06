package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    @Override
    public User getUser(Long userId) {
        return Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
    }

    @Override
    public List<User> getAllUsers() {
        return Optional.ofNullable(userStorage.getAllUsers())
                .orElse(new ArrayList<>());
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        getUser(user.getId());
        return userStorage.updateUser(user);
    }

    @Override
    public void removeUser(Long userId) {
        User user = getUser(userId);
        userStorage.removeUser(user);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = getUser(userId);
        return Optional.ofNullable(userStorage.getFriends(user))
                .orElse(new ArrayList<>());
    }

    public List<User> getFriendsCommonOther(Long userId, Long otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);
        return Optional.ofNullable(userStorage.getFriendsCommonOther(user, otherUser))
                .orElse(new ArrayList<>());
    }

    @Override
    public List<User> addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user.equals(friend))
            throw new ValidationException("Невозможно добавить в друзья самого себя");
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(EventOperation.ADD)
                .entityId(friendId)
                .build();
        eventStorage.createEvent(event);
        return userStorage.addFriend(user, friend);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(EventOperation.REMOVE)
                .entityId(friendId)
                .build();
        eventStorage.createEvent(event);
        userStorage.removeFriend(user, friend);
    }

    public List<Event> getFeed(Long userId) {
        User user = getUser(userId);
        return eventStorage.getFeed(user.getId());
    }

    @Override
    public List<Film> findRecommendations(Long userId) {
        getUser(userId);
        List<Long> likedFilms = filmStorage.getLikedFilm(userId);
        if (likedFilms.isEmpty()) {
            log.info("Список понравишься фильмов пуст");
            return List.of();
        }

        Long similarUser = filmStorage.getSimilarUser(userId, likedFilms)
                .stream()
                .findFirst()
                .orElse(null);
        if (similarUser == null) {
            log.info("Похожих пользователей нет, рекомендации нет");
            return List.of();
        }

        List<Film> films = filmStorage.findRecommendations(userId, likedFilms, similarUser);
        if (films.isEmpty()) {
            log.info("У похожего пользователя нет новых фильмов для рекомендации");
        }
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        return films;
    }
}
