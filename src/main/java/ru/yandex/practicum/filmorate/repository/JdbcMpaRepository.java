package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class JdbcMpaRepository extends BaseRepository<Mpa> implements MpaRepository {

    private static final String findAllQuery = "SELECT * FROM Mpa_Rating";
    private static final String findOneQuery = "SELECT * FROM Mpa_Rating WHERE id = ?";

    public JdbcMpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Mpa> getAllMpa() {
        return findMany(findAllQuery);
    }

    public Mpa getMpa(long id) {
        Optional<Mpa> genreOptinal = findOne(findOneQuery, String.valueOf(id));
        if (genreOptinal.isEmpty()) {
            log.info("Нет возрастного рейтинга с таким id: " + id);
            throw new NotFoundException("Нет возрастного рейтинга с таким id: " + id);
        }
        return genreOptinal.get();
    }
}
