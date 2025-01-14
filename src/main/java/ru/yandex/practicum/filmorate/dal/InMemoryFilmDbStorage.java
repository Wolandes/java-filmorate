package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmDbStorage extends BaseRepository implements FilmDbStorage {

    private static final String findAllQuery = "SELECT * FROM Film";
    private static final String addOneQuery = "INSERT INTO Film (name, description, releaseDate, duration, idUserLikes, genres_id, mpa_rating_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String findOneQuery = "SELECT * FROM Film WHERE id = ?";
    private static final String updateOneQuery = "UPDATE Film SET name = ?, description = ?, releaseDate = ?, duration = ?, idUserLikes = ?, genres = ?, mpa = ? WHERE id = ?";

    private Map<Long, Film> allFilms = new HashMap<>();

    public InMemoryFilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return findMany(findAllQuery);
    }

    @Override
    public Film addFilm(Film postFilm) {
        long id = insert(addOneQuery, postFilm.getName(), postFilm.getDuration(), postFilm.getReleaseDate(), postFilm.getDuration(), postFilm.getIdUserLikes(), postFilm.getGenres(), postFilm.getMpa());
        postFilm.setId(id);
        allFilms.put(postFilm.getId(), postFilm);
        log.info("Фильм добавлен в коллекцию: " + postFilm);
        return postFilm;
    }

    @Override
    public Film updateFilm(Film putFilm) {
        update(updateOneQuery, putFilm.getName(), putFilm.getDuration(), putFilm.getReleaseDate(), putFilm.getDuration(), putFilm.getIdUserLikes(), putFilm.getGenres(), putFilm.getMpa(), putFilm.getId());
        log.info("Фильм обновлен в коллекции: " + putFilm);
        return putFilm;
    }

    @Override
    public void setAllFilmsMap(Map<Long, Film> allFilmsMap) {
        allFilms = allFilmsMap;
    }

    @Override
    public Map<Long, Film> getAllFilmsMap() {
        List<Film> films = findMany(findAllQuery);
        return films.stream().collect(Collectors.toMap(Film::getId, film -> film));
    }

    @Override
    public Film getFilm(Long id) {
        Optional<Film> filmOptional = findOne(findOneQuery, mapper, id);
        Film film = filmOptional.get();
        return film;
    }
}




