package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String FIND_DIRECTOR = "select id, name from public.directors where id in (:ids)";
    private static final String FIND_ALL_DIRECTORS = "select id, name from public.directors";
    private static final String INSERT_DIRECTOR = """
            insert into public.directors (name)
            values (:name)
            """;
    private static final String UPDATE_DIRECTOR = """
            update public.directors
            set name = :name
            where id = :id
            """;
    private static final String DELETE_DIRECTOR = """
            delete from public.film_director
            where director_id = :id;
            delete from public.directors
            where id = :id;
            """;
    private static final String GET_FILM_DIRECTOR = """
            select fd.film_id, fd.director_id, d.name as director_name
            from public.film_director fd
            inner join public.directors d on d.id = fd.director_id
            where fd.film_id in (:film_ids)
            """;

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Director> mapper;

    @Override
    public Director getDirector(Long directorId) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("ids", directorId);
            return jdbc.queryForObject(FIND_DIRECTOR, params, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Director> getDirectors(List<Long> directorIds) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("ids", directorIds);
            return jdbc.query(FIND_DIRECTOR, params, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        try {
            return jdbc.query(FIND_ALL_DIRECTORS, new MapSqlParameterSource(), mapper);
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public Director createDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", director.getName());
        try {
            jdbc.update(INSERT_DIRECTOR, params, keyHolder, new String[]{"id"});
            Integer id = keyHolder.getKeyAs(Integer.class);
            return getDirector(Long.valueOf(id));
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_DIRECTOR_ERROR, director));
        }
    }

    @Override
    public Director updateDirector(Director director) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", director.getId());
        params.addValue("name", director.getName());
        try {
            jdbc.update(UPDATE_DIRECTOR, params);
            return getDirector(director.getId());
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.UPDATE_DIRECTOR_ERROR, director.getId()));
        }
    }

    @Override
    public void removeDirector(Director director) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", director.getId());
        try {
            jdbc.update(DELETE_DIRECTOR, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_DIRECTOR_ERROR, director.getId()));
        }
    }

    @Override
    public void addDirectorsToFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_ids", film.getId());
        jdbc.query(GET_FILM_DIRECTOR, params, (rs) -> {
            film.getDirectors().add(makeDirector(rs));
        });
    }

    @Override
    public void addDirectorsToFilm(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_ids", filmIds);
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        jdbc.query(GET_FILM_DIRECTOR, params, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.getDirectors().add(makeDirector(rs));
        });
    }

    static Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(rs.getLong("director_id"),
                rs.getString("director_name"));
    }
}
