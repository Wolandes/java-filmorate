package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.GenreRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {

    private static final String findAllQuery = "SELECT f.*, m.name AS mpa_name FROM Films f JOIN Mpa_Rating m ON f.mpa_rating_id = m.id";

    public JdbcFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return findMany(findAllQuery);
    }

    @Override
    public Film addFilm(Film postFilm) {
        long id = insert("INSERT INTO Films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)", postFilm.getName(), postFilm.getDescription(), postFilm.getReleaseDate(), postFilm.getDuration(), postFilm.getMpa().getId());
        postFilm.setId(id);
        if (postFilm.getGenres() != null) {
            for (Genre genre : postFilm.getGenres()) {
                if (genre.getId() > 0) {
                    insert("INSERT INTO Film_Genre (film_id, genre_id) VALUES (?, ?)", id, genre.getId());
                }
            }
        }
        log.info("Фильм добавлен: " + postFilm);
        return getFilm(id); // Возвращаем фильм с полной информацией
    }

    @Override
    public Film updateFilm(Film putFilm) {
        String updateOneQuery = "UPDATE Films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
        update(updateOneQuery, putFilm.getName(), putFilm.getDescription(), putFilm.getReleaseDate(), putFilm.getDuration(), putFilm.getMpa().getId(), putFilm.getId());

        jdbc.update("DELETE FROM Film_Genre WHERE film_id = ?", putFilm.getId());
        if (putFilm.getGenres() != null) {
            for (Genre genre : putFilm.getGenres()) {
                insert("INSERT INTO Film_Genre (film_id, genre_id) VALUES (?, ?)", putFilm.getId(), genre.getId());
            }
        }
        log.info("Фильм обновлен: " + putFilm);
        return getFilm(putFilm.getId()); // Возвращаем фильм с полной информацией
    }

    @Override
    public Map<Long, Film> getAllFilmsMap() {
        List<Film> films = findMany(findAllQuery);
        return films.stream().collect(Collectors.toMap(Film::getId, film -> film));
    }

    @Override
    public Film getFilm(Long id) {
        // Выполняем запрос для получения данных фильма и его MPA
        Film film = jdbc.queryForObject("SELECT f.*, m.name AS mpa_name " + "FROM Films f " + "JOIN Mpa_Rating m ON f.mpa_rating_id = m.id " + "WHERE f.id = ?", new FilmRowMapper(), id);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        List<Genre> genres = jdbc.query("SELECT g.id, g.name " + "FROM Genre g " + "JOIN Film_Genre fg ON g.id = fg.genre_id " + "WHERE fg.film_id = ?", new GenreRowMapper(), id);
        genres.sort(Comparator.comparingLong(Genre::getId));
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update("INSERT INTO Film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbc.update("DELETE FROM Film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Long> getFilmLikes(Long filmId) {
        return jdbc.queryForList("SELECT user_id FROM Film_likes WHERE film_id = ?", Long.class, filmId);
    }


    @Override
    public boolean isLikeExists(Long filmId, Long userId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM Film_likes WHERE film_id = ? AND user_id = ?", Integer.class, filmId, userId);
        return count != null && count > 0;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbc.query("SELECT f.*, m.name AS mpa_name, COUNT(fl.user_id) AS likes " + "FROM Films f " + "LEFT JOIN Film_likes fl ON f.id = fl.film_id " + "JOIN Mpa_Rating m ON f.mpa_rating_id = m.id " + "GROUP BY f.id " + "ORDER BY likes DESC " + "LIMIT ?", new FilmRowMapper(), count);
    }
}




