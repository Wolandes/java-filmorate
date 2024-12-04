package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService = new FilmService();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("Вызван метод GET /films");
        List<Film> films = filmService.getAllFilms();
        log.info("Количество фильмов в ответе = {}", films.size());
        return films;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({ValidatorGroups.Create.class})
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Вызван метод POST /films с телом {}", film);
        Film newFilm = filmService.createFilm(film);
        log.info("Возвращен ответ {}", newFilm);
        return newFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({ValidatorGroups.Update.class})
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Вызван метод PUT /films с телом {}", film);
        Film newFilm = filmService.updateFilm(film);
        log.info("Возвращен ответ {}", newFilm);
        return newFilm;
    }
}
