package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {

    private static final String findAllQuery = "SELECT * FROM Genre";
    private static final String findOneQuery = "SELECT * FROM Genre WHERE id = ?";

    public JdbcGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getAllGenres() {
        return findMany(findAllQuery);
    }

    public Genre getGenre(long id) {
        Optional<Genre> genreOptinal = findOne(findOneQuery, String.valueOf(id));
        if (genreOptinal.isEmpty()) {
            log.info("Нет жанра с таким id: " + id);
            throw new NotFoundException("Нет жанра с таким id: " + id);
        }
        return genreOptinal.get();
    }
}
