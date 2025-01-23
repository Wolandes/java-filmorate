package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceDbImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public Genre getGenre(Long genreId) {
        return Optional.ofNullable(genreStorage.getGenre(genreId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.GENRE_NOT_FOUND_ERROR, genreId)));
    }

    @Override
    public List<Genre> getGenres(List<Long> ids) {
        return Optional.ofNullable(genreStorage.getGenres(ids))
                .orElse(new ArrayList<>());
    }

    @Override
    public List<Genre> getAllGenres() {
        return Optional.ofNullable(genreStorage.getAllGenres())
                .orElse(new ArrayList<>());
    }
}
