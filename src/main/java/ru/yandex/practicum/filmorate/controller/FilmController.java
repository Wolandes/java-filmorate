package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private ValidationService validationService = new ValidationService();
    private FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получаем данные об всех фильмах");
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@RequestBody Film postFilm) {
        log.info("Пошел процесс добавление фильма " + postFilm);
        postFilm = validationService.checkValidationFilm(postFilm);
        return filmService.addFilm(postFilm);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film putFilm) {
        log.info("Пошел процесс обновление фильма " + putFilm);
        putFilm = validationService.checkValidationFilmOnPut(filmService.getAllMapFilms()
                .keySet(), putFilm);
        return filmService.updateFilm(putFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long userId, @PathVariable long id) {
        log.info("Пошел процесс добавления лайка пользователем с id: " + userId + ". К фильму с id: " + id);
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long userId, @PathVariable long id) {
        filmService.deleteLike(userId, id);
        log.info("Пошел процесс удаления лайка пользователем с id: " + userId + ". К фильму с id: " + id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Пошел процесс получения популярных фильмов");
        return filmService.getPopularFilms(count);
    }
}
