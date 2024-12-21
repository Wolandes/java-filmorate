package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUNT_ERROR, film.getId()));
        }
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUNT_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUNT_ERROR, userId)));
        filmStorage.addLike(film, user);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUNT_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUNT_ERROR, userId)));
        filmStorage.removeLike(film, user);
    }
}
