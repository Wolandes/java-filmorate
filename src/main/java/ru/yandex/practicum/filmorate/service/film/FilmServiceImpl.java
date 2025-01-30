package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.SortBy;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpaa.MpaaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaaStorage mpaaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film getFilm(Long filmId) {
        Film film = Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        genreStorage.addGenresToFilm(film);
        directorStorage.addDirectorsToFilm(film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = Optional.ofNullable(filmStorage.getAllFilms())
                .orElse(new ArrayList<>());
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        if (mpaaStorage.getMpaa(film.getMpa().getId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.MPAA_NOT_FOUND_ERROR, film.getMpa().getId()));
        }
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }
        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .toList();
        if (genreStorage.getGenres(genreIds).size() != genreIds.size()) {
            throw new NotFoundException(String.format(ExceptionMessages.GENRE_NOT_FOUND_FROM_LIST_ERROR, genreIds));
        }
        if (film.getDirectors() == null) {
            film.setDirectors(new LinkedHashSet<>());
        }
        List<Long> directorIds = film.getDirectors().stream()
                .map(Director::getId)
                .toList();
        if (directorStorage.getDirectors(directorIds).size() != directorIds.size()) {
            throw new NotFoundException(String.format(ExceptionMessages.DIRECTOR_NOT_FOUND_FROM_LIST_ERROR, directorIds));
        }
        Film newFilm = Optional.ofNullable(filmStorage.createFilm(film))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.INSERT_FILM_ERROR, film)));
        genreStorage.addGenresToFilm(newFilm);
        directorStorage.addDirectorsToFilm(newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, film.getId()));
        }
        if (mpaaStorage.getMpaa(film.getMpa().getId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.MPAA_NOT_FOUND_ERROR, film.getMpa().getId()));
        }
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }
        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .toList();
        if (genreStorage.getGenres(genreIds).size() != genreIds.size()) {
            throw new NotFoundException(String.format(ExceptionMessages.GENRE_NOT_FOUND_FROM_LIST_ERROR, genreIds));
        }
        if (film.getDirectors() == null) {
            film.setDirectors(new LinkedHashSet<>());
        }
        List<Long> directorIds = film.getDirectors().stream()
                .map(Director::getId)
                .toList();
        if (directorStorage.getDirectors(directorIds).size() != directorIds.size()) {
            throw new NotFoundException(String.format(ExceptionMessages.DIRECTOR_NOT_FOUND_FROM_LIST_ERROR, directorIds));
        }
        Film newFilm = Optional.ofNullable(filmStorage.updateFilm(film))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.INSERT_FILM_ERROR, film)));
        genreStorage.addGenresToFilm(newFilm);
        directorStorage.addDirectorsToFilm(newFilm);
        return newFilm;
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        List<Film> films = Optional.ofNullable(filmStorage.getPopularFilms(count))
                .orElse(new ArrayList<>());
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        filmStorage.addLike(film, user);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        filmStorage.removeLike(film, user);
    }

    @Override
    public List<Film> getFilmsByDirectorId(Long directorId, String sortBy) {
        SortBy sort = SortBy.from(sortBy);
        Director director = Optional.ofNullable(directorStorage.getDirector(directorId))
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.DIRECTOR_NOT_FOUND_ERROR, directorId)));
        List<Film> films = Optional.ofNullable(filmStorage.getFilmsByDirectorId(director, sort))
                .orElse(new ArrayList<>());
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        return films;
    }
}
