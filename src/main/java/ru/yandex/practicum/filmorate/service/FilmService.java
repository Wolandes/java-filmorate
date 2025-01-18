package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userStorage;

    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    //
    public Film getFilm(long id) {
        return filmRepository.getFilm(id);
    }

    public Film addFilm(Film postFilm) {
        return filmRepository.addFilm(postFilm);
    }

    public Film updateFilm(Film putFilm) {
        return filmRepository.updateFilm(putFilm);
    }

    public void addLike(Long userId, Long filmId) {
        userStorage.getUser(userId); // Проверка существования пользователя
        filmRepository.getFilm(filmId); // Проверка существования фильма

        if (filmRepository.isLikeExists(filmId, userId)) {
            log.info("Пользователь с id: {} уже ставил лайк фильму с id: {}", userId, filmId);
            return;
        }

        filmRepository.addLike(filmId, userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
    }

    public void deleteLike(Long userId, Long filmId) {
        userStorage.getUser(userId); // Проверка существования пользователя
        filmRepository.getFilm(filmId); // Проверка существования фильма

        if (!filmRepository.isLikeExists(filmId, userId)) {
            log.info("Пользователь с id: {} не ставил лайк фильму с id: {}", userId, filmId);
            return;
        }

        filmRepository.deleteLike(filmId, userId);
        log.info("Пользователь с id: {} удалил лайк фильму с id: {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmRepository.getPopularFilms(count);
    }

    public Map<Long, Film> getAllMapFilms() {
        return filmRepository.getAllFilmsMap();
    }
}
