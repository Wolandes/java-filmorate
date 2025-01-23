package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String GET_FILM = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id,
            m.name as mpaa_name, fg.genre_id, g.name as genre_name
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            left outer join public.film_genre fg on fg.film_id = f.id
            left outer join public.genre g on g.id = fg.genre_id
            where f.id = :id
            """;
    private static final String GET_FILMS = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name as mpaa_name
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            """;
    private static final String GET_FILMS_POPULAR = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name as mpaa_name,
            (select count(*) from public.likes l where l.film_id = f.id) as count_likes
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            order by count_likes desc, f.id asc
            limit :count
            """;
    private static final String GET_FILM_GENRE = """
            select fg.film_id, fg.genre_id, g.name as genre_name
            from public.film_genre fg
            inner join public.genre g on g.id = fg.genre_id
            """;
    private static final String INSERT_FILM = """
            insert into public.films (name, description, release_date, duration, mpaa_id)
            values (:name, :description, :release_date, :duration, :mpaa_id)
            """;
    private static final String UPDATE_FILM = """
            update public.films
            set name = :name,
            description = :description,
            release_date = :release_date,
            duration = :duration,
            mpaa_id = :mpaa_id
            where id = :id
            """;
    private static final String INSERT_FILM_GENRE = """
            insert into public.film_genre (film_id, genre_id)
            select :film_id, g.id
            from public.genre g
            left outer join public.film_genre fg on fg.film_id = :film_id and fg.genre_id = g.id
            where g.id in (:genre_ids)
            and fg.genre_id is null
            """;
    private static final String DELETE_FILM_GENRE = """
            delete from public.film_genre
            where film_id = :film_id
            and genre_id not in (:genre_ids)
            """;
    private static final String INSERT_LIKE = """
            merge into public.likes (film_id, user_id)
            values (:film_id, :user_id)
            """;
    private static final String DELETE_LIKE = """
            delete from public.likes
            where film_id = :film_id
            and user_id = :user_id
            """;

    private final NamedParameterJdbcOperations jdbc;
    private final ResultSetExtractor<Film> filmResultSetExtractor;
    private final ResultSetExtractor<List<Film>> filmsResultSetExtractor;
    private final ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> filmGenreResultSetExtractor;

    @Override
    public Film getFilm(Long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", filmId);
        try {
            return jdbc.query(GET_FILM, params, filmResultSetExtractor);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        try {
            List<Film> films = Optional.ofNullable(jdbc.query(GET_FILMS, filmsResultSetExtractor))
                    .orElse(new ArrayList<>());
            Map<Long, LinkedHashSet<Genre>> filmGenre = Optional.ofNullable(
                            jdbc.query(GET_FILM_GENRE, filmGenreResultSetExtractor))
                    .orElse(new HashMap<>());
            return films.stream()
                    .peek(film -> film.setGenres(Optional.ofNullable(filmGenre.get(film.getId()))
                            .orElse(film.getGenres())))
                    .toList();
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public Film createFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpaa_id", film.getMpa().getId());
        try {
            jdbc.update(INSERT_FILM, params, keyHolder, new String[]{"id"});
            Integer id = keyHolder.getKeyAs(Integer.class);
            MapSqlParameterSource paramsFilmGenre = new MapSqlParameterSource();
            paramsFilmGenre.addValue("film_id", id);
            paramsFilmGenre.addValue("genre_ids",
                    film.getGenres().stream()
                            .map(Genre::getId)
                            .toList());
            jdbc.update(INSERT_FILM_GENRE, paramsFilmGenre);
            return getFilm(Long.valueOf(id));
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_FILM_ERROR, film));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpaa_id", film.getMpa().getId());
        try {
            jdbc.update(UPDATE_FILM, params);
            MapSqlParameterSource paramsFilmGenre = new MapSqlParameterSource();
            paramsFilmGenre.addValue("film_id", film.getId());
            paramsFilmGenre.addValue("genre_ids",
                    film.getGenres().stream()
                            .map(Genre::getId)
                            .toList());
            jdbc.update(DELETE_FILM_GENRE, paramsFilmGenre);
            jdbc.update(INSERT_FILM_GENRE, paramsFilmGenre);
            return getFilm(film.getId());
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.UPDATE_FILM_ERROR, film.getId()));
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);
        try {
            List<Film> films = Optional.ofNullable(jdbc.query(GET_FILMS_POPULAR, params, filmsResultSetExtractor))
                    .orElse(new ArrayList<>());
            Map<Long, LinkedHashSet<Genre>> filmGenre = Optional.ofNullable(
                            jdbc.query(GET_FILM_GENRE, filmGenreResultSetExtractor))
                    .orElse(new HashMap<>());
            return films.stream()
                    .peek(film -> film.setGenres(Optional.ofNullable(filmGenre.get(film.getId()))
                            .orElse(film.getGenres())))
                    .toList();
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public void addLike(Film film, User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        params.addValue("user_id", user.getId());
        try {
            jdbc.update(INSERT_LIKE, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_LIKE_ERROR, user.getId(), film.getId()));
        }
    }

    @Override
    public void removeLike(Film film, User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        params.addValue("user_id", user.getId());
        try {
            jdbc.update(DELETE_LIKE, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_LIKE_ERROR, user.getId(), film.getId()));
        }
    }
}
