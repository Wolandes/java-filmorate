package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    Genre getGenre(Long genreId);

    List<Genre> getGenres(List<Long> ids);

    List<Genre> getAllGenres();
}
