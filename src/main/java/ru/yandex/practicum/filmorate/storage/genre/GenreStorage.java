package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenre(Long genreId);

    List<Genre> getGenres(List<Long> ids);

    List<Genre> getAllGenres();

    void addGenresToFilm(List<Film> films);

    void addGenresToFilm(Film film);
}
