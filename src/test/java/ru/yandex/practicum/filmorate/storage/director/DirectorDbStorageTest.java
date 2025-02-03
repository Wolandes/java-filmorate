package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DirectorDbStorage.class})
@ContextConfiguration(classes = {DirectorRowMapper.class})
public class DirectorDbStorageTest {
    private final DirectorStorage directorStorage;

    static Film getTestFilm() {
        return new Film().toBuilder()
                .id(1L)
                .directors(new LinkedHashSet<>())
                .build();
    }

    @Test
    @DisplayName("Список режиссеров по нескольким id")
    public void shouldGetDirectors() {
        List<Director> testDirector = new ArrayList<>();
        testDirector.add(directorStorage.getDirector(2L));
        testDirector.add(directorStorage.getDirector(3L));
        List<Long> ids = testDirector.stream()
                .map(Director::getId)
                .toList();
        Optional<List<Director>> directorOptional = Optional.ofNullable(directorStorage.getDirectors(ids));

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }

    @Test
    @DisplayName("Добавление режиссеров к фильмам")
    public void shouldAddDirectorsToFilm() {
        Set<Director> testDirector = new LinkedHashSet<>();
        testDirector.add(directorStorage.getDirector(1L));
        testDirector.add(directorStorage.getDirector(2L));
        Film testFilm = getTestFilm();
        directorStorage.addDirectorsToFilm(testFilm);
        Optional<Set<Director>> directorOptional = Optional.ofNullable(testFilm.getDirectors());

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);

        testDirector = new LinkedHashSet<>();
        testDirector.add(directorStorage.getDirector(4L));
        List<Film> testFilms = new ArrayList<>();
        testFilms.add(getTestFilm());
        testFilms.add(getTestFilm().toBuilder().id(2L).build());
        directorStorage.addDirectorsToFilm(testFilms);
        directorOptional = Optional.ofNullable(testFilms.get(1).getDirectors());

        assertThat(directorOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testDirector);
    }
}
