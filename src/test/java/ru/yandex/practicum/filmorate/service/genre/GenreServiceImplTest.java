package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreServiceImpl.class})
@ContextConfiguration(classes = {GenreDbStorage.class, GenreRowMapper.class})
public class GenreServiceImplTest {
    private final GenreService genreService;

    static Genre getTestGenre() {
        return new Genre(1L, "Комедия");
    }

    @Test
    @DisplayName("Получить жанр по id")
    public void shouldGetGenre() {
        Genre testGenre = getTestGenre();
        Optional<Genre> GenreOptional = Optional.ofNullable(genreService.getGenre(testGenre.getId()));

        assertThat(GenreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testGenre);
    }

    @Test
    @DisplayName("Получить несколько жанров по id")
    public void shouldGetGenres() {
        List<Genre> testGenre = new ArrayList<>();
        testGenre.add(new Genre(2L, "Драма"));
        testGenre.add(new Genre(3L, "Мультфильм"));

        Optional<List<Genre>> genreOptional = Optional.ofNullable(
                genreService.getGenres(testGenre.stream()
                        .map(Genre::getId)
                        .toList()));

        assertThat(genreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testGenre);
    }

    @Test
    @DisplayName("Список жанров")
    public void shouldGetAllGenres() {
        List<Genre> testGenre = new ArrayList<>();
        testGenre.add(new Genre(1L, "Комедия"));
        testGenre.add(new Genre(2L, "Драма"));
        testGenre.add(new Genre(3L, "Мультфильм"));
        testGenre.add(new Genre(4L, "Триллер"));
        testGenre.add(new Genre(5L, "Документальный"));
        testGenre.add(new Genre(6L, "Боевик"));
        Optional<List<Genre>> genreOptional = Optional.ofNullable(genreService.getAllGenres());

        assertThat(genreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testGenre);
    }
}
