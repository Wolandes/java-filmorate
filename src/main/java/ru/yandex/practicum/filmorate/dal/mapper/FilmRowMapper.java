package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException{
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_rating_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);
        Set<Genre> genres = new HashSet<>();
        do {
            Integer genreId = resultSet.getInt("genre_id");
            if (genreId != null) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(resultSet.getString("genre_name"));
                genres.add(genre);
            }
        } while (resultSet.next());
        film.setGenres(genres);
        film.setIdUserLikes(new HashSet<>());
        return film;
    }
}
