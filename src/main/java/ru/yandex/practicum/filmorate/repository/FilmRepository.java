package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmRepository {
    Collection<Film> getAllFilms();

    Film addFilm(Film postFilm);

    Film updateFilm(Film putFilm);

    Film getFilm(Long id);

    Map<Long, Film> getAllFilmsMap();

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Long> getFilmLikes(Long filmId);

    boolean isLikeExists(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}
