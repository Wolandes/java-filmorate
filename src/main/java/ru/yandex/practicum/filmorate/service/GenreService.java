package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final JdbcGenreRepository genreDbStorage;

    public Collection<Genre> getAllGenres() {
        log.info("Получение информации об всех жанрах");
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenre(long id) {
        log.info("Получение информации об жанре с id: " + id);
        return genreDbStorage.getGenre(id);
    }
}
