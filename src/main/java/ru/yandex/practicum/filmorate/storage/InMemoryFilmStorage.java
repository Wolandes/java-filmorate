package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    Map<Long, Film> allFilms = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return allFilms.values();
    }

    @Override
    public Film addFilm(Film postFilm) {
        long id = getNextId();
        postFilm.setId(id);
        allFilms.put(postFilm.getId(), postFilm);
        log.info("Фильм добавлен в коллекцию: " + postFilm);
        return postFilm;
    }

    @Override
    public Film updateFilm(Film putFilm){
        allFilms.put(putFilm.getId(),putFilm);
        log.info("Фильм обновлен в коллекции: " + putFilm);
        return putFilm;
    }

    private long getNextId() {
        long currentMaxId = allFilms.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Map<Long, Film> getCollectionAllFilms(){
        return allFilms;
    }
}
