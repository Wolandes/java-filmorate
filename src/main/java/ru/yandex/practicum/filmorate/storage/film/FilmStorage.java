package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film getFilm(Long filmId);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void removeFilm(Film film);

    List<Film> getPopularFilms(Long count, Long genreId, Integer year);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    List<Film> getFilmsByDirectorId(Director director, SortBy sortBy);

    List<Film> searchFilms(String query, Set<SearchBy> by);

    List<Long> getLikedFilm(Long userId);

    List<Long> getSimilarUser(Long userId, List<Long> likedFilms);

    List<Film> findRecommendations(Long userId, List<Long> likedFilms, Long similarUser);

    List<Film> getCommonFilms(User user, User friend);
}
