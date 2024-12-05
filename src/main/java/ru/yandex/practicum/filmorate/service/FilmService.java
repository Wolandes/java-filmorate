package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmService {
    public static final String FILM_NOT_FOUNT_ERROR = "Фильм с id = %d не найден";

    private long sequenceId;
    private final Map<Long, Film> films;

    public FilmService() {
        sequenceId = 0L;
        films = new HashMap<>();
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        Film newFilm = film.toBuilder()
                .id(generateId())
                .build();
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) throws NotFoundException {
        if (!films.containsKey(film.getId())) {
            log.warn(String.format(FILM_NOT_FOUNT_ERROR, film.getId()));
            throw new NotFoundException(String.format(FILM_NOT_FOUNT_ERROR, film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    private long generateId() {
        log.info("Сгенерирован id = {} для нового фильма", ++sequenceId);
        return sequenceId;
    }
}
