package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.ValidationService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    ValidationService validationService = new ValidationService();
    InMemoryFilmStorage inMemoryFilmStorage;

    FilmController(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получаем данные об всех фильмах");
        return inMemoryFilmStorage.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film postFilm) {
        log.info("Пошел процесс добавление фильма " + postFilm);
        postFilm = validationService.checkValidationFilm(postFilm);
        return inMemoryFilmStorage.addFilm(postFilm);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film putFilm) {
        log.info("Пошел процесс обновление фильма " + putFilm);
        putFilm = validationService.checkValidationFilmOnPut(inMemoryFilmStorage.getCollectionAllFilms().keySet(), putFilm);
        return inMemoryFilmStorage.updateFilm(putFilm);
    }
}
