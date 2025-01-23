package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_GENRE = "select id, name from public.genre where id in (:ids)";
    private static final String ALL_GENRE = "select id, name from public.genre";

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Genre> mapper;

    @Override
    public Genre getGenre(Long genreId) throws DataAccessException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("ids", genreId);
            return jdbc.queryForObject(FIND_GENRE, params, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Genre> getGenres(List<Long> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        try {
            return Optional.ofNullable(jdbc.query(FIND_GENRE, params, mapper))
                    .orElse(new ArrayList<>());
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        try {
            return Optional.ofNullable(jdbc.query(ALL_GENRE, new MapSqlParameterSource(), mapper))
                    .orElse(new ArrayList<>());
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }
}
