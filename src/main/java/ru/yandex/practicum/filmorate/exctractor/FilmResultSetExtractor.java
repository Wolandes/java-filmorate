package ru.yandex.practicum.filmorate.exctractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpaa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<Film> {
    @Override
    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
        Film film = null;
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        while (rs.next()) {
            if (film == null) {
                film = new Film(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpaa(rs.getLong("mpaa_id"),
                                rs.getString("mpaa_name")),
                        genres);
            }
            if (rs.getObject("genre_id") != null) {
                genres.add(new Genre(rs.getLong("genre_id"),
                        rs.getString("genre_name")));
            }
        }
        return film;
    }
}
