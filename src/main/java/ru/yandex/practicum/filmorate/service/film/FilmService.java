package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film getFilm(Long filmId);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void removeFilm(Long filmId);

    List<Film> getPopularFilms(Long count, Long genreId, Integer year);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getFilmsByDirectorId(Long directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
