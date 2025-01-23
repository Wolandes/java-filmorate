package ru.yandex.practicum.filmorate.exctractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

@Component
public class FilmGenreResultSetExtractor implements ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> {
    @Override
    public Map<Long, LinkedHashSet<Genre>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, LinkedHashSet<Genre>> filmGenre = new HashMap<>();
        while (rs.next()) {
            LinkedHashSet<Genre> genres = Optional.ofNullable(filmGenre.get(rs.getLong("film_id")))
                    .orElse(new LinkedHashSet<>());
            genres.add(new Genre(rs.getLong("genre_id"),
                    rs.getString("genre_name")));
            filmGenre.put(rs.getLong("film_id"), genres);
        }
        return filmGenre;
    }
}
