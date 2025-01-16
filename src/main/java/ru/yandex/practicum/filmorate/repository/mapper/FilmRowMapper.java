package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private JdbcTemplate jdbc = new JdbcTemplate();

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException{
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("releaseDate").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_rating_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);
        List<Genre> genres = jdbc.query("SELECT g.id, g.name FROM Genre g " +
                        "JOIN Film_Genre fg ON g.id = fg.genre_id WHERE fg.film_id = ?",
                new GenreRowMapper(), film.getId());
        film.setGenres(new HashSet<>(genres));

        return film;
    }
}
