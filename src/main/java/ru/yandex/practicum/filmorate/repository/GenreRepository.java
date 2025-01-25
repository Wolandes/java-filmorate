package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreRepository {
    public Collection<Genre> getAllGenres();

    public Genre getGenre(long id);
}
