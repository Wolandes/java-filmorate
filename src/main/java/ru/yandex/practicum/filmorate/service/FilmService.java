package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    final FilmStorage filmStorage;
    final UserStorage userStorage;

    LikeComparator likeComparator = new LikeComparator();

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film postFilm) {
        return filmStorage.addFilm(postFilm);
    }

    public Film updateFilm(Film putFilm) {
        return filmStorage.updateFilm(putFilm);
    }

    public void addLike(Long userId, Long id) {
        Map<Long, Film> allFilmsMap = filmStorage.getAllFilmsMap();
        Film film = allFilmsMap.get(id);
        if (film == null) {
            throw new NotFoundException("Нет фильма с таким id: " + id);
        }
        Map<Long, User> users = userStorage.getCollectionAllUsers();
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Нет пользователя с таким id: " + user);
        }
        Set<Long> filmLikes = film.getIdUserLikes();
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        for (Long filmLike : filmLikes) {
            if (filmLike == userId) {
                log.info("Пользователь с id: " + userId + "уже ставил лайк");
                return;
            }
        }
        filmLikes.add(userId);
        film.setIdUserLikes(filmLikes);
        allFilmsMap.put(film.getId(), film);
        filmStorage.setAllFilmsMap(allFilmsMap);
        log.info("Пользователь c id: " + userId + " поставил лайк");
    }

    public void deleteLike(Long userId, Long id) {
        Map<Long, Film> allFilmsMap = filmStorage.getAllFilmsMap();
        Film film = allFilmsMap.get(id);
        if (film == null) {
            throw new NotFoundException("Нет фильма с таким id: " + id);
        }
        Map<Long, User> users = userStorage.getCollectionAllUsers();
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Нет пользователя с таким id: " + user);
        }
        Set<Long> filmLikes = film.getIdUserLikes();
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        for (Long filmLike : filmLikes) {
            if (filmLike == userId) {
                log.info("Пользователь с id: " + userId + "уже ставил лайк");
                return;
            }
        }
        filmLikes.remove(userId);
        film.setIdUserLikes(filmLikes);
        allFilmsMap.put(film.getId(), film);
        filmStorage.setAllFilmsMap(allFilmsMap);
        log.info("Пользователь c id: " + userId + " удалил лайк");
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> filmSet = filmStorage.getAllFilms();
        TreeSet<Film> filmTreeSet = new TreeSet<>(likeComparator);
        filmTreeSet.addAll(filmSet);
        List<Film> filmList = new ArrayList<>(filmTreeSet);

        if (count > filmList.size()) {
            count = filmList.size();
        }
        return filmList.subList(0, count);
    }

    public Map<Long, Film> getAllMapFilms() {
        return filmStorage.getAllFilmsMap();
    }
}

class LikeComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        int size1 = (film1.getIdUserLikes() != null) ? film1.getIdUserLikes().size() : 0;
        int size2 = (film2.getIdUserLikes() != null) ? film2.getIdUserLikes().size() : 0;

        int comparison = Integer.compare(size2, size1);

        if (comparison == 0) {
            return Long.compare(film1.getId(), film2.getId());
        }

        return comparison;
    }
}
