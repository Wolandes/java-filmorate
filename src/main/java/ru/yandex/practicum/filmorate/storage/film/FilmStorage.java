package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    Film getFilm(Long filmId);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(Long count);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);
}
