package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    @RequestMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getGenre(@PathVariable("id") Long genreId) {
        log.info("Вызван метод GET /genres/{}", genreId);
        Genre genre = genreService.getGenre(genreId);
        log.info("Метод GET /genres/{} вернул ответ {}", genreId, genre);
        return genre;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> getAllGenres() {
        log.info("Вызван метод GET /genres");
        List<Genre> genres = genreService.getAllGenres();
        log.info("Метод GET /genres вернул ответ {}", genres);
        return genres;
    }
}
