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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_GENRE = "select id, name from public.genre where id in (:ids)";
    private static final String ALL_GENRE = "select id, name from public.genre";
    private static final String GET_FILM_GENRE = """
            select fg.film_id, fg.genre_id, g.name as genre_name
            from public.film_genre fg
            inner join public.genre g on g.id = fg.genre_id
            where fg.film_id in (:film_ids)
            """;

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


    @Override
    public void addGenresToFilm(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_ids", filmIds);
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        jdbc.query(GET_FILM_GENRE, params, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.getGenres().add(makeGenre(rs));
        });
    }

    @Override
    public void addGenresToFilm(Film film) {
        List<Long> filmIds = new ArrayList<>();
        filmIds.add(film.getId());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_ids", filmIds);
        jdbc.query(GET_FILM_GENRE, params, (rs) -> {
            film.getGenres().add(makeGenre(rs));
        });
    }

    static Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getLong("genre_id"),
                rs.getString("name"));
    }
}
