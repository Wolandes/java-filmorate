package ru.yandex.practicum.filmorate.dal;

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
public class InMemoryGenreDbStorage extends BaseRepository {

    private static final String findAllQuery = "SELECT * FROM Genre";
    private static final String findOneQuery = "SELECT * FROM Genre WHERE id = ?";

    public InMemoryGenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper){
        super(jdbc,mapper);
    }

    public Collection<Genre> getAllGenres() {
        return findMany(findAllQuery);
    }

    public Genre getGenre(long id){
        Optional<Genre> genreOptinal = findOne(findOneQuery, String.valueOf(id));
        Genre genre = genreOptinal.get();
        if (genre == null) {
            log.info("Нет пользователя с таким id: " + id);
            throw new NotFoundException("Нет пользователя с таким id: " + id);
        }
        return genre;
    }
}
