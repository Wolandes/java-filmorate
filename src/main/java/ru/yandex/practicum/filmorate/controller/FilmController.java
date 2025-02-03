package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmsWithGenre(@PathVariable("id") Long filmId) {
        log.info("Вызван метод GET /films/{}", filmId);
        Film film = filmService.getFilm(filmId);
        log.info("Метод GET /films/{} вернул ответ {}", filmId, film);
        return film;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("Вызван метод GET /films");
        List<Film> films = filmService.getAllFilms();
        log.info("Метод GET /films вернул ответ {}", films);
        return films;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({ValidatorGroups.Create.class})
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Вызван метод POST /films с телом {}", film);
        Film newFilm = filmService.createFilm(film);
        log.info("Метод POST /films вернул ответ {}", newFilm);
        return newFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({ValidatorGroups.Update.class})
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Вызван метод PUT /films с телом {}", film);
        Film newFilm = filmService.updateFilm(film);
        log.info("Метод PUT /films вернул ответ {}", newFilm);
        return newFilm;
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFilm(@PathVariable("filmId") Long filmId) {
        log.info("Вызван метод DELETE /films/{}", filmId);
        filmService.removeFilm(filmId);
        log.info("Метод DELETE /films/{} успешно выполнен", filmId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") Long count,
                                      @RequestParam(value = "genreId", required = false) Long genreId,
                                      @RequestParam(value = "year", required = false) Integer year) {
        log.info("Получение популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable("id") Long filmId,
                        @PathVariable("userId") Long userId) {
        log.info("Вызван метод PUT /films/{id}/like/{userId} с id = {} и userId = {}", filmId, userId);
        filmService.addLike(filmId, userId);
        log.info("Метод PUT /films/{id}/like/{userId} успешно выполнен");
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable("id") Long filmId,
                           @PathVariable("userId") Long userId) {
        log.info("Вызван метод DELETE /films/{id}/like/{userId} с id = {} и userId = {}", filmId, userId);
        filmService.removeLike(filmId, userId);
        log.info("Метод DELETE /films/{id}/like/{userId} успешно выполнен");
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmsByDirectorId(@PathVariable("directorId") Long directorId,
                                           @RequestParam("sortBy") String sortBy) {
        log.info("Вызван метод GET /director/{} с sortBy = {}", directorId, sortBy);
        List<Film> films = filmService.getFilmsByDirectorId(directorId, sortBy);
        log.info("Метод GET /director/{} вернул ответ {}", directorId, films);
        return films;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getSearchFilms(@RequestParam("query") String query,
                                     @RequestParam("by") String by) {
        log.info("Вызван метод GET /search с query = {} и by = {}", query, by);
        List<Film> films = filmService.searchFilms(query, by);
        log.info("Метод GET /search вернул ответ {}", films);
        return films;
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getCommonFilms(@RequestParam("userId") Long userId,
                                     @RequestParam("friendId") Long friendId) {
        log.info("Вызван метод GET /common с userId = {} и friendId = {}", userId, friendId);
        List<Film> films = filmService.getCommonFilms(userId, friendId);
        log.info("Метод GET /common вернул ответ {}", films);
        return films;
    }
}
