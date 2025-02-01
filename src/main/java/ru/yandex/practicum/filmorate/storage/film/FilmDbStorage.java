package ru.yandex.practicum.filmorate.storage.film;

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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String GET_FILM = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name as mpaa_name
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            where f.id = :id
            """;
    private static final String GET_FILMS = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name as mpaa_name
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            order by f.id
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
    private static final String INSERT_FILM_DIRECTOR = """
            insert into public.film_director (film_id, director_id)
            select :film_id, d.id
            from public.directors d
            left outer join public.film_director fd on fd.film_id = :film_id and fd.director_id = d.id
            where d.id in (:director_ids)
            and fd.director_id is null
            """;
    private static final String DELETE_FILM_DIRECTOR = """
            delete from public.film_director
            where film_id = :film_id
            and director_id not in (:director_ids)
            """;
    private static final String GET_FILMS_BY_DIRECTOR = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name as mpaa_name,
            (select count(*) from public.likes l where l.film_id = f.id) as count_likes
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            where exists(select fd.director_id
            from public.film_director fd
            where fd.film_id = f.id and fd.director_id = :director_id)
            %s
            """;
    private static final String SEARCH_FILMS = """
            select f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name as mpaa_name,
            (select count(*) from public.likes l where l.film_id = f.id) as count_likes
            from public.films f
            inner join public.mpaa m on m.id = f.mpaa_id
            where %s
            """;
    private static final String DELETE_FILM = """
            delete from public.likes
            where film_id = :id;
            delete from public.film_genre
            where film_id = :id;
            delete from public.film_director
            where film_id = :id;
            delete from public.films
            where id = :id;
            """;

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Film> filmRowMapper;

    @Override
    public Film getFilm(Long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", filmId);
        try {
            return jdbc.queryForObject(GET_FILM, params, filmRowMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        try {
            return jdbc.query(GET_FILMS, filmRowMapper);
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
            MapSqlParameterSource paramsFilmDirector = new MapSqlParameterSource();
            paramsFilmDirector.addValue("film_id", id);
            paramsFilmDirector.addValue("director_ids",
                    film.getDirectors().stream()
                            .map(Director::getId)
                            .toList());
            jdbc.update(INSERT_FILM_DIRECTOR, paramsFilmDirector);
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
            MapSqlParameterSource paramsFilmDirector = new MapSqlParameterSource();
            paramsFilmDirector.addValue("film_id", film.getId());
            paramsFilmDirector.addValue("director_ids",
                    film.getDirectors().stream()
                            .map(Director::getId)
                            .toList());
            jdbc.update(DELETE_FILM_DIRECTOR, paramsFilmDirector);
            jdbc.update(INSERT_FILM_DIRECTOR, paramsFilmDirector);
            return getFilm(film.getId());
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.UPDATE_FILM_ERROR, film.getId()));
        }
    }

    @Override
    public void removeFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        try {
            jdbc.update(DELETE_FILM, params);
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.DELETE_FILM_ERROR);
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count, Long genreId, Integer year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_id, m.name AS mpaa_name,
                (SELECT COUNT(*) FROM public.likes l WHERE l.film_id = f.id) AS count_likes
                FROM public.films f
                INNER JOIN public.mpaa m ON m.id = f.mpaa_id
                LEFT JOIN public.film_genre fg ON f.id = fg.film_id
                LEFT JOIN public.genre g ON fg.genre_id = g.id
                LEFT JOIN public.film_director fd ON f.id = fd.film_id
                LEFT JOIN public.directors d ON fd.director_id = d.id
                """);

        if (genreId != null) {
            sql.append("WHERE fg.genre_id = :genreId ");
            params.addValue("genreId", genreId);
        }

        if (year != null) {
            if (genreId != null) {
                sql.append("AND EXTRACT(YEAR FROM f.release_date) = :year ");
            } else {
                sql.append("WHERE EXTRACT(YEAR FROM f.release_date) = :year ");
            }
            params.addValue("year", year);
        }
        sql.append("ORDER BY count_likes DESC, f.id ASC ");
        sql.append("LIMIT :count ");
        params.addValue("count", count);
        List<Film> films = jdbc.query(sql.toString(), params, filmRowMapper);
        return films;
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

    @Override
    public List<Film> getFilmsByDirectorId(Director director, SortBy sortBy) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", director.getId());
        try {
            return jdbc.query(String.format(GET_FILMS_BY_DIRECTOR, getOrderByToFilmsByDirectorId(sortBy)), params, filmRowMapper);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.SELECT_ERROR));
        }
    }

    static String getOrderByToFilmsByDirectorId(SortBy sortBy) {
        return switch (sortBy) {
            case YEAR -> "order by f.release_date";
            case LIKES -> "order by count_likes desc";
        };
    }

    @Override
    public List<Film> searchFilms(String query, Set<SearchBy> by) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("query", query);
        try {
            return jdbc.query(String.format(SEARCH_FILMS, getWhereToSearchFilms(by)), params, filmRowMapper);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.SELECT_ERROR));
        }
    }

    static String getWhereToSearchFilms(Set<SearchBy> by) {
        return by.stream()
                .map(b -> {
                    return switch (b) {
                        case DIRECTOR -> """
                                exists(select fd.director_id
                                from public.film_director fd
                                inner join public.directors d on d.id = fd.director_id
                                where fd.film_id = f.id and d.name like '%' || :query || '%')
                                """;
                        case TITLE -> "f.name like '%' || :query || '%'";
                    };
                })
                .collect(Collectors.joining(" or "));
    }
}
