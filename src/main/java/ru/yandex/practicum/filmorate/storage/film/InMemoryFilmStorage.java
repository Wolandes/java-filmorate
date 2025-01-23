package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long sequenceId;
    private final Map<Long, Film> films;
    private final Map<Long, Set<User>> likes;

    public InMemoryFilmStorage() {
        sequenceId = 0L;
        films = new HashMap<>();
        likes = new HashMap<>();
    }

    @Override
    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        Film newFilm = film.toBuilder()
                .id(generateId())
                .build();
        films.put(newFilm.getId(), newFilm);
        likes.put(newFilm.getId(), new HashSet<>());
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        return films.values().stream()
                .sorted((film1, film2) -> {
                    int likes1 = likes.get(film1.getId()).size();
                    int likes2 = likes.get(film2.getId()).size();
                    if (likes1 != likes2) {
                        return likes2 - likes1;
                    } else {
                        return film1.getId().compareTo(film2.getId());
                    }
                })
                .limit(count)
                .toList();
    }

    @Override
    public void addLike(Film film, User user) {
        Set<User> likeUsers = likes.get(film.getId());
        likeUsers.add(user);
    }

    @Override
    public void removeLike(Film film, User user) {
        Set<User> likeUsers = likes.get(film.getId());
        likeUsers.remove(user);
    }

    private long generateId() {
        log.info("Сгенерирован id = {} для нового фильма", ++sequenceId);
        return sequenceId;
    }
}
