package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film addFilm(Film postFilm);

    Film updateFilm(Film putFilm);

    Film addLike(Integer id);

    Film deleteLike(Integer id);

    Collection<Film> getPopularFilms();
}