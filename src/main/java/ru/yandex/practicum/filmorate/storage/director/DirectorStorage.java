package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    Director getDirector(Long directorId);

    List<Director> getDirectors(List<Long> directorIds);

    List<Director> getAllDirectors();

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(Director director);

    void addDirectorsToFilm(Film film);

    void addDirectorsToFilm(List<Film> films);

    long getCountFilmByDirector(Director director);
}
