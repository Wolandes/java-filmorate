package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validate.Validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    Validation validation = new Validation();
    Map<Long, Film> allFilms = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получаем данные об всех фильмах");
        return allFilms.values();
    }

    @PostMapping
    public Film postFilm(@RequestBody Film postFilm) {
        log.info("Пошел процесс добавление фильма " + postFilm);
        postFilm = validation.checkValidationFilm(postFilm);
        long id = getNextId();
        postFilm.setId(id);
        allFilms.put(postFilm.getId(), postFilm);
        log.info("Фильм добавлен в коллекцию: " + postFilm);
        return postFilm;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film putFilm) {
        log.info("Пошел процесс обновление фильма " + putFilm);
        putFilm = validation.checkValidationFilmOnPut(allFilms.keySet(), putFilm);
        allFilms.put(putFilm.getId(), putFilm);
        log.info("Фильм обновлен в коллекции: " + putFilm);
        return putFilm;
    }

    private long getNextId() {
        long currentMaxId = allFilms.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
