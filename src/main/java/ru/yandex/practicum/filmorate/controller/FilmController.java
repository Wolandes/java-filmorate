package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public static final String FILMID_VALIDATION_ERROR = "Id должен быть указан";
    public static final String FILM_NOT_FOUND_ERROR = "Фильм с id = %d не найден";
    public static final String NAME_VALIDATION_ERROR = "Название не может быть пустым";
    public static final String DESCRIPTION_VALIDATION_ERROR = String.format(
            "Максимальная длина описания — %d символов", MAX_DESCRIPTION_LENGTH);
    public static final String RELEASE_DATE_VALIDATION_ERROR = String.format(
            "Дата релиза — не раньше %s года", MIN_RELEASE_DATE.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
    public static final String DURATION_VALIDATION_ERROR = "Продолжительность фильма должна быть положительным числом";

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validation(film);
        film.setId(getNextId());
        log.info("Сгенерирован id = {} для нового фильма", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.warn(FILMID_VALIDATION_ERROR);
            throw new ValidationException(FILMID_VALIDATION_ERROR);
        }
        if (!films.containsKey(film.getId())) {
            log.warn(String.format(FILM_NOT_FOUND_ERROR, film.getId()));
            throw new NotFoundException(String.format(FILM_NOT_FOUND_ERROR, film.getId()));
        }
        validation(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validation(final Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn(NAME_VALIDATION_ERROR);
            throw new ValidationException(NAME_VALIDATION_ERROR);
        } else if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn(DESCRIPTION_VALIDATION_ERROR);
            throw new ValidationException(DESCRIPTION_VALIDATION_ERROR);
        } else if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn(RELEASE_DATE_VALIDATION_ERROR);
            throw new ValidationException(RELEASE_DATE_VALIDATION_ERROR);
        } else if (film.getDuration() <= 0) {
            log.warn(DURATION_VALIDATION_ERROR);
            throw new ValidationException(DURATION_VALIDATION_ERROR);
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
