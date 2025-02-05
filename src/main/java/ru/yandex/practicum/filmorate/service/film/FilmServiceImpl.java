package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.SearchBy;
import ru.yandex.practicum.filmorate.storage.film.SortBy;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpaa.MpaaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaaStorage mpaaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    @Override
    public Film getFilm(Long filmId) {
        Film film = getFilmById(filmId);
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
        getFilmById(film.getId());
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
    public void removeFilm(Long filmId) {
        Film film = getFilmById(filmId);
        filmStorage.removeFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(Long count, Long genreId, Integer year) {
        List<Film> films = Optional.ofNullable(filmStorage.getPopularFilms(count, genreId, year))
                .orElse(new ArrayList<>());
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        log.info("Метод getPopularFilms /films вернул ответ {}", films);
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = getUserById(userId);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.ADD)
                .entityId(filmId)
                .build();
        eventStorage.createEvent(event);
        filmStorage.addLike(film, user);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = getUserById(userId);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.REMOVE)
                .entityId(filmId)
                .build();
        eventStorage.createEvent(event);
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

    @Override
    public List<Film> searchFilms(String query, String by) {
        Set<SearchBy> byList = Arrays.stream(by.split(","))
                .filter(b -> !b.isBlank())
                .map(b -> b.toLowerCase().trim())
                .map(SearchBy::from)
                .collect(Collectors.toSet());
        List<Film> films = Optional.ofNullable(filmStorage.searchFilms(query, byList))
                .orElse(new ArrayList<>());
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        return films;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        List<Film> films = Optional.ofNullable(filmStorage.getCommonFilms(user, friend))
                .orElse(new ArrayList<>());
        genreStorage.addGenresToFilm(films);
        directorStorage.addDirectorsToFilm(films);
        return films;
    }

    private Film getFilmById(Long filmId) {
        return Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
    }

    private User getUserById(Long userId) {
        return Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
    }
}
